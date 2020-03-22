package com.thinqtv.thinqtv_android.ui.auth;

import androidx.annotation.Nullable;

class RegisterFormState {
    @Nullable
    private final Integer emailError;
    @Nullable
    private final Integer nameError;
    @Nullable
    private final Integer permalinkError;
    @Nullable
    private final Integer passwordError;
    @Nullable
    private final Integer passwordConfirmationError;
    private final boolean isDataValid;

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