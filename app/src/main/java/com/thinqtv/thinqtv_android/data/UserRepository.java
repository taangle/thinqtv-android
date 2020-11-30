package com.thinqtv.thinqtv_android.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.stripe.android.CustomerSession;
import com.stripe.android.EphemeralKeyUpdateListener;
import com.thinqtv.thinqtv_android.R;
import com.thinqtv.thinqtv_android.StartupLoadingActivity;
import com.thinqtv.thinqtv_android.data.model.LoggedInUser;
import com.thinqtv.thinqtv_android.stripe.StripeEphemeralKeyProvider;
import com.thinqtv.thinqtv_android.ui.auth.LoginViewModel;
import com.thinqtv.thinqtv_android.ui.auth.RegisterViewModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and information about the logged-in user. Also
 * requests registration of new users.
 */
public class UserRepository {

    private static volatile UserRepository instance;
    private LoggedInUser user = null;
    private DataSource dataSource;

    private UserRepository() {
        setDataSource(DataSource.getInstance());
    }

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void setLoggedInUser(LoggedInUser user) {
        this.user = user;
    }

    public LoggedInUser getLoggedInUser() {
        return user;
    }

    /**
     * Takes in the login credentials entered by the user and creates a request for the server.
     * The result is sent back to the login view model. Also, if the login was successful, the
     * logged-in user is updated.
     */
    public void login(String email, String password, Context context, LoginViewModel loginViewModel) {
        final String loginUrl = "api/v1/users/login";
        JSONObject loginParams = new JSONObject();
        try {
            loginParams.put("email", email);
            loginParams.put("password", password);
        } catch(JSONException e) { // Couldn't form JSON object for request.
            e.printStackTrace();
            loginViewModel.setResult(new Result<>(R.string.could_not_reach_server, false));
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                context.getResources().getString(R.string.login_url), loginParams,
                response -> {
                    try {
                        JSONObject user = new JSONObject(response.getString("user"));
                        user.put("token", response.getString("token"));
                        setLoggedInUser(new LoggedInUser(context, new Gson().fromJson(user.toString(), HashMap.class)));
                        loginViewModel.setResult(new Result<>(null, true));
                        getEphemeralKey(context);
                    } catch(JSONException e) {
                        e.printStackTrace();
                        loginViewModel.setResult(new Result<>(R.string.login_failed, false));
                    }
                }, error -> {
                    if (error.networkResponse != null && error.networkResponse.statusCode == 401) { // Email or password was wrong.
                        loginViewModel.setResult(new Result<>(R.string.login_failed, false));
                    } else {
                        loginViewModel.setResult(new Result<>(R.string.could_not_reach_server, false));
                    }
                });

        dataSource.addToRequestQueue(request, context);
    }

    public void logout(Context context) {
        getLoggedInUser().logout();
        setLoggedInUser(null);
        CustomerSession.endCustomerSession();
        getEphemeralKey(context);
    }

    /**
     * Takes in the information provided by the user and creates a request to create a new user.
     * The result is sent back to the login view model. If the registration was successful, the
     * logged-in user is updated.
     */
    public void register(String email, String name, String permalink, String password,
                         Context context, RegisterViewModel registerViewModel) {
        JSONObject userRegister = new JSONObject();
        JSONObject registerParams = new JSONObject();
        try {
            registerParams.put("email", email);
            registerParams.put("name", name);
            registerParams.put("permalink", permalink);
            registerParams.put("password", password);
            userRegister.put("user", registerParams);
        } catch(JSONException e) { // Couldn't form JSON object for request.
            e.printStackTrace();
            registerViewModel.setResult(new Result<>(R.string.could_not_reach_server, false));
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                context.getResources().getString(R.string.register_url), userRegister,
                response -> {
                    try {
                        JSONObject user = new JSONObject(response.getString("user"));
                        user.put("token", response.getString("token"));
                        setLoggedInUser(new LoggedInUser(context, new Gson().fromJson(user.toString(), HashMap.class)));
                        registerViewModel.setResult(new Result<>(null, true));
                        getEphemeralKey(context);
                    } catch(JSONException e) {
                        e.printStackTrace();
                        registerViewModel.setResult(new Result<>(R.string.server_response_error, false));
                    }
                }, error -> {
                    if (error.networkResponse != null) { // There was a problem with one of the user-provided inputs.
                        if (error.networkResponse.statusCode == 422 && error.networkResponse.data != null) {
                            List<Integer> errorMessages = new ArrayList<>();
                            // The server should have sent a list of errors
                            try {
                                JSONObject response = new JSONObject(new String(error.networkResponse.data));
                                JSONObject errors = response.getJSONObject("errors");
                                JSONArray errorArray = errors.names();
                                if (errorArray == null) {
                                    registerViewModel.setResult(new Result<>(R.string.server_response_error, false));
                                    return;
                                }
                                for (int i = 0; i < errorArray.length(); i++) {
                                    switch(errorArray.get(i).toString()) {
                                        case "email":
                                            errorMessages.add(R.string.email_taken);
                                            break;
                                        case "permalink":
                                            errorMessages.add(R.string.permalink_taken);
                                            break;
                                        default:
                                            errorMessages.add(R.string.generic_input_error);
                                            break;
                                    }
                                }
                                registerViewModel.setResult(new Result<>(errorMessages, false));

                            } catch (JSONException e) {
                                registerViewModel.setResult(new Result<>(R.string.server_response_error, false));
                            }

                        }
                        else {
                            registerViewModel.setResult(new Result<>(R.string.register_failed, false));
                        }
                    } else {
                        registerViewModel.setResult(new Result<>(R.string.could_not_reach_server, false));
                    }
                });
        dataSource.addToRequestQueue(request, context);
    }

    public void loadSavedUser(StartupLoadingActivity activity) {

        SharedPreferences pref = activity.getSharedPreferences("ACCOUNT", MODE_PRIVATE);
        String authToken = pref.getString("token", null);

        if (authToken == null) {
            getEphemeralKey(activity);
            activity.finish();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                activity.getResources().getString(R.string.users_url), new JSONObject(), response -> {
            try {
                response.put("token", authToken);
                setLoggedInUser(new LoggedInUser(activity, new Gson().fromJson(response.toString(), HashMap.class)));
                getEphemeralKey(activity);
            } catch(JSONException e) {
                e.printStackTrace();
            }
            activity.finish();
        }, error -> {
            getEphemeralKey(activity);
            activity.finish();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };

        dataSource.addToRequestQueue(request, activity);
    }

    public void updateAccount(Context context, String email, String newPassword,
                       String newPasswordConfirm, String permalink) {
        JSONObject params = new JSONObject();
        try {
            if (!email.equals("")) {
                params.put("email", email);
            }
            if (!newPassword.equals("") && !newPasswordConfirm.equals("")) {
                params.put("password", newPassword);
                params.put("password_confirmation", newPasswordConfirm);
            }
            if (!permalink.equals("")) {
                params.put("permalink", permalink);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = context.getResources().getString(R.string.users_url) + "/" + getLoggedInUser().getUserInfo().get("permalink");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, params, response -> {
            HashMap<String, String> updateParams = new Gson().fromJson(response.toString(), HashMap.class);
            if (!email.equals("")) {
                updateParams.put("email", email);
            }
            getLoggedInUser().updateUserInfo(updateParams);
            ((Activity)context).finish();
            }, error -> {
            error.printStackTrace();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + getLoggedInUser().getUserInfo().get("token"));
                return headers;
            }
        };
        dataSource.addToRequestQueue(request, context);
    }
    public void updateProfile(Context context, String name, ImageView profilePic, String about, String topic1,
                       String topic2, String topic3, ImageView bannerPic) {
        String url = context.getResources().getString(R.string.users_url) + "/" + getLoggedInUser().getUserInfo().get("permalink");
        VolleyMultipartRequest request = new VolleyMultipartRequest(Request.Method.PUT, url,
                response -> {
                    getLoggedInUser().updateUserInfo(new Gson().fromJson(new String(response.data), HashMap.class));
                    ((Activity)context).finish();
                }, error -> {
                    NetworkResponse response = error.networkResponse;
                    String errorMessage = "Unknown error";
                }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                if (!name.equals("")) {
                    params.put("name", name);
                }
                if (!about.equals("")) {
                    params.put("about", about);
                }
                if (!topic1.equals("")) {
                    params.put("genre1", topic1);
                }
                if (!topic2.equals("")) {
                    params.put("genre2", topic2);
                }
                if (!topic3.equals("")) {
                    params.put("genre3", topic3);
                }
                return params;
            }
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + getLoggedInUser().getUserInfo().get("token"));
                return headers;
            }
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                Random rand = new Random();
                if (profilePic.getDrawable() != null && ((BitmapDrawable)profilePic.getDrawable()).getBitmap() != null) {
                    params.put("profilepic", new DataPart("profile_pic" + rand.nextInt(10000) + ".jpeg", getFileDataFromDrawable(context, profilePic.getDrawable()), "image/jpeg"));
                }
                if (bannerPic.getDrawable() != null && ((BitmapDrawable)bannerPic.getDrawable()).getBitmap() != null) {
                    params.put("bannerpic", new DataPart("banner_pic" + rand.nextInt(10000) + ".jpeg", getFileDataFromDrawable(context, bannerPic.getDrawable()), "image/jpeg"));
                }
                return params;
            }
        };
        dataSource.addToRequestQueue(request, context);
    }

    public void getEphemeralKey(Context context) {
        CustomerSession.initCustomerSession(context, new StripeEphemeralKeyProvider(context));
    }

    public static byte[] getFileDataFromDrawable(Context context, int id) {
        Drawable drawable = ContextCompat.getDrawable(context, id);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    public static byte[] getFileDataFromDrawable(Context context, Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}