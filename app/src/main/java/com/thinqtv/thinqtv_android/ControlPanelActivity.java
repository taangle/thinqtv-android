package com.thinqtv.thinqtv_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.thinqtv.thinqtv_android.data.UserRepository;
import com.thinqtv.thinqtv_android.data.model.LoggedInUser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

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
