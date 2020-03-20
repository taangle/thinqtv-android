package com.thinqtv.thinqtv_android.data;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.thinqtv.thinqtv_android.R;
import com.thinqtv.thinqtv_android.data.model.LoggedInUser;
import com.thinqtv.thinqtv_android.ui.login.LoggedInUserView;
import com.thinqtv.thinqtv_android.ui.login.LoginActivity;
import com.thinqtv.thinqtv_android.ui.login.LoginResult;
import com.thinqtv.thinqtv_android.ui.login.LoginViewModel;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginDataSource {
    private String url = "https://fast-mountain-02267.herokuapp.com/api/v1/users/sign_in.json";
    public void login(String email, String password, Context context, LoginListener listener, LoginViewModel loginViewModel, LoginRepository loginRepository) {
        JSONObject userLogin = new JSONObject();
        try {
            JSONObject loginParams = new JSONObject();
            loginParams.put("email", email);
            loginParams.put("password", password);
            userLogin.put("user", loginParams);
        } catch(Exception e) {
            e.printStackTrace();
        }
        try {
            RequestQueue queue = Volley.newRequestQueue(context);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    url, userLogin,
                    new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("JSONPost", response.toString());
                    //JSONObject user = response.getJSONObject("user");
                    try {
                        LoggedInUser user = new LoggedInUser(response.getString("name"), response.getString("token"));
                        loginRepository.setLoggedInUser(user);
                        loginViewModel.setLoginResult(new LoginResult(new LoggedInUserView(user.getName())));
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.networkResponse.statusCode == 401) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {
                            String jsonResponse = new String(networkResponse.data);
                            Log.d("login failed: ", jsonResponse);
                        }
                        else {
                            Log.d("login failed: ", error.networkResponse.toString());
                        }
                        loginViewModel.setLoginResult(new LoginResult(R.string.login_failed));
                    }
                    else {
                        Log.d("JSONPost Error", error.toString());
                        loginViewModel.setLoginResult(new LoginResult(R.string.could_not_reach_server));
                    }

                }
            });
            queue.add(request);
            /*LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Doe");
            return new Result.Success<>(fakeUser);*/
        } catch (Exception e) {
            return; // new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
