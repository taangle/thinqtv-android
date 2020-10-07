package com.thinqtv.thinqtv_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView;


public class Profile extends AppCompatActivity implements AdapterView.OnItemSelectedListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //listen to the browse schedule button
       final Button button = findViewById(R.id.browse_schedule);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                goHome(v);
            }
        });

        Spinner spinner = findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ProfileSpinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    public void goHome(View v){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        System.out.println(" ''" + v + " ''");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch(position){
            case 0:
                //goHome(view); //HOME WHILE SIGN IN
                break;
            case 1:
                goHome(view); //HOME WHILE SIGN IN
                break;
            case 2:
              //  goHome(view); //CONTROL PANEL
                break;
            case 3:
                goHome(view);  //SIGN OUT
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
