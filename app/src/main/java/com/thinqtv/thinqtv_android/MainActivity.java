package com.thinqtv.thinqtv_android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.JsonArrayRequest;
import com.thinqtv.thinqtv_android.data.DataSource;
import com.thinqtv.thinqtv_android.data.UserRepository;
import com.thinqtv.thinqtv_android.ui.auth.LoginActivity;

import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;
import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String THINQTV_ROOM_NAME = "ThinqTV";
    private static final String screenNameKey = "com.thinqtv.thinqtv_android.SCREEN_NAME";
    private static String lastScreenNameStr = "";

    private ActionBarDrawerToggle mDrawerToggle; //toggle for sidebar button shown in action bar
    boolean eventsExpanded = false; //used to expand and collapse the Events ScrollView, changes with each click

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent loadSavedUser = new Intent(this, StartupLoadingActivity.class);
        startActivity(loadSavedUser);

        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        initializeEvents();
        generateSidebar();
        setDrawerToggle();

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
                SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
                String defaultValue = lastScreenNameStr;
                lastScreenNameStr = sharedPref.getString(screenNameKey, defaultValue);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        generateSidebar();

        // restore text inside screen name field if the user hasn't typed anything to override it
        EditText screenName = findViewById(R.id.screenName);
        // Check if a user has logged in, and if so, set the screen name.
        if (UserRepository.getInstance().isLoggedIn()) {
            lastScreenNameStr = UserRepository.getInstance().getLoggedInUser().getName();
            screenName.setText(lastScreenNameStr);
        }
        // Otherwise, restore text inside screen name field if the user hasn't typed anything to override it
        else {
            String screenNameStr = screenName.getText().toString();
            if (screenNameStr.length() == 0) {
                screenName.setText(lastScreenNameStr);
            }
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
        Intent i = new Intent(this, AddEventActivity.class);
        startActivity(i);
    }

    // Go to login page.
    public void goToLogin(View v) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    public void logout(View v) {
        UserRepository.getInstance().logout();

        finish();
        overridePendingTransition(R.anim.catalyst_fade_in, R.anim.catalyst_fade_out);
        startActivity(getIntent());
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
    public void setUpcomingEvents(JSONArray json)
    {
        try {
            //link layout and JSON file
            LinearLayout linearLayout = findViewById(R.id.upcoming_events_linearView);

            // Get the selected event filter text
            Spinner eventFilter_spinner = (Spinner)findViewById(R.id.eventsSpinner);
            String eventFilter_selection = eventFilter_spinner.getSelectedItem().toString();

            //For each event in the database, create a new item for it in ScrollView
            for(int i=0; i < json.length(); i++)
            {
                // gets the name and sets its values
                TextView newEvent_name = new TextView(this);
                newEvent_name.setTextSize(22);
                newEvent_name.setPadding(20, 40, 0, 0);
                newEvent_name.setTextColor(getResources().getColor(R.color.colorPrimary));
                newEvent_name.setText(json.getJSONObject(i).getString("name")
                        .substring(0, Math.min(json.getJSONObject(i).getString("name").length(), 18)));
                if (json.getJSONObject(i).getString("name").length() > 18)
                    newEvent_name.setText(newEvent_name.getText() + "...");

                // add listener to the name, so when the user clicks an event it will bring them to the event page
                newEvent_name.setOnClickListener(new goToWebview_ClickListener(this,
                        "http://www.thinq.tv/" + json.getJSONObject(i).getString("permalink")));

                // gets the host id and sets its values
                TextView newEvent_host = new TextView(this);
                newEvent_host.setTextSize(15);
                newEvent_host.setWidth(600);
                newEvent_host.setPadding(20, 110, 0, 0);
                newEvent_host.setTextColor(Color.GRAY);
                newEvent_host.setText("Hosted by " + json.getJSONObject(i).getString("username")
                        .substring(0, Math.min(json.getJSONObject(i).getString("username").length(), 18)));
                if (json.getJSONObject(i).getString("username").length() > 18)
                    newEvent_host.setText(newEvent_host.getText() + "...");

                // gets the date of event
                TextView newEvent_time = new TextView(this);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
                Date date = new Date();
                try {
                    date = dateFormat.parse(json.getJSONObject(i).getString("start_at"));
                } catch (ParseException e) { e.printStackTrace(); }

                // formats the date from above into viewable format
                SimpleDateFormat displayFormat = new SimpleDateFormat("EEE, MMM dd");
                newEvent_time.setText(displayFormat.format(date));
                newEvent_time.setTextSize(20);
                newEvent_time.setPadding(750, 45, 0, 0);
                newEvent_time.setTextColor(getResources().getColor(R.color.colorPrimary));

                // formats the date from above into viewable format (BUT NOW ITS START TIME)
                displayFormat = new SimpleDateFormat("h:mm aa");
                TextView newEvent_starttime = new TextView(this);
                newEvent_starttime.setText(displayFormat.format(date) + " PDT");
                newEvent_starttime.setTextSize(15);
                newEvent_starttime.setPadding(750, 110, 0, 0);
                newEvent_starttime.setTextColor(Color.GRAY);

                // Now you have all your TextViews, create a ConstraintLayout for each one
                ConstraintLayout constraintLayout = new ConstraintLayout(this);
                constraintLayout.setLayoutParams(new LayoutParams
                        (ConstraintLayout.LayoutParams.MATCH_PARENT,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75f, getResources().getDisplayMetrics())));

                // Add your TextViews to the ConstraintLayout
                constraintLayout.addView(newEvent_name);
                constraintLayout.addView(newEvent_host);
                constraintLayout.addView(newEvent_time);
                constraintLayout.addView(newEvent_starttime);

                // Add simple divider to put in between ConstraintLayouts (ie events)
                View viewDivider = new View(this);
                viewDivider.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 2));
                viewDivider.setBackgroundColor(Color.LTGRAY);

                //get current date and what week it is
                Calendar mCalendar = Calendar.getInstance();

                switch(eventFilter_selection)
                {
                    case ("All Events") :
                    {
                        Date end_time = new Date();
                        try {
                            end_time = dateFormat.parse(json.getJSONObject(i).getString("end_at"));
                        } catch (ParseException e) { e.printStackTrace(); }

                        Date current_time = mCalendar.getTime();

                        if (date.before(current_time) && end_time.after(current_time))
                        {
                            constraintLayout.setLayoutParams(new LayoutParams
                                (ConstraintLayout.LayoutParams.MATCH_PARENT,
                                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 125f, getResources().getDisplayMetrics())));

                            Button happening_now = new Button(this);
                            happening_now.setBackground(getDrawable(R.drawable.rounded_button));
                            happening_now.setTextSize(15);
                            happening_now.setTextColor(Color.WHITE);
                            happening_now.setText(R.string.happening_now);
                            happening_now.setTransformationMethod(null);
                            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(800, 120);
                            happening_now.setLayoutParams(params);
                            happening_now.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
                            happening_now.setY((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70f, getResources().getDisplayMetrics()));
                            happening_now.setX((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50f, getResources().getDisplayMetrics()));

                            happening_now.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    onJoinClick(v);
                                }
                            });

                            constraintLayout.addView(happening_now);
                        }

                        linearLayout.addView(constraintLayout);
                        linearLayout.addView(viewDivider);
                        break;
                    }
                    case ("This Week") :
                    {
                        Date end_time = new Date();
                        try {
                            end_time = dateFormat.parse(json.getJSONObject(i).getString("end_at"));
                        } catch (ParseException e) { e.printStackTrace(); }

                        Date current_time = mCalendar.getTime();

                        if (date.before(current_time) && end_time.after(current_time))
                        {
                            constraintLayout.setLayoutParams(new LayoutParams
                                    (ConstraintLayout.LayoutParams.MATCH_PARENT,
                                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 125f, getResources().getDisplayMetrics())));

                            Button happening_now = new Button(this);
                            happening_now.setBackground(getDrawable(R.drawable.rounded_button));
                            happening_now.setTextSize(15);
                            happening_now.setTextColor(Color.WHITE);
                            happening_now.setText(R.string.happening_now);
                            happening_now.setTransformationMethod(null);
                            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(800, 120);
                            happening_now.setLayoutParams(params);
                            happening_now.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
                            happening_now.setY((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70f, getResources().getDisplayMetrics()));
                            happening_now.setX((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50f, getResources().getDisplayMetrics()));

                            happening_now.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    onJoinClick(v);
                                }
                            });

                            constraintLayout.addView(happening_now);
                        }

                        mCalendar.set(Calendar.WEEK_OF_MONTH, (mCalendar.get(Calendar.WEEK_OF_MONTH) + 1));
                        Date filterDate = mCalendar.getTime();

                        if (date.before(filterDate))
                        {
                            linearLayout.addView(constraintLayout);
                            linearLayout.addView(viewDivider);
                        }
                        break;
                    }
                    case ("Next Week") :
                    {
                        mCalendar.set(Calendar.WEEK_OF_MONTH, (mCalendar.get(Calendar.WEEK_OF_MONTH) + 1));
                        Date filterDate = mCalendar.getTime();

                        if (date.after(filterDate))
                        {
                            mCalendar.set(Calendar.WEEK_OF_MONTH, (mCalendar.get(Calendar.WEEK_OF_MONTH) + 1));
                            filterDate = mCalendar.getTime();

                            if (date.before(filterDate))
                            {
                                linearLayout.addView(constraintLayout);
                                linearLayout.addView(viewDivider);
                            }
                        }
                        break;
                    }
                    case ("Future") :
                    {
                        mCalendar.set(Calendar.WEEK_OF_MONTH, (mCalendar.get(Calendar.WEEK_OF_MONTH) + 2));
                        Date filterDate = mCalendar.getTime();

                        if (date.after(filterDate))
                        {
                            linearLayout.addView(constraintLayout);
                            linearLayout.addView(viewDivider);
                        }
                        break;
                    }
                }
            }
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public void getEventsJSONfile()
    {
        // where you get the JSON file
        final String url = "https://thinqtv.herokuapp.com/events.json";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                // If you receive a response, the JSON data is saved in response
                // Clear the linearLayout
                LinearLayout layout = (LinearLayout) findViewById(R.id.upcoming_events_linearView);
                layout.removeAllViews();

                //fill it back in with the response data
                setUpcomingEvents(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                TextView loading = findViewById(R.id.loading_placeholder);
                loading.setText("Error : Unable to load fellowship information");
            }
        });
        DataSource.getInstance().addToRequestQueue(request, this);
    }

    public void initializeEvents()
    {
        // get the spinner filter and the layout that's inside of it
        Spinner eventFilter = (Spinner) findViewById(R.id.eventsSpinner);

        // add listener for whenever a user changes filter
        eventFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getEventsJSONfile();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                // this doesn't ever happen but i need to override the virtual class
            }
        });
    }

    public void expandEventsClick(View v)
    {
        // Link the header TextView
        TextView header = (TextView) findViewById(R.id.upcoming_events_header);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) header.getLayoutParams();

        // Link any buttons
        TextView carrot = (TextView) findViewById(R.id.expandButton);
        Button joinButton = findViewById(R.id.defaultJoinButton);

        // if the Upcoming Events are expanded, minimize
        if (eventsExpanded)
        {
            params.verticalBias = 0.5f;

            // the buttons are always visible under the ScrollView for some reason
            // because of this, they must be set to invisible when you expand the Events
            joinButton.setVisibility(View.VISIBLE);

            // make it twist
            carrot.setRotation(0);
            ConstraintLayout.LayoutParams lparams = (ConstraintLayout.LayoutParams) carrot.getLayoutParams();
            lparams.verticalBias = 0.33f;
            carrot.setLayoutParams(lparams);

            // for when it gets clicked again
            eventsExpanded = false;
        }
        // when the Upcoming Events are expanded already, this code collapses it
        // it's just the opposite of the above code essentially
        else
        {
            params.verticalBias = 0.25f;

            joinButton.setVisibility(View.INVISIBLE);

            carrot.setRotation(180);
            ConstraintLayout.LayoutParams lparams = (ConstraintLayout.LayoutParams) carrot.getLayoutParams();
            lparams.verticalBias = 0.48f;
            carrot.setLayoutParams(lparams);

            eventsExpanded = true;
        }

        // move header based on the values set in the if-else statement
        // other items are linked to the header so they will move as well
        header.setLayoutParams(params);
    }

    private void generateSidebar()
    {
        ListView mDrawerList = (ListView)findViewById(R.id.navList);
        DrawerLayout mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        ArrayAdapter<String> mAdapter;

        String[] osArray;
        if (UserRepository.getInstance().isLoggedIn())
        {
            osArray = getResources().getStringArray(R.array.sidebar_menu_loggedIn);

            //TODO: when a user can view profile, take below line out
            osArray[0] = UserRepository.getInstance().getLoggedInUser().getName();
        }
        else
            osArray = getResources().getStringArray(R.array.sidebar_menu);

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);

        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String choice = parent.getItemAtPosition(position).toString();
                if (choice.equals(getResources().getString(R.string.sidebar_register_login))) {
                    goToLogin(view);
                }
                else if (choice.equals(getResources().getString(R.string.sidebar_get_involved))) {
                    Intent i = new Intent(MainActivity.this, AnyWebview.class);
                    i.putExtra("webviewLink", "http://www.thinq.tv/getinvolved"); //Optional parameters
                    startActivity(i);
                }
                else if (choice.equals(getResources().getString(R.string.sidebar_podcast))) {
                    Intent i = new Intent(MainActivity.this, AnyWebview.class);
                    i.putExtra("webviewLink", "http://www.thinq.tv/drschaeferspeaking"); //Optional parameters
                    startActivity(i);
                }
                else if (choice.equals(getResources().getString(R.string.sidebar_about))) {
                    Intent i = new Intent(MainActivity.this, AnyWebview.class);
                    i.putExtra("webviewLink", "http://www.thinq.tv/aboutus"); //Optional parameters
                    startActivity(i);
                }
                else if (choice.equals(getResources().getString(R.string.sidebar_join_the_team))) {
                    Intent i = new Intent(MainActivity.this, AnyWebview.class);
                    i.putExtra("webviewLink", "http://www.thinq.tv/jointheteam"); //Optional parameters
                    startActivity(i);
                }
                else if (choice.equals(getResources().getString(R.string.sidebar_account))) {
                    Intent i = new Intent(MainActivity.this, AccountSettingsActivity.class);
                    startActivity(i);
                }
                else if (choice.equals(getResources().getString(R.string.sidebar_logout))) {
                    logout(view);
                }
                else if (choice.equals(getResources().getString(R.string.sidebar_add_event))) {
                    Intent i = new Intent(MainActivity.this, AddEventActivity.class);
                    startActivity(i);
                }
                else if (choice.equals(UserRepository.getInstance().getLoggedInUser().getName())) {
                    Intent i = new Intent(MainActivity.this, ControlPanelActivity.class);
                    startActivity(i);
                }
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
    }

    private void setDrawerToggle()
    {
        DrawerLayout mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}