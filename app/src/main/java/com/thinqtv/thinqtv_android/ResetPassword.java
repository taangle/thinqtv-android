package com.thinqtv.thinqtv_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.thinqtv.thinqtv_android.ui.login.LoginActivity;

public class ResetPassword extends AppCompatActivity {

    private EditText Email;
    private Button Signin;
    private Button SignUp;
    private Button Sent;
    private TextView update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);


        //Get the email and password text from the screen
        Email = (EditText)findViewById(R.id.editText3);

        //Listen to the sign Up button from the sign in page and connect to the collect the inputs
        Sent = (Button)findViewById(R.id.Sentbutton);
        Sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInput(Email.getText().toString());

            }
        });

        update = (TextView)findViewById(R.id.NotetextView34);

        //Listen to the sign Up button from the sign in page and connect to the collect the inputs
        Signin = (Button)findViewById(R.id.signbutton);
        Signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSignIn(v);

            }
        });

        //Listen to the sign Up button from the sign in page and connect to the sign Up page
        SignUp = (Button)findViewById(R.id.SUpbutton);
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSignUp(v);

            }
        });
    }

    private void checkInput (String email) {
        if (email.equals("lpokamep@asu.edu")) {
            update.setText("Email Sent! ");
        }
    }

    //Go to the Sign up page
    public void gotoSignUp(View V){
    }

    //Go to the Sign up page
    public void gotoSignIn(View V){
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }
}
