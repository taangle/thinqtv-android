package com.thinqtv.thinqtv_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

public class ConferenceActivity extends JitsiMeetActivity {
    private static final String URL_STR = "https://meet.jit.si";

    @Override
    protected void initialize() {
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
                .setFeatureFlag("pip.enabled", true)
                .build();
        JitsiMeet.setDefaultConferenceOptions(defaultOptions);

        super.initialize();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // return to the main activity
        startActivity(new Intent(this, MainActivity.class));
    }
}