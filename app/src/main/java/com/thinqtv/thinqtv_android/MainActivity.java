package com.thinqtv.thinqtv_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String URL_STR = "https://meet.jit.si";
    private static final String THINQTV_ROOM_NAME = "ThinqTV";
    private static final String screenNameKey = "com.thinqtv.thinqtv_android.SCREEN_NAME";;
    private static String lastScreenNameStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        URL serverURL;

        try {
            serverURL = new URL(URL_STR);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid server URL.");
        }
        JitsiMeetConferenceOptions defaultOptions
                = new JitsiMeetConferenceOptions.Builder()
                .setServerURL(serverURL)
                .setWelcomePageEnabled(false)
                .build();
        JitsiMeet.setDefaultConferenceOptions(defaultOptions);

        if (savedInstanceState != null) {
            lastScreenNameStr = savedInstanceState.getString(screenNameKey);
            EditText screenName = findViewById(R.id.screenName);
            screenName.setText(lastScreenNameStr);
        }
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
            userInfoBundle.putString("displayName", lastScreenNameStr);
            optionsBuilder.setUserInfo(new JitsiMeetUserInfo(userInfoBundle));
        }

        JitsiMeetConferenceOptions options = optionsBuilder.build();
        JitsiMeetActivity.launch(this, options);
    }

    // go to get involved page
    public void goGetInvolved(View V){
        Intent i = new Intent(this, GetInvolved.class);
        startActivity(i);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        if (lastScreenNameStr.length() > 0) {
            outState.putString(screenNameKey, lastScreenNameStr);
        }

        super.onSaveInstanceState(outState, outPersistentState);
    }
}
