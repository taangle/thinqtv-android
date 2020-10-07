package com.thinqtv.thinqtv_android;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.thinqtv.thinqtv_android.ui.auth.LoginActivity;

public class welcome_fragment extends Fragment {


    public welcome_fragment() {
        // Required empty public constructor
    }

    public static welcome_fragment newInstance() {
        welcome_fragment fragment = new welcome_fragment();
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
        return inflater.inflate(R.layout.welcome_fragment, container, false);
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState)
    {
        Button loginBtn = getView().findViewById(R.id.button);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gotoSignIn();
            }
        });
    }

    public void gotoSignIn()
    {
        Intent i = new Intent(getContext(), LoginActivity.class);
        startActivity(i);
    }
}
