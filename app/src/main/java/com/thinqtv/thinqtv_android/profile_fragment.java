package com.thinqtv.thinqtv_android;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.thinqtv.thinqtv_android.data.UserRepository;
import com.thinqtv.thinqtv_android.ui.auth.LoginActivity;

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
        final Button button = getView().findViewById(R.id.browse_schedule);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                goHome(v);
            }
        });

        Spinner spinner = getView().findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.ProfileSpinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){
                    case 0:
                        //goHome(view); //HOME WHILE SIGN IN
                        break;
                    case 1:
                        logout();
                        break;
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                // this doesn't ever happen but i need to override the virtual class
            }
        });
    }

    public void goHome(View v){
        Intent i = new Intent(getContext(), MainActivity.class);
        startActivity(i);
        System.out.println(" ''" + v + " ''");
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
