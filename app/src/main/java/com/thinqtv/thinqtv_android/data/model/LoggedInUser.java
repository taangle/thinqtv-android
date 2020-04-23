package com.thinqtv.thinqtv_android.data.model;

public class LoggedInUser {

    private final String name;
    private String authToken;
    private final String permalink;
    private final String email;

    public LoggedInUser(String name, String authToken, String permalink, String email) {
        this.name = name;
        this.authToken = authToken;
        this.permalink = permalink;
        this.email = email;
    }

    public String getName() {
        return name;
    }
    public String getEmail() { return email; }
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
    public String getAuthToken() {
        return authToken;
    }
}