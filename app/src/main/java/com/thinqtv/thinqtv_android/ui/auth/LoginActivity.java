package com.thinqtv.thinqtv_android.ui.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.thinqtv.thinqtv_android.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private CallbackManager callbackManager;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

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
        final LoginButton fbLoginButton = findViewById(R.id.fb_login_button);

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

        // TODO check if user is already logged in with FB
        callbackManager = CallbackManager.Factory.create();
        fbLoginButton.setReadPermissions(Arrays.asList("email"));
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.i(getString(R.string.fb_sign_in_tag), "Successfully logged in with FB. Access token: " + loginResult.getAccessToken().getToken());
                validateAccessTokenWithServer(loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {
                // App code
                Log.i(getString(R.string.fb_sign_in_tag), "Sign in with FB cancelled");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.w(getString(R.string.fb_sign_in_tag), "Sign in with FB threw error", exception);
            }
        });
    }

    private void validateAccessTokenWithServer(String accessToken) {
        String json = String.format("{\"access_token\": \"%s\"}", accessToken);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(getString(R.string.server_url_fb_sign_in))
                .post(body)
                .build();

        Log.i(getString(R.string.fb_sign_in_tag), "Sending access token to backend");
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(getString(R.string.fb_sign_in_tag), "Error sending access token to backend", e);
                // TODO updateUI(null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.i(getString(R.string.fb_sign_in_tag), "Signed in with FB");
                    // TODO updateUI();
                }
                else {
                    Log.w(getString(R.string.fb_sign_in_tag), "Failed to sign in with FB, response code: " + response.code());
                    // TODO updateUI(null);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
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