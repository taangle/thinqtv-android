package com.thinqtv.thinqtv_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class getInvolved2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_involved2);
    }

    public void goHome(View v){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

}
