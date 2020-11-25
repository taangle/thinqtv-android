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

import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.thinqtv.thinqtv_android.R;
import com.thinqtv.thinqtv_android.data.DataSource;
import com.thinqtv.thinqtv_android.data.UserRepository;
import com.thinqtv.thinqtv_android.data.model.LoggedInUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import co.apptailor.googlesignin.RNGoogleSigninModule;
import okhttp3.MediaType;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private CallbackManager callbackManager;
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
                validateAccessTokenWithServer(loginResult.getAccessToken().getToken(), loginResult.getAccessToken().getUserId(), context);
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

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.google_sign_in_button).setOnClickListener(view -> {
            Log.i(getString(R.string.google_sign_in_tag), "google button clicked.");
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RNGoogleSigninModule.RC_SIGN_IN);
        });
    }

    @Override
    public void onResume(){
        super.onResume();

        if (UserRepository.getInstance().isLoggedIn())
            finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);

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

                validateTokenWithServer(idToken, account, this);
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

    private void validateTokenWithServer(String idToken, GoogleSignInAccount account, Context context) {
        JSONObject params = new JSONObject();
        try {
            params.put("id_token", idToken);
        } catch(JSONException e) { // Couldn't form JSON object for request.
            e.printStackTrace();
            Log.e(getString(R.string.google_sign_in_tag), "Error forming request");
            return;
        }

        Log.i(getString(R.string.google_sign_in_tag), "Sending access token to backend");
        JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                getString(R.string.server_url_google_sign_in), params,
                response -> {
                    try {
                        Log.i(getString(R.string.google_sign_in_tag), "Signed in with Google.");
                        JSONObject user = new JSONObject(response.getString("user"));
                        user.put("token", response.getString("token"));
                        UserRepository.getInstance().setLoggedInUser(new LoggedInUser(context, new Gson().fromJson(user.toString(), HashMap.class)));
                        setResult(Activity.RESULT_OK);

                        // Complete and destroy login activity only on a successful login.
                        finish();
                    } catch(JSONException e) {
                        Log.w(getString(R.string.google_sign_in_tag), "Error sending token to backend.");
                        e.printStackTrace();
                        LoginManager.getInstance().logOut();
                    }
                }, error -> {
                    Log.w(getString(R.string.google_sign_in_tag), "whate");
                    if (error.networkResponse != null && error.networkResponse.statusCode == 400 && error.networkResponse.data != null) {
                        try {
                            JSONObject errorJson = new JSONObject(new String(error.networkResponse.data));
                            Log.i(getString(R.string.google_sign_in_tag), errorJson.getString("errors"));
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    LoginManager.getInstance().logOut();
        });

        DataSource.getInstance().addToRequestQueue(request, context);

    }

    private void validateAccessTokenWithServer(String accessToken, String userId, Context context) {
        JSONObject params = new JSONObject();
        try {
            params.put("access_token", accessToken);
            params.put("user_id", userId);
        } catch(JSONException e) { // Couldn't form JSON object for request.
            e.printStackTrace();
            Log.e(getString(R.string.fb_sign_in_tag), "Error forming request");
            return;
        }

        Log.i(getString(R.string.fb_sign_in_tag), "Sending access token to backend");
        JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.POST,
            getString(R.string.server_url_fb_sign_in), params,
            response -> {
                try {
                    Log.i(getString(R.string.fb_sign_in_tag), "Signed in with FB");
                    JSONObject user = new JSONObject(response.getString("user"));
                    user.put("token", response.getString("token"));
                    UserRepository.getInstance().setLoggedInUser(new LoggedInUser(context, new Gson().fromJson(user.toString(), HashMap.class)));
                    setResult(Activity.RESULT_OK);

                    // Complete and destroy login activity only on a successful login.
                    finish();
                } catch(JSONException e) {
                    Log.w(getString(R.string.fb_sign_in_tag), "Error sending access token to backend.");
                    e.printStackTrace();
                    LoginManager.getInstance().logOut();
                }
            }, error -> {
                Log.w(getString(R.string.fb_sign_in_tag), "Failed to sign in with FB, response code: " + error.networkResponse.statusCode);
                if (error.networkResponse != null && error.networkResponse.statusCode == 400 && error.networkResponse.data != null) {
                    try {
                        JSONObject errorJson = new JSONObject(new String(error.networkResponse.data));
                        Log.i(getString(R.string.fb_sign_in_tag), errorJson.getString("errors"));
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }
                LoginManager.getInstance().logOut();
            });

        DataSource.getInstance().addToRequestQueue(request, context);
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