package com.thinqtv.thinqtv_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.thinqtv.thinqtv_android.data.DataSource;
import com.thinqtv.thinqtv_android.data.UserRepository;

import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import android.widget.LinearLayout.LayoutParams;

public class conversations_fragment extends Fragment {
    private static final String THINQTV_ROOM_NAME = "ThinqTV";
    private static final String screenNameKey = "com.thinqtv.thinqtv_android.SCREEN_NAME";
    private static String lastScreenNameStr = "";

    private ActionBarDrawerToggle mDrawerToggle; //toggle for sidebar button shown in action bar

    public conversations_fragment() {
        // Required empty public constructor
    }

    public static conversations_fragment newInstance() {
        conversations_fragment fragment = new conversations_fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.conversations_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeEvents();

        // If a user is logged in, use their name. Otherwise, try to find a name elsewhere.
        if (UserRepository.getInstance().isLoggedIn()) {
            lastScreenNameStr = UserRepository.getInstance().getLoggedInUser().getName();
        }
        else {
            // restore screen name using lastInstanceState if possible
            if (savedInstanceState != null) {
                lastScreenNameStr = savedInstanceState.getString(screenNameKey);
            }
            // else try to restore it using SharedPreferences
            else {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                String defaultValue = lastScreenNameStr;
                lastScreenNameStr = sharedPref.getString(screenNameKey, defaultValue);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Check if a user has logged in, and if so, set the screen name.
        if (UserRepository.getInstance().isLoggedIn()) {
            lastScreenNameStr = UserRepository.getInstance().getLoggedInUser().getName();
        }
        // Otherwise, restore text inside screen name field if the user hasn't typed anything to override it
        else {
            //TODO: SHOULD ANYTHING HAPPEN IF THE APP IS OPENED AND THE USER IS NOT LOGGED IN?
        }
    }

    @Override
    public void onDestroy() {
        if (lastScreenNameStr.length() > 0) {
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
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
        Intent intent = new Intent(getContext().getApplicationContext(), ConferenceActivity.class);
        intent.setAction("org.jitsi.meet.CONFERENCE");
        intent.putExtra("JitsiMeetConferenceOptions", options);
        startActivity(intent);
        getActivity().finish();
    }

    // listener for when a user clicks an event to go to its page
    private class goToWebview_ClickListener implements View.OnClickListener{
        private Context mContext;
        private String webviewLink;

        public goToWebview_ClickListener(Context context, String address){
            mContext = context;
            webviewLink = address;
        }

        @Override
        public void onClick(View v){
            Intent i = new Intent(mContext, AnyWebview.class);
            i.putExtra("webviewLink", webviewLink); //Optional parameters
            startActivity(i);
        }
    }

    // Use EventsJSON file to fill in ScrollView
    public void setUpcomingEvents(ArrayList<JSONObject> json)
    {
        Collections.sort(json, new Comparator<JSONObject>() {

            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                // TODO Auto-generated method stub

                try {
                    return (lhs.getString("start_at").toLowerCase().compareTo(rhs.getString("start_at").toLowerCase()));
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return 0;
                }
            }
        });


        try {
            //link layout and JSON file
            LinearLayout linearLayout = getView().findViewById(R.id.upcoming_events_linearView);

            // Get the selected event filter text
            Spinner eventFilter_spinner = (Spinner)getView().findViewById(R.id.eventsSpinner);
            String eventFilter_selection = eventFilter_spinner.getSelectedItem().toString();

            //For each event in the database, create a new item for it in ScrollView
            for(int i=0; i < json.size(); i++)
            {
                // get the time of the event in local time
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
                dateFormat.setTimeZone(TimeZone.getTimeZone("MST"));
                Date date = new Date();
                try {
                    date = dateFormat.parse(json.get(i).getString("start_at"));
                } catch (ParseException e) { e.printStackTrace(); }
                dateFormat.setTimeZone(TimeZone.getDefault());
                SimpleDateFormat displayFormat = new SimpleDateFormat("EEE, MMM dd - h:mm aa");
                String eventTimeString = displayFormat.format(date);

                // get the title of the event
                String eventTitleString = json.get(i).getString("name");

                // gets the name and sets its values
                TextView newEvent_textView = new TextView(getContext());
                newEvent_textView.setId(View.generateViewId());
                newEvent_textView.setTextSize(22);
                newEvent_textView.setTextColor(getResources().getColor(R.color.colorPrimary));
                newEvent_textView.setLayoutParams(new LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));
                newEvent_textView.setGravity(Gravity.CENTER_HORIZONTAL);
                newEvent_textView.setText(Html.fromHtml("<b>" + eventTitleString + "</b> <br> <br>" + eventTimeString));

                // add listener to the name, so when the user clicks an event it will bring them to the event page
                //newEvent_textView.setOnClickListener(new goToWebview_ClickListener(getContext(),
                //"http://www.thinq.tv/" + json.getJSONObject(i).getString("permalink")));

                // Now that you have your textview, create a container for it and add it
                ConstraintLayout constraintLayout = new ConstraintLayout(getContext());
                constraintLayout.addView(newEvent_textView);

                // Add simple divider to put in between ConstraintLayouts (ie events)
                View viewDivider = new View(getContext());
                viewDivider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
                viewDivider.setBackgroundColor(Color.LTGRAY);

                //get current date and what week it is
                Calendar mCalendar = Calendar.getInstance();

                switch(eventFilter_selection)
                {
                    case ("All Events \u25bc") :
                    {
                        Date end_time = new Date();
                        try {
                            end_time = dateFormat.parse(json.get(i).getString("end_at"));
                        } catch (ParseException e) { e.printStackTrace(); }

                        Date current_time = mCalendar.getTime();

                        if (date.before(current_time) && end_time.after(current_time))
                        {
                            Button happening_now = new Button(getContext());
                            happening_now.setId(View.generateViewId());
                            happening_now.setBackground(getActivity().getDrawable(R.drawable.rounded_button));
                            happening_now.setTextSize(15);
                            happening_now.setTextColor(Color.WHITE);
                            happening_now.setText(R.string.happening_now);
                            happening_now.setPadding(100,0,100,0);
                            happening_now.setAllCaps(false);

                            happening_now.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    onJoinClick(v);
                                }
                            });

                            // Remove the event time from the textView and add the Happening Now Button
                            newEvent_textView.setText(Html.fromHtml("<b>" + eventTitleString + "</b> <br>"));
                            constraintLayout.addView(happening_now);

                            // Center the Happening Now button under the textView
                            ConstraintSet constraintSet = new ConstraintSet();
                            constraintSet.clone(constraintLayout);
                            constraintSet.connect(happening_now.getId(),ConstraintSet.TOP,newEvent_textView.getId(),ConstraintSet.BOTTOM,0);
                            constraintSet.connect(happening_now.getId(),ConstraintSet.START,newEvent_textView.getId(),ConstraintSet.START,0);
                            constraintSet.connect(happening_now.getId(),ConstraintSet.END,newEvent_textView.getId(),ConstraintSet.END,0);
                            constraintSet.applyTo(constraintLayout);
                        }

                        if (!json.get(i).getString("topic").equals("DropIn"))
                        {
                            try {
                                linearLayout.addView(constraintLayout);
                                linearLayout.addView(viewDivider);
                            } catch (NullPointerException e) {}
                        }
                        break;
                    }
                    case ("This Week \u25bc") :
                    {
                        Date end_time = new Date();
                        try {
                            end_time = dateFormat.parse(json.get(i).getString("end_at"));
                        } catch (ParseException e) { e.printStackTrace(); }

                        Date current_time = mCalendar.getTime();

                        if (date.before(current_time) && end_time.after(current_time))
                        {
                            Button happening_now = new Button(getContext());
                            happening_now.setId(View.generateViewId());
                            happening_now.setBackground(getActivity().getDrawable(R.drawable.rounded_button));
                            happening_now.setTextSize(15);
                            happening_now.setTextColor(Color.WHITE);
                            happening_now.setText(R.string.happening_now);
                            happening_now.setPadding(100,0,100,0);
                            happening_now.setAllCaps(false);

                            happening_now.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    onJoinClick(v);
                                }
                            });

                            // Remove the event time from the textView and add the Happening Now Button
                            newEvent_textView.setText(Html.fromHtml("<b>" + eventTitleString + "</b> <br>"));
                            constraintLayout.addView(happening_now);

                            // Center the Happening Now button under the textView
                            ConstraintSet constraintSet = new ConstraintSet();
                            constraintSet.clone(constraintLayout);
                            constraintSet.connect(happening_now.getId(),ConstraintSet.TOP,newEvent_textView.getId(),ConstraintSet.BOTTOM,0);
                            constraintSet.connect(happening_now.getId(),ConstraintSet.START,newEvent_textView.getId(),ConstraintSet.START,0);
                            constraintSet.connect(happening_now.getId(),ConstraintSet.END,newEvent_textView.getId(),ConstraintSet.END,0);
                            constraintSet.applyTo(constraintLayout);
                        }

                        mCalendar.set(Calendar.WEEK_OF_MONTH, (mCalendar.get(Calendar.WEEK_OF_MONTH) + 1));
                        Date filterDate = mCalendar.getTime();

                        if (date.before(filterDate))
                        {
                            if (!json.get(i).getString("topic").equals("DropIn"))
                            {
                                try {
                                    linearLayout.addView(constraintLayout);
                                    linearLayout.addView(viewDivider);
                                } catch (NullPointerException e) {}
                            }
                        }
                        break;
                    }
                    case ("Next Week \u25bc") :
                    {
                        mCalendar.set(Calendar.WEEK_OF_MONTH, (mCalendar.get(Calendar.WEEK_OF_MONTH) + 1));
                        Date filterDate = mCalendar.getTime();

                        if (date.after(filterDate))
                        {
                            mCalendar.set(Calendar.WEEK_OF_MONTH, (mCalendar.get(Calendar.WEEK_OF_MONTH) + 1));
                            filterDate = mCalendar.getTime();

                            if (date.before(filterDate))
                            {
                                if (!json.get(i).getString("topic").equals("DropIn"))
                                {
                                    try {
                                        linearLayout.addView(constraintLayout);
                                        linearLayout.addView(viewDivider);
                                    } catch (NullPointerException e) {}
                                }
                            }
                        }
                        break;
                    }
                    case ("Future \u25bc") :
                    {
                        mCalendar.set(Calendar.WEEK_OF_MONTH, (mCalendar.get(Calendar.WEEK_OF_MONTH) + 2));
                        Date filterDate = mCalendar.getTime();

                        if (date.after(filterDate))
                        {
                            if (!json.get(i).getString("topic").equals("DropIn"))
                            {
                                try {
                                    linearLayout.addView(constraintLayout);
                                    linearLayout.addView(viewDivider);
                                } catch (NullPointerException e) {}
                            }
                        }
                        break;
                    }
                }

                constraintLayout.setPadding(50,50,50,50);
                constraintLayout.setLayoutParams(new LinearLayout.LayoutParams (ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
            }
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public void getEventsJSONfile()
    {
        // where you get the JSON file
        final String url = "https://thinq.tv/api/v1/events";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                // If you receive a response, the JSON data is saved in response
                // Clear the linearLayout
                try {
                    LinearLayout layout = (LinearLayout) getView().findViewById(R.id.upcoming_events_linearView);
                    layout.removeAllViews();
                } catch (NullPointerException e) { return; }

                //fill it back in with the response data
                ArrayList<JSONObject> array = new ArrayList<JSONObject>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        array.add(response.getJSONObject(i));
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                setUpcomingEvents(array);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LinearLayout layout = (LinearLayout) getView().findViewById(R.id.upcoming_events_linearView);
                layout.removeView(getView().findViewById(R.id.loading_events));

                TextView loadingError = getView().findViewById(R.id.loading_placeholder);
                loadingError.setVisibility(View.VISIBLE);
            }
        });
        DataSource.getInstance().addToRequestQueue(request, getContext());
    }

    public void initializeEvents()
    {

        // get the spinner filter and the layout that's inside of it
        Spinner eventFilter = (Spinner) getView().findViewById(R.id.eventsSpinner);

        // add listener for whenever a user changes filter
        eventFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextSize(18);
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
                getEventsJSONfile();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                // this doesn't ever happen but i need to override the virtual class
            }
        });
    }
}
