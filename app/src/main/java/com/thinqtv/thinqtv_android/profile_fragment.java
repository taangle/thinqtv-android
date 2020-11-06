package com.thinqtv.thinqtv_android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.zxing.common.BitMatrix;
import com.thinqtv.thinqtv_android.data.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class profile_fragment extends Fragment {

    private GoogleSignInClient googleSignInClient;
    public profile_fragment(GoogleSignInClient googleSignInClient) {
        this.googleSignInClient = googleSignInClient;
    }

    public static profile_fragment newInstance(GoogleSignInClient googleSignInClient) {
        profile_fragment fragment = new profile_fragment(googleSignInClient);
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
        return inflater.inflate(R.layout.profile_fragment, container, false);
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState)
    {
        // Connect the Browse Schedule button
        final Button browseScheduleBtn = getView().findViewById(R.id.browse_schedule);
        browseScheduleBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                goHome(v);
            }
        });

        // Connect the Schedule a Conversation button
        Button scheduleConversationButton = view.findViewById(R.id.scheduleConversation);
        scheduleConversationButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddEventActivity.class);
            startActivity(intent);
        });

        // Connect the control panel button
        Button controlPanelBtn = view.findViewById(R.id.controlPanelBtn);
        controlPanelBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ControlPanelActivity.class);
            startActivity(intent);
        });

        // Connect the sign out button
        Button signOutBtn = view.findViewById(R.id.signOutBtn);
        signOutBtn.setOnClickListener(v -> {
            logout();
        });

        // Update username text
        TextView usernameTV = view.findViewById(R.id.username);
        usernameTV.setText(UserRepository.getInstance().getLoggedInUser().getUserInfo().get("name"));

        // Update profile picture
        class UploadTask extends AsyncTask<String, String, String> {

            @Override
            protected String doInBackground(String... strings) {



//                I DONT KNOW HOW TO DO THIS BUT I REALLY WISH I DID :(

                return null;
            }
        }
        UploadTask updatePic = new UploadTask();
        updatePic.execute();
    }

    public void goHome(View v){
        Fragment fragment = conversation_fragment.newInstance();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        BottomNavigationView bottomNavigation = getView().findViewById(R.id.bottom_navigation);
        MainActivity.bottomNavigation.setSelectedItemId(R.id.action_conversation);
    }

    public void logout() {
        UserRepository.getInstance().logout();
        LoginManager.getInstance().logOut();
        googleSignInClient.signOut()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i(getString(R.string.google_sign_in_tag), "Signed out of Google accountl");
                    }
                });
        ((MainActivity)getActivity()).openFragment(welcome_fragment.newInstance());
    }
}
