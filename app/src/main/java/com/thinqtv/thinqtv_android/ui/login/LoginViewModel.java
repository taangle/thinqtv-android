package com.thinqtv.thinqtv_android.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.content.Context;
import android.util.Patterns;

import com.thinqtv.thinqtv_android.data.LoginRepository;
import com.thinqtv.thinqtv_android.R;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(LoginResult result) {
        loginResult.setValue(result);
    }

    public void login(String username, String password, Context context) {

        loginRepository.login(username, password, context, this);
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_email, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.password_empty));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        /*
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.E
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }*/
    }

    private boolean isPasswordValid(String password) {
        // return password != null && password.trim().length() >= 8; Should we validate password for login?
        return password != null && password.trim().length() > 0;
    }
}
