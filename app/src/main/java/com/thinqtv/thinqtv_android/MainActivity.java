package com.thinqtv.thinqtv_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.thinqtv.thinqtv_android.data.UserRepository;

public class MainActivity extends AppCompatActivity {
    public static BottomNavigationView bottomNavigation;
    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent loadSavedUser = new Intent(this, StartupLoadingActivity.class);
        startActivity(loadSavedUser);

        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // If a user is logged in, use their name. Otherwise, try to find a name elsewhere.
        if (!UserRepository.getInstance().isLoggedIn())
        {
            // if Google is signed in but user is not, just sign out of Google
            GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
            if (googleSignInAccount != null)
                mGoogleSignInClient.signOut()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.i(getString(R.string.google_sign_in_tag), "Signed out of Google account");
                            }
                        });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        putSharedPrefs();
    }

    private void putSharedPrefs() {
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        profile_fragment profile = (profile_fragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.profile_fragment));
        if (profile != null && profile.isVisible()) {
            editor.putString(getString(R.string.fragment), getString(R.string.profile_fragment));
            editor.apply();
            return;
        }

        aboutus_fragment aboutus = (aboutus_fragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.aboutus_fragment));
        if (aboutus != null && aboutus.isVisible()) {
            editor.putString(getString(R.string.fragment), getString(R.string.aboutus_fragment));
            editor.apply();
            return;
        }

        inviteus_fragment inviteus = (inviteus_fragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.inviteus_fragment));
        if (inviteus != null && inviteus.isVisible()) {
            editor.putString(getString(R.string.fragment), getString(R.string.inviteus_fragment));
            editor.apply();
            return;
        }

        welcome_fragment welcome = (welcome_fragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.welcome_fragment));
        if (welcome != null && welcome.isVisible()) {
            editor.putString(getString(R.string.fragment), getString(R.string.welcome_fragment));
            editor.apply();
            return;
        }

        editor.putString(getString(R.string.fragment), getString(R.string.conversation_fragment));
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        // Disable back button for fragments INSIDE this activity
        // Not disabled between activities.
        this.moveTaskToBack(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Open the proper fragment depending on login
        if (UserRepository.getInstance().isLoggedIn()) {
            openFragmentBasedOnSharedPrefs();
        } else {
            bottomNavigation.setSelectedItemId(R.id.action_profile);
        }
    }

    private void openFragmentBasedOnSharedPrefs() {
        String fragment = getPreferences(Context.MODE_PRIVATE).getString(getString(R.string.fragment), getString(R.string.conversation_fragment));
        if (fragment.equals(getString(R.string.aboutus_fragment))) {
            bottomNavigation.setSelectedItemId(R.id.action_aboutus);
        }
        else if (fragment.equals(getString(R.string.inviteus_fragment))) {
            bottomNavigation.setSelectedItemId(R.id.action_inviteus);
        }
        else if (fragment.equals(getString(R.string.profile_fragment))) {
            bottomNavigation.setSelectedItemId(R.id.action_profile);
        }
        else if (fragment.equals(getString(R.string.welcome_fragment))) {
            bottomNavigation.setSelectedItemId(R.id.action_profile);
        }
        else {
            bottomNavigation.setSelectedItemId(R.id.action_conversation);
        }
    }

    public void openFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment, tag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = item ->
    {
        switch (item.getItemId()) {
            case R.id.action_conversation:
                openFragment(conversation_fragment.newInstance(), getString(R.string.conversation_fragment));
                return true;
            case R.id.action_inviteus:
                openFragment(inviteus_fragment.newInstance(), getString(R.string.inviteus_fragment));
                return true;
            case R.id.action_aboutus:
                openFragment(aboutus_fragment.newInstance("",""), getString(R.string.aboutus_fragment));
                return true;
            case R.id.action_profile:
                if (UserRepository.getInstance().isLoggedIn())
                    openFragment(profile_fragment.newInstance(mGoogleSignInClient), getString(R.string.profile_fragment));
                else
                    openFragment(welcome_fragment.newInstance(), getString(R.string.welcome_fragment));
                return true;
        }
        return false;
    };
}