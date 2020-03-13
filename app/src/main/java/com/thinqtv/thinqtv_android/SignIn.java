package com.thinqtv.thinqtv_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;

public class SignIn extends AppCompatActivity {

    // extract screen email and password from EditText fields
    //Larissa for Sign In page
    private EditText Email;
    private EditText Password;
    private Button Signin;
    private Button SignUp;
    private CheckedTextView Terms;
    private CheckedTextView ResetPassW;
  //  TermscheckedTextView2
    private int counter = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //Get the email and password text from the screen
        Email = (EditText)findViewById(R.id.EmaileditText);
        Password = (EditText)findViewById(R.id.PasswordeditText);

        //Listen to the sign Up button from the sign in page and connect to the collect the inputs
        Signin = (Button)findViewById(R.id.SignInbutton);
        Signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInput(Email.getText().toString(), Password.getText().toString());

            }
        });

        //Listen to the sign Up button from the sign in page and connect to the sign Up page
        SignUp = (Button)findViewById(R.id.SignUpbutton3);
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSignUp(v);

            }
        });

        //Listen to the terms of service text from the sign in page and connect to the Terms page
        Terms = (CheckedTextView) findViewById(R.id.TermscheckedTextView2);
        Terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoTerms(v);

            }
        });

        //Listen to the forgot password text from the sign in page and connect to the Reset Password  page
        ResetPassW = (CheckedTextView) findViewById(R.id.ForgotPasscheckedTextView);
        ResetPassW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // ResetPassword.setBackgroundColor()
                gotoResetPassword(v);
            }
        });

    }


    //Function that validate a pair of entered email and password
    private void checkInput (String email, String passw)
    {
        if((email.equals("admin")) && (passw.equals("1234")))
        {
            Intent i = new Intent(this, Account.class);
            startActivity(i);
        }
        else
        {
            counter--;
            if(counter == 0)  //user is limited to 3 attempt
            {
                Signin.setEnabled(false);
            }
        }
    }

    //Go to the home page
    public void goHome(View v){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    //Go to the Sign up page
    public void gotoSignUp(View V){
        Intent i = new Intent(this, SignUp.class);
        startActivity(i);
    }

    //Go to the Terms of Service page
    public void gotoTerms(View V){
        Intent i = new Intent(this, Terms.class);
        startActivity(i);
    }

    //Go to the Forgot Password page
    public void gotoResetPassword(View V){
        Intent i = new Intent(this, ResetPassword.class);
        startActivity(i);
    }
}
