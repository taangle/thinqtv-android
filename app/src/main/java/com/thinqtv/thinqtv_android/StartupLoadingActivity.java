package com.thinqtv.thinqtv_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.thinqtv.thinqtv_android.data.UserRepository;

public class StartupLoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup_loading);

        UserRepository.getInstance().loadSavedUser(this);
    }
}
