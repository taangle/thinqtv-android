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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.thinqtv.thinqtv_android.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import co.apptailor.googlesignin.RNGoogleSigninModule;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private GoogleSignInClient mGoogleSignInClient;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Override
    protected void onStart() {
        super.onStart();

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (googleSignInAccount != null) {
            // this really should never happen, but if it does just sign out
            Log.i(getString(R.string.google_sign_in_tag), "Already logged in with Google: " + googleSignInAccount.getEmail());
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i(getString(R.string.google_sign_in_tag), "Signed out of Google account");
                        }
                    });
        }
    }

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

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.google_sign_in_button).setOnClickListener(view -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RNGoogleSigninModule.RC_SIGN_IN);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RNGoogleSigninModule.RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                Log.i(getString(R.string.google_sign_in_tag), "Successfully got GoogleSignInAccount");
                String idToken = account.getIdToken();
                Log.d(getString(R.string.google_sign_in_tag), "ID token: " + idToken);

                validateTokenWithServer(idToken, account);
            }
            else {
                Log.i(getString(R.string.google_sign_in_tag), "GoogleSignInAccount was null");
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(getString(R.string.google_sign_in_tag), "signInResult:failed code=" + e.getStatusCode()
                + ", message=" + e.getMessage(), e);
        }
    }

    private void validateTokenWithServer(String idToken, GoogleSignInAccount account) {
        String json = String.format("{\"idToken\": \"%s\"}", idToken);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(getString(R.string.server_url_google_sign_in))
                .post(body)
                .build();

        Log.i(getString(R.string.google_sign_in_tag), "Sending ID token to backend");
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(getString(R.string.google_sign_in_tag), "Error sending ID token to backend.", e);
                mGoogleSignInClient.signOut()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.i(getString(R.string.google_sign_in_tag), "Signed out of Google account");
                            }
                        });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.i(getString(R.string.google_sign_in_tag), "Signed in as: " + response.body().string());
                    // TODO actually log in to the user repository
                }
                else {
                    Log.w(getString(R.string.google_sign_in_tag), "Failed to sign in with Google, response code: "  + response.code());
                    mGoogleSignInClient.signOut()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.i(getString(R.string.google_sign_in_tag), "Signed out of Google account");
                                }
                            });
                }
            }
        });
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