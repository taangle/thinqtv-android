package com.thinqtv.thinqtv_android.data.model;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class LoggedInUser {

    private final SharedPreferences pref;
    private HashMap<String, String> userInfo;

    public LoggedInUser(Context context, HashMap<String, String> userInfo) {
        this.userInfo = userInfo;
        pref = context.getSharedPreferences("ACCOUNT", MODE_PRIVATE);
        pref.edit().putString("token", userInfo.get("token")).apply();
    }

    public HashMap<String, String> getUserInfo() { return userInfo; }

    public void updateUserInfo(Map<String, String> updateParams) {
        for (Map.Entry<String, String> entry : updateParams.entrySet()) {
            if (entry.getValue() != null) {
                userInfo.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public void logout() {
        pref.edit().remove("token").apply();
    }
}