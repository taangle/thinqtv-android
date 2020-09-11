package com.thinqtv.thinqtv_android;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link conversations_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class conversations_fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    boolean eventsExpanded = false; //used to expand and collapse the Events ScrollView, changes with each click
    private static final String THINQTV_ROOM_NAME = "ThinqTV";
    private static final String screenNameKey = "com.thinqtv.thinqtv_android.SCREEN_NAME";
    private static String lastScreenNameStr = "";

    public conversations_fragment() {
        // Required empty public constructor
    }

    public void expandEventsClick(View v)
    {
        // Link the header TextView
        TextView header = (TextView) getView().findViewById(R.id.upcoming_events_header);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) header.getLayoutParams();

        // Link any buttons
        TextView carrot = (TextView) getView().findViewById(R.id.expandButton);
        Button joinButton = getView().findViewById(R.id.defaultJoinButton);

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

    // Button listener for "Join Conversation" button that connects to default ThinQ.TV chatroom
    public void onJoinClick(View v) {
        // extract screen name and conference name from EditText fields
        EditText screenName = getView().findViewById(R.id.screenName);
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
        Intent intent = new Intent(getContext().getApplicationContext(), ConferenceActivity.class);
        intent.setAction("org.jitsi.meet.CONFERENCE");
        intent.putExtra("JitsiMeetConferenceOptions", options);
        startActivity(intent);
        getActivity().finish();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment conversations_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static conversations_fragment newInstance(String param1, String param2) {
        conversations_fragment fragment = new conversations_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.conversations_fragment, container, false);
    }
}
