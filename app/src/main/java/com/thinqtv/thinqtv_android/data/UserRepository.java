package com.thinqtv.thinqtv_android.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.thinqtv.thinqtv_android.R;
import com.thinqtv.thinqtv_android.StartupLoadingActivity;
import com.thinqtv.thinqtv_android.data.model.LoggedInUser;
import com.thinqtv.thinqtv_android.ui.auth.LoginViewModel;
import com.thinqtv.thinqtv_android.ui.auth.RegisterViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and information about the logged-in user. Also
 * requests registration of new users.
 */
public class UserRepository {

    private static volatile UserRepository instance;
    private LoggedInUser user = null;

    private UserRepository() {
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

    private void setLoggedInUser(LoggedInUser user) {
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
        final String loginUrl = "api/v1/users/sign_in.json";
        JSONObject userLogin = new JSONObject();
        JSONObject loginParams = new JSONObject();
        try {
            loginParams.put("email", email);
            loginParams.put("password", password);
            userLogin.put("user", loginParams);
        } catch(JSONException e) { // Couldn't form JSON object for request.
            e.printStackTrace();
            loginViewModel.setResult(new Result<>(R.string.could_not_reach_server, false));
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                DataSource.getServerUrl() + loginUrl, userLogin,
                response -> {
                    try {
                        setLoggedInUser(new LoggedInUser(context, response.getString("name"), response.getString("token"), response.getString("permalink"), response.getString("email")));
                        loginViewModel.setResult(new Result<>(null, true));
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

        DataSource.getInstance().addToRequestQueue(request, context);
    }

    public void logout() {
        getLoggedInUser().logout();
        setLoggedInUser(null);
    }

    /**
     * Takes in the information provided by the user and creates a request to create a new user.
     * The result is sent back to the login view model. If the registration was successful, the
     * logged-in user is updated.
     */
    public void register(String email, String name, String permalink, String password,
                         Context context, RegisterViewModel registerViewModel) {
        final String registerUrl = "api/v1/users";
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
                DataSource.getServerUrl() + registerUrl, userRegister,
                response -> {
                    try {
                        setLoggedInUser(new LoggedInUser(context, response.getString("name"), response.getString("token"), response.getString("permalink"), response.getString("email")));
                        registerViewModel.setResult(new Result<>(null, true));
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
        DataSource.getInstance().addToRequestQueue(request, context);
    }

    public void loadSavedUser(StartupLoadingActivity activity) {
        final String loginUrl = "api/v1/users/sign_in.json";
        Context context = (Context)activity;

        SharedPreferences pref = context.getSharedPreferences("ACCOUNT", MODE_PRIVATE);
        String email = pref.getString("email", null);
        String authToken = pref.getString("token", null);

        JSONObject userLogin = new JSONObject();
        JSONObject loginParams = new JSONObject();
        try {
            loginParams.put("email", email);
            loginParams.put("token", authToken);
            userLogin.put("user", loginParams);
        } catch(JSONException e) { // Couldn't form JSON object for request.
            e.printStackTrace();
            activity.finish();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                DataSource.getServerUrl() + loginUrl, userLogin,
                response -> {
                    try {
                        setLoggedInUser(new LoggedInUser(context, response.getString("name"), response.getString("token"), response.getString("permalink"), response.getString("email")));
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                    activity.finish();
                }, error -> {
            activity.finish();
        });

        DataSource.getInstance().addToRequestQueue(request, context);
    }
}