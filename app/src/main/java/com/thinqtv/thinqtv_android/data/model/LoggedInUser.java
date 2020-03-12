package com.thinqtv.thinqtv_android.data.model;

public class LoggedInUser {

    private String name;
    private String authToken;

    public LoggedInUser(String name, String authToken) {
        this.name = name;
        this.authToken = authToken;
    }

    public String getName() {
        return name;
    }
    public String getAuthToken() {
        return authToken;
    }
}
