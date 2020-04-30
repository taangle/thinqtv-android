package com.thinqtv.thinqtv_android.data.model;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class LoggedInUser {

    private String name;
    private String authToken;
    private String permalink;
    private String email;
    private final SharedPreferences pref;
    private String profilePic;
    private String about;
    private String genre1;
    private String genre2;
    private String genre3;
    private String bannerPic;
    private String id;

    public LoggedInUser(Context context, String name, String authToken, String permalink, String email, String id) {
        this.name = name;
        this.authToken = authToken;
        this.permalink = permalink;
        this.email = email;
        this.id = id;
        pref = context.getSharedPreferences("ACCOUNT", MODE_PRIVATE);
        pref.edit().putString("email", email).apply();
        updateToken(authToken);
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() { return email; }
    public String getAuthToken() {
        return authToken;
    }

    public void updateToken(String authToken) {
        this.authToken = authToken;
        pref.edit().putString("token", authToken).apply();
    }
    public void updateProfile(String name, String profilePic, String about, String genre1, String genre2, String genre3, String bannerPic) {
        this.name = name;
        this.profilePic = profilePic;
        this.about = about;
        this.genre1 = genre1;
        this.genre2 = genre2;
        this.genre3 = genre3;
        this.bannerPic = bannerPic;
    }
    public void updateAccount(String email, String permalink) {
        this.email = email;
        this.permalink = permalink;
    }
    public void logout() {
        pref.edit().remove("email").apply();
        pref.edit().remove("token").apply();
    }
}