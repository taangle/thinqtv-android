package com.thinqtv.thinqtv_android.data;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.thinqtv.thinqtv_android.R;
import com.thinqtv.thinqtv_android.data.model.LoggedInUser;
import com.thinqtv.thinqtv_android.ui.login.LoggedInUserView;
import com.thinqtv.thinqtv_android.ui.login.LoginResult;
import com.thinqtv.thinqtv_android.ui.login.LoginViewModel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;
    private LoggedInUser user = null;
    private final String loginUrl = "api/v1/users/sign_in.json";

    private LoginRepository() {
    }

    public static LoginRepository getInstance() {
        if (instance == null) {
            instance = new LoginRepository();
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

    public void login(String email, String password, Context context, LoginViewModel loginViewModel) {
        JSONObject userLogin = new JSONObject();
        JSONObject loginParams = new JSONObject();
        try {
            loginParams.put("email", email);
            loginParams.put("password", password);
            userLogin.put("user", loginParams);
        } catch(JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                DataSource.getServerUrl() + loginUrl, userLogin,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    setLoggedInUser(new LoggedInUser(response.getString("name"), response.getString("token")));
                } catch(JSONException e) {
                    e.printStackTrace();
                }
                loginViewModel.setLoginResult(new LoginResult(new LoggedInUserView(user.getName())));
            }

            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null && error.networkResponse != null && error.networkResponse.statusCode == 401) {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String response = new String(networkResponse.data);
                        // req went through, prob with email or pass.
                    }
                    loginViewModel.setLoginResult(new LoginResult(R.string.login_failed));
                } else {
                    loginViewModel.setLoginResult(new LoginResult(R.string.could_not_reach_server));
                }
            }
        });

        DataSource.getInstance(context).addToRequestQueue(request);
    }
}