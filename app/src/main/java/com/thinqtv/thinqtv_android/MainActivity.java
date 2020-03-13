package com.thinqtv.thinqtv_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.LinearLayout;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;
import org.json.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {
    private static final String THINQTV_ROOM_NAME = "ThinqTV";
    private static final String screenNameKey = "com.thinqtv.thinqtv_android.SCREEN_NAME";
    private static String lastScreenNameStr = "";
    private String fullEventsJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getEventsJSONfile();
        setContentView(R.layout.activity_main);
        getEventsJSONfile();
        
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

    // Use EventsJSON file to fill in ScrollView
    public void setUpcomingEvents()
    {
        try {
            //link layout and JSON file
            LinearLayout linearLayout = findViewById(R.id.upcoming_events_linearView);
            JSONArray json = new JSONArray(fullEventsJSON);

            //For each event in the database, create a new item for it in ScrollView
            for(int i=0; i < json.length(); i++)
            {
                // gets the name and sets its values
                TextView newEvent_name = new TextView(this);
                newEvent_name.setText(json.getJSONObject(i).getString("name"));
                newEvent_name.setTextSize(22);
                newEvent_name.setPadding(20, 70, 0, 0);
                newEvent_name.setTextColor(getResources().getColor(R.color.colorPrimary));

                // gets the host id and sets its values
                TextView newEvent_host = new TextView(this);
                newEvent_host.setText(getResources().getString(R.string.hosted_by) + " " + json.getJSONObject(i).getString("id"));
                newEvent_host.setTextSize(15);
                newEvent_host.setPadding(20, 150, 0, 0);
                newEvent_host.setTextColor(Color.GRAY);

                // gets the date of event, formats it accordingly
                TextView newEvent_time = new TextView(this);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
                Date date = new Date();
                try {
                    date = format.parse(json.getJSONObject(i).getString("start_at"));
                } catch (ParseException e) { e.printStackTrace(); }
                format.applyPattern("EEE, MMM dd");
                newEvent_time.setText(format.format(date));
                newEvent_time.setTextSize(20);
                newEvent_time.setPadding(750, 80, 0, 0);
                newEvent_time.setTextColor(getResources().getColor(R.color.colorPrimary));

                // Now you have all your TextViews, create a ConstraintLayout for each one
                ConstraintLayout constraintLayout = new ConstraintLayout(this);
                constraintLayout.setLayoutParams(new LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100f, getResources().getDisplayMetrics())));

                // Add your TextViews to the ConstraintLayout
                constraintLayout.addView(newEvent_name);
                constraintLayout.addView(newEvent_host);
                constraintLayout.addView(newEvent_time);

                // Add simple divider to put in between ConstraintLayouts (ie events)
                View viewDivider = new View(this);
                viewDivider.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 2));
                viewDivider.setBackgroundColor(Color.LTGRAY);

                // Add the ConstraintLayout and divider to the linked linearLayout
                linearLayout.addView(constraintLayout);
                linearLayout.addView(viewDivider);
            }

        } catch (JSONException e) { e.printStackTrace(); }
    }

    public void getEventsJSONfile()
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
                // If you receive a response, set the fullEventsJSON string to the response
                // Then call setUpcomingEvents() to fill in ScrollView data
                fullEventsJSON = response;
                setUpcomingEvents();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO : ADD SOMETHING TO HAPPEN WHEN JSON IS NOT REACHABLE
                // TODO : ie display error message
            }
        });

        //Add the request to the Queue
        //This is essentially telling it to execute
        mRequestQueue.add(mStringRequest);
    }

    public void expandEventsClick(View v) {
        // Link the header TextView
        TextView header = (TextView) findViewById(R.id.upcoming_events_header);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) header.getLayoutParams();

        // Link any buttons
        CheckBox checkBox = (CheckBox) findViewById(R.id.expand_checkBox);
        Button joinButton = findViewById(R.id.defaultJoinButton);
        Button involvedButton = findViewById(R.id.get_involved);

        // if the Upcoming Events is NOT expanded
        if (!checkBox.isChecked())
        {
            // convert pixels to dp and set the margin
            float headerMarginSmall = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    450f,
                    getResources().getDisplayMetrics()
            );
            params.topMargin = (int) headerMarginSmall;

            // the buttons are always visible under the ScrollView for some reason
            // because of this, they must be set to invisible when you expand the Events
            joinButton.setVisibility(View.VISIBLE);
            involvedButton.setVisibility(View.VISIBLE);
        }
        // when the Upcoming Events are expanded already, this code collapses it
        // it's just the opposite of the above code essentially
        else
        {
            float headerMarginLarge = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    215f,
                    getResources().getDisplayMetrics()
            );
            params.topMargin = (int) headerMarginLarge;

            joinButton.setVisibility(View.INVISIBLE);
            involvedButton.setVisibility(View.INVISIBLE);
        }

        // move header based on the values set in the if-else statement
        // other items are linked to the header so they will move as well
        header.setLayoutParams(params);
    }
}
