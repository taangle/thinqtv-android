package com.thinqtv.thinqtv_android;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.VideoView;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //VideoView mainViewer;
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

        // COMMENTED OUT BECAUSE I'M NOT SURE IF IT ACTUALLY LOOKS GOOD. GET TEAM OPINION
        //mainViewer = (VideoView) findViewById(R.id.videoview);
        //Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.crowdpubcommercialv_1);
        //mainViewer.setVideoURI(uri);
        //mainViewer.start();
    }

// COMMENTED OUT BECAUSE BUTTON IS NOT USEFUL ANYMORE
/*
    public void onButtonClick(View v) {
        EditText editText = findViewById(R.id.conferenceName);

        String text = editText.getText().toString();
        System.out.println(text);

        if (text.length() > 0) {
            JitsiMeetConferenceOptions options
                    = new JitsiMeetConferenceOptions.Builder()
                    .setRoom(text)
                    .build();
            JitsiMeetActivity.launch(this, options);
        }
    }
*/

    //Button listener for "Join Conversation" button that connects to default ThinQ.TV chatroom
    public void onButtonClickDefault(View v) {
        JitsiMeetConferenceOptions options
                = new JitsiMeetConferenceOptions.Builder()
                .setRoom("ThinqTV")
                .build();
        JitsiMeetActivity.launch(this, options);
    }
}
