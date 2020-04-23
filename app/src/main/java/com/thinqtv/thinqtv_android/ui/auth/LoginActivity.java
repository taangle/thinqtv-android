package com.thinqtv.thinqtv_android.ui.auth;

import android.app.Activity;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.thinqtv.thinqtv_android.R;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new AuthViewModelFactory())
                .get(LoginViewModel.class);

        final EditText emailEditText = findViewById(R.id.email);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.sign_in);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        final Button registerButton = findViewById(R.id.sign_up);
        final TextView errorTextView = findViewById(R.id.error);

        // Can't submit the initial empty form.
        loginButton.setEnabled(false);

        // Check if the data in the form is valid each time the loginFormState changes.
        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                emailEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        // Respond to the login once the result is made available.
        loginViewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.INVISIBLE);
            if (!loginResult.isSuccess() && loginResult.getData() != null) {
                List<?> errorList = (List<?>)loginResult.getData();
                showLoginFailed(errorList, errorTextView);
            }
            if (loginResult.isSuccess()) {
                setResult(Activity.RESULT_OK);

                // Complete and destroy login activity only on a successful login.
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Send the new text to the loginViewModel, which will check if it's valid.
                loginViewModel.loginDataChanged(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        Context context = this;
        loginButton.setOnClickListener(view -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginViewModel.login(emailEditText.getText().toString(),
                    passwordEditText.getText().toString(),
                    context);
        });

        registerButton.setOnClickListener(view -> goToRegister());
    }

    // Display error at the top of the login screen.
    private void showLoginFailed(List<?> errorList, TextView errorTextView) {
        if (errorList.size() == 0) {
            errorTextView.setText(R.string.login_failed);
        }
        else {
            // If multiple errors occur, show the first one.
            errorTextView.setText((Integer)errorList.get(0));
        }
    }

    private void goToRegister() {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }
}