package com.terenko.fileserver.Layout.ui.login;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
public class RegisterResult {
    @Nullable
    private LoggedInUserView success;
    @Nullable
    private Integer error;

   public RegisterResult(@Nullable Integer error) {
        this.error = error;
    }

    public RegisterResult(@Nullable LoggedInUserView success) {
        this.success = success;
    }

    @Nullable
    LoggedInUserView getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}