package com.thinqtv.thinqtv_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;

import java.net.MalformedURLException;
import java.net.URL;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.*;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final String THINQTV_ROOM_NAME = "ThinqTV";
    private static final String screenNameKey = "com.thinqtv.thinqtv_android.SCREEN_NAME";
    private static String lastScreenNameStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getJSONfile();
        
        // restore screen name using lastInstanceState if possible
        if (savedInstanceState != null) {
            lastScreenNameStr = savedInstanceState.getString(screenNameKey);
        }
        // else try to restore it using SharedPreferences
        else {
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            String defaultValue = lastScreenNameStr;
            lastScreenNameStr = sharedPref.getString(screenNameKey, defaultValue);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // restore text inside screen name field if the user hasn't typed anything to override it
        EditText screenName = findViewById(R.id.screenName);
        String screenNameStr = screenName.getText().toString();
        if (screenNameStr.length() == 0) {
            screenName.setText(lastScreenNameStr);
        }
    }

    @Override
    protected void onDestroy() {
        if (lastScreenNameStr.length() > 0) {
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(screenNameKey, lastScreenNameStr);
            editor.commit();
        }

        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // save lastScreenNameStr to savedInstanceState if it exists
        if (lastScreenNameStr.length() > 0) {
            outState.putString(screenNameKey, lastScreenNameStr);
        }

        super.onSaveInstanceState(outState);
    }

    // Button listener for "Join Conversation" button that connects to default ThinQ.TV chatroom
    public void onJoinClick(View v) {
        // extract screen name and conference name from EditText fields
        EditText screenName = findViewById(R.id.screenName);
        lastScreenNameStr = screenName.getText().toString();

        JitsiMeetConferenceOptions.Builder optionsBuilder
                = new JitsiMeetConferenceOptions.Builder()
                .setRoom(THINQTV_ROOM_NAME);

        if (lastScreenNameStr.length() > 0) {
            Log.d("SCREEN_NAME", lastScreenNameStr);
            Bundle userInfoBundle = new Bundle();
            // the string "displayName" is required by the API
            userInfoBundle.putString("displayName", lastScreenNameStr);
            optionsBuilder.setUserInfo(new JitsiMeetUserInfo(userInfoBundle));
        }

        JitsiMeetConferenceOptions options = optionsBuilder.build();

        // build and start intent to start a jitsi meet conference
        Intent intent = new Intent(getApplicationContext(), ConferenceActivity.class);
        intent.setAction("org.jitsi.meet.CONFERENCE");
        intent.putExtra("JitsiMeetConferenceOptions", options);
        startActivity(intent);
        finish();
    }

    // go to get involved page
    public void goGetInvolved(View V){
        Intent i = new Intent(this, GetInvolved.class);
        startActivity(i);
    }
    String fullJSON;
    public void setUpcomingEvents()
    {
        TextView event1 = (TextView) findViewById(R.id.upcomingEvent_1);
        TextView event2 = (TextView) findViewById(R.id.upcomingEvent_2);
        TextView event3 = (TextView) findViewById(R.id.upcomingEvent_3);
        TextView event4 = (TextView) findViewById(R.id.upcomingEvent_4);
        TextView event5 = (TextView) findViewById(R.id.upcomingEvent_5);

        try {
            JSONArray json = new JSONArray(fullJSON);
            event1.setText(json.getJSONObject(0).getString("id"));
            event2.setText(json.getJSONObject(1).getString("id"));
            event3.setText(json.getJSONObject(2).getString("id"));
            event4.setText(json.getJSONObject(3).getString("id"));
            event5.setText(json.getJSONObject(4).getString("id"));
        } catch (JSONException e) {
            // TODO : ADD SOMETHING FOR EXCEPTION
        }
    }
    public void getJSONfile()
    {
        final String url = "https://thinqtv.herokuapp.com/events.json";


        //RequestQueue initialized
        RequestQueue mRequestQueue;
        mRequestQueue = Volley.newRequestQueue(this);

        //String Request initialized
        StringRequest mStringRequest;
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                fullJSON = response;
                setUpcomingEvents();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        mRequestQueue.add(mStringRequest);
    }
    public void expandEventsClick(View v) {
        String temp = "";
    }
}
