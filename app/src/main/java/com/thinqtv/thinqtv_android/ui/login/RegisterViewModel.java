package com.thinqtv.thinqtv_android.ui.login;

import android.util.Patterns;

import com.thinqtv.thinqtv_android.R;
import com.thinqtv.thinqtv_android.data.LoginRepository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegisterViewModel extends ViewModel {

    private MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    RegisterViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<RegisterFormState> getRegisterFormState() {
        return registerFormState;
    }
    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(LoginResult result) {
        loginResult.setValue(result);
    }

    public void registerDataChanged(String email, String name, String permalink,
                                    String password, String passwordConfirmation) {
        boolean validForm = true;
        if (!isEmailValid(email)) {
            registerFormState.setValue(new RegisterFormState(R.string.invalid_email, null, null, null, null));
            validForm = false;
        }
        if (!isNameValid(name)) {
            registerFormState.setValue(new RegisterFormState(null, R.string.invalid_name, null, null, null));
            validForm = false;
        }
        if (!isPermalinkValid(permalink)) {
            registerFormState.setValue(new RegisterFormState(null, null, R.string.invalid_permalink, null, null));
            validForm = false;
        }
        if (!isPasswordValid(password)) {
            registerFormState.setValue(new RegisterFormState(null, null, null, R.string.invalid_password, null));
            validForm = false;
        }
        if (!isPasswordConfirmationValid(password, passwordConfirmation)) {
            registerFormState.setValue(new RegisterFormState(null, null, null, null, R.string.invalid_password_confirmation));
            validForm = false;
        }
        registerFormState.setValue(new RegisterFormState(validForm));
        /*else {
            registerFormState.setValue(new RegisterFormState(true));
        }*/
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private boolean isNameValid(String name) {
        return true;
    }
    private boolean isPermalinkValid(String permalink) {
        return true;
    }
    private boolean isPasswordValid(String password) {
        return (password != null && password.trim().length() >= 8);
    }
    private boolean isPasswordConfirmationValid(String password, String passwordConfirmation) {
        return password.equals(passwordConfirmation);
    }
}
