package com.thinqtv.thinqtv_android;

import android.content.Intent;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.thinqtv.thinqtv_android.data.DataSource;
import com.thinqtv.thinqtv_android.data.UserRepository;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigation;
    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent loadSavedUser = new Intent(this, StartupLoadingActivity.class);
        startActivity(loadSavedUser);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // If a user is logged in, use their name. Otherwise, try to find a name elsewhere.
        if (UserRepository.getInstance().isLoggedIn())
        {
            openFragment(conversations_fragment.newInstance());
            bottomNavigation.setSelectedItemId(R.id.action_conversations);
        }
        else
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
            openFragment(welcome_fragment.newInstance());
            bottomNavigation.setSelectedItemId(R.id.action_profile);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // When resuming from profile/login page, check if user is logged in and choose fragment accordingly.
        if (bottomNavigation.getSelectedItemId() == R.id.action_profile)
            if (UserRepository.getInstance().getLoggedInUser() == null) {
                openFragment(welcome_fragment.newInstance());
            } else {
                openFragment(profile_fragment.newInstance(mGoogleSignInClient));
            }
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = item ->
    {
        switch (item.getItemId()) {
            case R.id.action_dropin:
                openFragment(dropin_fragment.newInstance());
                return true;
            case R.id.action_conversations:
                openFragment(conversations_fragment.newInstance());
                return true;
            case R.id.action_aboutus:
                openFragment(aboutus_fragment.newInstance("",""));
                return true;
            case R.id.action_profile:
                if (UserRepository.getInstance().isLoggedIn())
                    openFragment(profile_fragment.newInstance(mGoogleSignInClient));
                else
                    openFragment(welcome_fragment.newInstance());
                return true;
        }
        return false;
    };
}