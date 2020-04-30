package com.thinqtv.thinqtv_android.data.model;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class LoggedInUser {

    private final String name;
    private String authToken;
    private final String permalink;
    private final String email;
    private final SharedPreferences pref;

    public LoggedInUser(Context context, String name, String authToken, String permalink, String email) {
        this.name = name;
        this.permalink = permalink;
        this.email = email;
        pref = context.getSharedPreferences("ACCOUNT", MODE_PRIVATE);
        pref.edit().putString("email", email).apply();
        updateToken(authToken);
    }

    public String getName() {
        return name;
    }

    public void updateToken(String authToken) {
        this.authToken = authToken;
        pref.edit().putString("token", authToken).apply();
    }
    public String getAuthToken() {
        return authToken;
    }
    public void logout() {
        pref.edit().remove("email").apply();
        pref.edit().remove("token").apply();
    }
}