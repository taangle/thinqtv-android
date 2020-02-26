package com.thinqtv.thinqtv_android;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        URL serverURL;
        try {
            serverURL = new URL("https://meet.jit.si");
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
    }

    public void onButtonClick(View v) {
        // extract screen name and conference name from EditText fields
        EditText screenName = findViewById(R.id.screenName);
        String screenNameStr = screenName.getText().toString();
        EditText conferenceName = findViewById(R.id.conferenceName);
        String conferenceNameStr = conferenceName.getText().toString();

        // join conference if a conference name has been entered
        if (conferenceNameStr.length() > 0) {
            // populate user Bundle if a screen name has been entered
            Bundle userInfoBundle = new Bundle();
            if (screenNameStr.length() > 0) {
                Log.d("SCREEN_NAME", screenNameStr);
                userInfoBundle.putString("displayName", screenNameStr);
            }

            JitsiMeetConferenceOptions options
                    = new JitsiMeetConferenceOptions.Builder()
                    .setRoom(conferenceNameStr)
                    .setUserInfo(new JitsiMeetUserInfo(userInfoBundle))
                    .build();
            JitsiMeetActivity.launch(this, options);
        }
    }

    // go to get involved page
    public void goGetInvolved(View V){
        Intent i = new Intent(this, GetInvolved.class);
        startActivity(i);
    }
}
