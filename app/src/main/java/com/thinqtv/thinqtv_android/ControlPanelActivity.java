package com.thinqtv.thinqtv_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ControlPanelActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_control_panel);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Button profileSettingsButton = findViewById(R.id.profile_button);
        Button accountSettingsButton = findViewById(R.id.account_button);
        Button editConversationsButton = findViewById(R.id.conversations_button);

        profileSettingsButton.setOnClickListener(view -> {
            Intent i = new Intent(this, ProfileSettingsActivity.class);
            startActivity(i);
        });
        accountSettingsButton.setOnClickListener(view -> {
            Intent i = new Intent(this, AccountSettingsActivity.class);
            startActivity(i);
        });
        editConversationsButton.setOnClickListener(view -> {
            // Intent i = new Intent(this, EditConversationsActivity.class);
            Intent i = new Intent(this, AddEventActivity.class);
            startActivity(i);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




}
