package com.terenko.fileserver.Layout.ui.login;


import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.terenko.fileserver.API.ApiService;
import com.terenko.fileserver.App;
import com.terenko.fileserver.R;


public class RegisterViewModel extends ViewModel {

    private MutableLiveData<RegisterFormState> registerFormData = new MutableLiveData<>();
    private MutableLiveData<RegisterResult> registerResult = new MutableLiveData<>();
    private ApiService apiService;

    RegisterViewModel() {

    }

    LiveData<RegisterFormState> getRegisterFormState() {
        return registerFormData;
    }

    LiveData<RegisterResult> getRegisterResult() {
        return registerResult;
    }

    public void register(String username, String password,App app) {

        app.register(username,password, registerResult);
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            registerFormData.setValue(new RegisterFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            registerFormData.setValue(new RegisterFormState(null, R.string.invalid_password));
        } else {
            registerFormData.setValue(new RegisterFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 3;
    }
    protected void onCleared() {


    }
}