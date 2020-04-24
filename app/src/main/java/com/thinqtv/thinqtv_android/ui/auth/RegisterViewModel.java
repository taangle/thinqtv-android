package com.thinqtv.thinqtv_android.ui.auth;

import android.content.Context;
import android.util.Patterns;

import com.thinqtv.thinqtv_android.R;
import com.thinqtv.thinqtv_android.data.Result;
import com.thinqtv.thinqtv_android.data.UserRepository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegisterViewModel extends ViewModel {

    private final MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private final MutableLiveData<Result> registerResult = new MutableLiveData<>();
    private final UserRepository userRepository;

    RegisterViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    LiveData<RegisterFormState> getRegisterFormState() {
        return registerFormState;
    }
    LiveData<Result> getResult() {
        return registerResult;
    }

    public void setResult(Result result) {
        registerResult.setValue(result);
    }

    void registerDataChanged(String email, String name, String permalink,
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
    }

    void register(String email, String name, String permalink, String password, Context context) {
        userRepository.register(email, name, permalink, password, context, this);
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isNameValid(String name) {
        return (name != null && name.trim().length() > 0);
    }

    private boolean isPermalinkValid(String permalink) {
        return (permalink != null && permalink.trim().length() > 0);
    }

    private boolean isPasswordValid(String password) {
        return (password != null && password.trim().length() >= 8);
    }

    private boolean isPasswordConfirmationValid(String password, String passwordConfirmation) {
        return password.equals(passwordConfirmation);
    }
}