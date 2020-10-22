package com.thinqtv.thinqtv_android.data.model;

public class LoggedInUser {

    private final String authToken;
    private String name;
    private String permalink;

    public LoggedInUser(String authToken, String name, String permalink) {
        this.authToken = authToken;
        this.name = name;
        this.permalink = permalink;
    }

    public String getName() {
        return name;
    }
    public String getPermalink() { return permalink; }
}