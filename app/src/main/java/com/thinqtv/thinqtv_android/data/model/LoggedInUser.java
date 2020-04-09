package com.thinqtv.thinqtv_android.data.model;

public class LoggedInUser {

    private final String name;
    private final String authToken;
    private final String permalink;

    public LoggedInUser(String name, String authToken, String permalink) {
        this.name = name;
        this.authToken = authToken;
        this.permalink = permalink;
    }

    public String getName() {
        return name;
    }
}