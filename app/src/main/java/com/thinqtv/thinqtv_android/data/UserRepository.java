package com.thinqtv.thinqtv_android.data;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.thinqtv.thinqtv_android.R;
import com.thinqtv.thinqtv_android.data.model.LoggedInUser;
import com.thinqtv.thinqtv_android.ui.auth.LoggedInUserView;
import com.thinqtv.thinqtv_android.ui.auth.LoginResult;
import com.thinqtv.thinqtv_android.ui.auth.LoginViewModel;
import com.thinqtv.thinqtv_android.ui.auth.RegisterViewModel;

import org.json.JSONException;
import org.json.JSONObject;

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
            loginViewModel.setLoginResult(new LoginResult(R.string.could_not_reach_server));
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                DataSource.getServerUrl() + loginUrl, userLogin,
                response -> {
                    try {
                        setLoggedInUser(new LoggedInUser(response.getString("name"), response.getString("token")));
                        loginViewModel.setLoginResult(new LoginResult(new LoggedInUserView(user.getName())));
                    } catch(JSONException e) {
                        e.printStackTrace();
                        loginViewModel.setLoginResult(new LoginResult(R.string.login_failed));
                    }
                }, error -> {
                    if (error.networkResponse != null && error.networkResponse.statusCode == 401) { // Email or password was wrong.
                        loginViewModel.setLoginResult(new LoginResult(R.string.login_failed));
                    } else {
                        loginViewModel.setLoginResult(new LoginResult(R.string.could_not_reach_server));
                    }
                });

        DataSource.getInstance().addToRequestQueue(request, context);
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
            registerViewModel.setLoginResult(new LoginResult(R.string.could_not_reach_server));
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                DataSource.getServerUrl() + registerUrl, userRegister,
                response -> {
                    try {
                        setLoggedInUser(new LoggedInUser(response.getString("name"), response.getString("token")));
                        registerViewModel.setLoginResult(new LoginResult(new LoggedInUserView(user.getName())));
                    } catch(JSONException e) {
                        e.printStackTrace();
                        registerViewModel.setLoginResult(new LoginResult(R.string.login_failed));
                    }
                }, error -> {
                    if (error.networkResponse.statusCode == 401) { // There was a problem with one of the user-provided inputs.
                        if (error.networkResponse.data != null) {
                            String response = new String(error.networkResponse.data);
                            // req went through, prob with email or pass.
                        }
                        registerViewModel.setLoginResult(new LoginResult(R.string.login_failed));
                    } else {
                        registerViewModel.setLoginResult(new LoginResult(R.string.could_not_reach_server));
                    }
                });
        DataSource.getInstance().addToRequestQueue(request, context);
    }
}