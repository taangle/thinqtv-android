package com.thinqtv.thinqtv_android.ui.auth;

import androidx.annotation.Nullable;

public class RegisterFormState {
    @Nullable
    private Integer emailError;
    @Nullable
    private Integer nameError;
    @Nullable
    private Integer permalinkError;
    @Nullable
    private Integer passwordError;
    @Nullable
    private Integer passwordConfirmationError;
    private boolean isDataValid;

    RegisterFormState(@Nullable Integer emailError, @Nullable Integer nameError,
                      @Nullable Integer permalinkError, @Nullable Integer passwordError,
                      @Nullable Integer passwordConfirmationError) {
        this.emailError = emailError;
        this.nameError = nameError;
        this.permalinkError = permalinkError;
        this.passwordError = passwordError;
        this.passwordConfirmationError = passwordConfirmationError;
        this.isDataValid = false;
    }

    RegisterFormState(boolean isDataValid) {
        this.emailError = null;
        this.nameError = null;
        this.permalinkError = null;
        this.passwordError = null;
        this.passwordConfirmationError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getEmailError() {
        return emailError;
    }
    @Nullable
    Integer getNameError() {
        return nameError;
    }
    @Nullable
    Integer getPermalinkError() {
        return permalinkError;
    }
    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }
    @Nullable
    Integer getPasswordConfirmationError() {
        return passwordConfirmationError;
    }


    boolean isDataValid() {
        return isDataValid;
    }
}
