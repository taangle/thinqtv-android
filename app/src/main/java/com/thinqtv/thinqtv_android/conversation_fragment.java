package com.thinqtv.thinqtv_android;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.thinqtv.thinqtv_android.data.DataSource;
import com.thinqtv.thinqtv_android.data.UserRepository;

import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class conversation_fragment extends Fragment {
    public conversation_fragment() {
        // Required empty public constructor
    }

    public static conversation_fragment newInstance() {
        conversation_fragment fragment = new conversation_fragment();
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
        return inflater.inflate(R.layout.conversation_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeEvents();
    }

    @Override
    public void onStart() {
        super.onStart();

        initializeEvents();
    }

    // Button listener for "Join Conversation" button that connects to default ThinQ.TV chatroom
    public void onJoinClick(View v, String roomName) {
        JitsiMeetConferenceOptions.Builder optionsBuilder
                = new JitsiMeetConferenceOptions.Builder()
                .setRoom(roomName);

        Bundle userInfoBundle = new Bundle();
        userInfoBundle.putString("displayName", UserRepository.getInstance().getLoggedInUser().getUserInfo().get("name"));
        optionsBuilder.setUserInfo(new JitsiMeetUserInfo(userInfoBundle));

        JitsiMeetConferenceOptions options = optionsBuilder.build();

        // build and start intent to start a jitsi meet conference
        Intent intent = new Intent(getContext().getApplicationContext(), ConferenceActivity.class);
        intent.setAction("org.jitsi.meet.CONFERENCE");
        intent.putExtra("JitsiMeetConferenceOptions", options);
        startActivity(intent);
    }

    // Use EventsJSON file to fill in ScrollView
    public void setUpcomingEvents(ArrayList<JSONObject> json) {
        sortByStartTime(json);

        // link layout and JSON file
        try {
            for(JSONObject eventObject : json) {
                createScrollViewItemForEvent(eventObject);
            }
        } catch (JSONException e) { e.printStackTrace(); }
    }

    private void createScrollViewItemForEvent(JSONObject eventObject) throws JSONException {
        LinearLayout linearLayout = getView().findViewById(R.id.upcoming_events_linearView);

        // Get the selected event filter text
        Spinner eventFilter_spinner = getView().findViewById(R.id.eventsSpinner);
        String eventFilter_selection = eventFilter_spinner.getSelectedItem().toString();

        // get the time of the event in local time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("MST"));
        Date date = new Date();
        try {
            date = dateFormat.parse(eventObject.getString("start_at"));
        } catch (ParseException e) { e.printStackTrace(); }

        SimpleDateFormat displayFormat = new SimpleDateFormat("EEE, MMM dd - h:mm aa");
        String eventTimeString = displayFormat.format(date);

        // get the title of the event
        String eventTitleString = eventObject.getString("name");

        // get the host name and permalink
        String eventHostString = eventObject.getString("username");
        String eventHostPerma = eventObject.getString("permalink");

        // gets the name and sets its values
        TextView newEvent_textView = new TextView(getContext());
        setTextViewProperties(newEvent_textView, eventTimeString, eventTitleString, eventHostString);

        // Now that you have your textview, create a container for it and add it
        ConstraintLayout constraintLayout = new ConstraintLayout(getContext());
        constraintLayout.addView(newEvent_textView);
        constraintLayout.setOnClickListener(v -> {
            Intent i = new Intent(getContext().getApplicationContext(), AnyWebview.class);
            i.putExtra("webviewLink", "https://thinq.tv/" + eventHostPerma);
            startActivity(i);
        });

        // Add simple divider to put in between ConstraintLayouts (ie events)
        View viewDivider = new View(getContext());
        viewDivider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
        viewDivider.setBackgroundColor(Color.LTGRAY);

        //get current date and what week it is
        Calendar calendar = Calendar.getInstance();

        String roomName = eventObject.getString("chatroom");

        switch(eventFilter_selection)
        {
            case ("Your RSVPs \u25bc") : {
                Date end_time = new Date();
                try {
                    end_time = dateFormat.parse(eventObject.getString("end_at"));
                } catch (ParseException e) { e.printStackTrace(); }

                Date current_time = calendar.getTime();

                if (date.before(current_time) && end_time.after(current_time))
                {
                    makeHappeningNow(eventTitleString, newEvent_textView, constraintLayout, roomName);
                }

                Date filterDate = calendar.getTime();
                if (UserRepository.getInstance().isLoggedIn() && end_time.after(filterDate)) {
                    //                        if (eventObject.getString("topic").equals("DropIn"))
                    {
                        try {
                            linearLayout.addView(constraintLayout);
                            linearLayout.addView(viewDivider);
                        } catch (NullPointerException e) {
                        }
                    }
                }
                break;
            }
            case ("All Events \u25bc") : {
                Date end_time = new Date();
                try {
                    end_time = dateFormat.parse(eventObject.getString("end_at"));
                } catch (ParseException e) { e.printStackTrace(); }

                Date current_time = calendar.getTime();

                if (date.before(current_time) && end_time.after(current_time))
                {
                    makeHappeningNow(eventTitleString, newEvent_textView, constraintLayout, roomName);
                }

//                        if (eventObject.getString("topic").equals("DropIn"))
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
                    end_time = dateFormat.parse(eventObject.getString("end_at"));
                } catch (ParseException e) { e.printStackTrace(); }

                Date current_time = calendar.getTime();

                if (date.before(current_time) && end_time.after(current_time))
                {
                    makeHappeningNow(eventTitleString, newEvent_textView, constraintLayout, roomName);
                }

                calendar.set(Calendar.WEEK_OF_MONTH, (calendar.get(Calendar.WEEK_OF_MONTH) + 1));
                Date filterDate = calendar.getTime();

                if (date.before(filterDate))
                {
//                            if (eventObject.getString("topic").equals("DropIn"))
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
                calendar.set(Calendar.WEEK_OF_MONTH, (calendar.get(Calendar.WEEK_OF_MONTH) + 1));
                Date filterDate = calendar.getTime();

                if (date.after(filterDate))
                {
                    calendar.set(Calendar.WEEK_OF_MONTH, (calendar.get(Calendar.WEEK_OF_MONTH) + 1));
                    filterDate = calendar.getTime();

                    if (date.before(filterDate))
                    {
//                                if (eventObject.getString("topic").equals("DropIn"))
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
                calendar.set(Calendar.WEEK_OF_MONTH, (calendar.get(Calendar.WEEK_OF_MONTH) + 2));
                Date filterDate = calendar.getTime();

                if (date.after(filterDate))
                {
//                            if (json.get(i).getString("topic").equals("DropIn"))
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

        setConstraintLayoutProperties(constraintLayout);
    }

    private void makeHappeningNow(String eventTitleString, TextView newEvent_textView, ConstraintLayout constraintLayout, String roomName) {
        Button happening_now = new Button(getContext());
        happening_now.setId(View.generateViewId());
        happening_now.setBackground(getActivity().getDrawable(R.drawable.rounded_button));
        happening_now.setTextSize(15);
        happening_now.setTextColor(Color.WHITE);
        happening_now.setText(R.string.happening_now);
        happening_now.setPadding(100, 0, 100, 0);
        happening_now.setAllCaps(false);

        happening_now.setOnClickListener(v -> {
            if (UserRepository.getInstance().isLoggedIn()) {
                onJoinClick(v, roomName);
            } else {
                Toast.makeText(getContext().getApplicationContext(),
                        "Please login to join a conversation", Toast.LENGTH_LONG).show();
            }
        });

        // Remove the event time from the textView and add the Happening Now Button
        newEvent_textView.setText(Html.fromHtml("<b>" + eventTitleString + "</b> <br>"));
        constraintLayout.addView(happening_now);

        // Center the Happening Now button under the textView
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(happening_now.getId(), ConstraintSet.TOP, newEvent_textView.getId(), ConstraintSet.BOTTOM, 0);
        constraintSet.connect(happening_now.getId(), ConstraintSet.START, newEvent_textView.getId(), ConstraintSet.START, 0);
        constraintSet.connect(happening_now.getId(), ConstraintSet.END, newEvent_textView.getId(), ConstraintSet.END, 0);
        constraintSet.applyTo(constraintLayout);
    }

    private void setConstraintLayoutProperties(ConstraintLayout constraintLayout) {
        constraintLayout.setPadding(50,50,50,50);
        constraintLayout.setLayoutParams(new LinearLayout.LayoutParams (ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
    }

    private void setTextViewProperties(TextView newEvent_textView, String eventTimeString, String eventTitleString, String eventHostString) {
        newEvent_textView.setId(View.generateViewId());
        newEvent_textView.setTextSize(22);
        newEvent_textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        newEvent_textView.setLayoutParams(new LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));
        newEvent_textView.setGravity(Gravity.CENTER_HORIZONTAL);
        newEvent_textView.setText(Html.fromHtml("<b>" + eventTitleString + "</b><br> <font color=#7F7F7F>" + eventHostString + "</font>" + "<br> <br>" + eventTimeString));
    }

    private void sortByStartTime(ArrayList<JSONObject> json) {
        Collections.sort(json, (lhs, rhs) -> {
            try {
                return (lhs.getString("start_at").toLowerCase().compareTo(rhs.getString("start_at").toLowerCase()));
            } catch (JSONException e) {
                e.printStackTrace();
                return 0;
            }
        });
    }

    public void getEventsJSONfile()
    {
        Spinner eventFilter_spinner = getView().findViewById(R.id.eventsSpinner);
        String eventFilter_selection = eventFilter_spinner.getSelectedItem().toString();
        JsonArrayRequest request;
        if (UserRepository.getInstance().isLoggedIn() && eventFilter_selection.equals("Your RSVPs ▼")) {
            request = new JsonArrayRequest(Request.Method.GET, getString(R.string.rsvps_url), null, this::handleEventResponse, error -> {
                Log.e("RSVP", "Getting RSVP'd event failed", error);
                handleResponseError();
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + UserRepository.getInstance().getLoggedInUser().getUserInfo().get("token"));
                    return headers;
                }
            };
        }
        else {
            request = new JsonArrayRequest(Request.Method.GET, getString(R.string.events_url), null, this::handleEventResponse, error -> {
                handleResponseError();
            });
        }
        DataSource.getInstance().addToRequestQueue(request, getContext());
    }

    private void handleResponseError() {
        LinearLayout layout = getView().findViewById(R.id.upcoming_events_linearView);
        layout.removeView(getView().findViewById(R.id.loading_events));

        TextView loadingError = getView().findViewById(R.id.loading_placeholder);
        loadingError.setVisibility(View.VISIBLE);
    }

    private void handleEventResponse(JSONArray response) {
        // If you receive a response, the JSON data is saved in response
        // Clear the linearLayout
        try {
            LinearLayout layout = getView().findViewById(R.id.upcoming_events_linearView);
            layout.removeAllViews();
        } catch (NullPointerException e) { return; }

        //fill it back in with the response data
        ArrayList<JSONObject> array = new ArrayList<>();
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

    public void initializeEvents()
    {
        // get the spinner filter and the layout that's inside of it
        Spinner eventFilter = getView().findViewById(R.id.eventsSpinner);

        // add listener for whenever a user changes filter
        eventFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (((TextView) adapterView.getChildAt(0)) != null && ((TextView) adapterView.getChildAt(0)) != null) {
                    ((TextView) adapterView.getChildAt(0)).setTextSize(18);
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
                    getEventsJSONfile();
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                // this doesn't ever happen but i need to override the virtual class
            }
        });
    }
}
