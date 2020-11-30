package com.thinqtv.thinqtv_android.data.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.thinqtv.thinqtv_android.NotificationPublisher;
import com.thinqtv.thinqtv_android.R;
import com.thinqtv.thinqtv_android.data.DataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;

public class LoggedInUser {

    private final SharedPreferences pref;
    private HashMap<String, String> userInfo;
    private ArrayList<RSVP> rsvps;

    public LoggedInUser(Context context, HashMap<String, String> userInfo) {
        this.userInfo = userInfo;
        pref = context.getSharedPreferences("ACCOUNT", MODE_PRIVATE);
        pref.edit().putString("token", userInfo.get("token")).apply();
        rsvps = new ArrayList<RSVP>();
        loadRSVPs(context);
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

    private void loadRSVPs(Context context) {
        String authToken = userInfo.get("token");

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
        context.getResources().getString(R.string.rsvps_url), null, response -> {
            try {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject rsvpInfo = response.getJSONObject(i);
                    rsvps.add(new RSVP(rsvpInfo.getString("start_at"), rsvpInfo.getInt("id"), rsvpInfo.getString("name")));
                }
                if (rsvps.size() > 0) {
                    updateRSVPNotification(context);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            error.printStackTrace();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };
        DataSource.getInstance().addToRequestQueue(request, context);

    }
    private void updateRSVPNotification(Context context) {
        Collections.sort(rsvps, (lhs, rhs) -> (lhs.getStartTime().toLowerCase().compareTo(rhs.getStartTime().toLowerCase())));
        String closestTime = rsvps.get(0).getStartTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("MST"));
        Date date = new Date();
        long currentTime = date.getTime();
        try {
            date = dateFormat.parse(closestTime);
        } catch (ParseException e) { e.printStackTrace(); }
        NotificationPublisher.scheduleNotification("Come Join The Conversation!", rsvps.get(0).getName() + " is beginning. Come ", (int)(date.getTime() - currentTime), (Activity)context);
    }
}