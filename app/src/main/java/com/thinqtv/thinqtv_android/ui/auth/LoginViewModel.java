package com.thinqtv.thinqtv_android.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.content.Context;
import android.util.Patterns;

import com.thinqtv.thinqtv_android.data.Result;
import com.thinqtv.thinqtv_android.data.UserRepository;
import com.thinqtv.thinqtv_android.R;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<Result> loginResult = new MutableLiveData<>();
    private final UserRepository userRepository;

    LoginViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<Result> getLoginResult() {
        return loginResult;
    }

    public void setResult(Result result) {
        loginResult.setValue(result);
    }

    void login(String username, String password, Context context) {
        userRepository.login(username, password, context, this);
    }

    void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_email, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.password_empty));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    private boolean isUserNameValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 0;
    }
}