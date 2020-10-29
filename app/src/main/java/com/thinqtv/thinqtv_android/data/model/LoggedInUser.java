package com.thinqtv.thinqtv_android.data.model;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

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

    public LoggedInUser(Context context, String authToken, String name, String permalink) {
        this.authToken = authToken;
        this.permalink = permalink;
        this.email = email;
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
    public String getPermalink() { return permalink; }
    public String getAbout() { return about; }
    public String getGenre1() { return genre1; }
    public String getGenre2() { return genre2; }
    public String getGenre3() { return genre3; }
    public String getProfilePic() { return profilePic; }
    public String getBannerPic() { return bannerPic; }

    public void updateToken(String authToken) {
        this.authToken = authToken;
        pref.edit().putString("token", authToken).apply();
    }

    public void updateEmail(String email) {
        this.email = email;
        pref.edit().putString("email", email).apply();
    }
    public void updateProfile(String name, String profilePic, String about, String genre1, String genre2, String genre3, String bannerPic) {
        try {
            JSONObject profilePicJson = new JSONObject(profilePic);
            this.profilePic = profilePicJson.getString("url");
            JSONObject bannerPicJson = new JSONObject(bannerPic);
            this.bannerPic = bannerPicJson.getString("url");
        } catch(JSONException e) {
            e.printStackTrace();
        }
        this.about = about;
        this.genre1 = genre1;
        this.genre2 = genre2;
        this.genre3 = genre3;
    }
    public void updateAccount(String email, String permalink) {
        updateEmail(email);
        this.permalink = permalink;
    }
    public void logout() {
        pref.edit().remove("email").apply();
        pref.edit().remove("token").apply();
    }
}