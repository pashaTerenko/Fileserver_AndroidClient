package com.terenko.fileserver;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.terenko.fileserver.API.ApiService;
import com.terenko.fileserver.API.ApiServiceLogin;
import com.terenko.fileserver.DTO.AuthenticationRequestDto;
import com.terenko.fileserver.DTO.AuthenticationToken;
import com.terenko.fileserver.DTO.CatalogDTO;
import com.terenko.fileserver.DTO.Responce;
import com.terenko.fileserver.Layout.ui.login.LoggedInUserView;
import com.terenko.fileserver.Layout.ui.login.LoginResult;
import com.terenko.fileserver.Layout.ui.login.RegisterResult;
import com.terenko.fileserver.Model.Account;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class App extends Application {
    final String TOKEN = "token";
    final String USERNAME = "username";
    final String PASSWORD = "password";
    ApiServiceLogin apiServiceLogin;
    ApiService apiService;
    MutableLiveData<Account> account;
    boolean isAuth = false;
    MutableLiveData<CatalogDTO> currentCatalog;
    MutableLiveData<String> token;
    final String SAVED_STATE = "saved_state";

    @Override
    public void onCreate() {
        super.onCreate();
        apiServiceLogin = new ApiServiceLogin();
        account = new MutableLiveData<>();
        currentCatalog=new MutableLiveData<>();
        token=new MutableLiveData<>();

        loadCredentials();
        token.observeForever(new Observer<String>() {
            @Override
            public void onChanged(String s) {
                apiService = new ApiService(token.getValue());

            }
        });
    }

    public ApiServiceLogin getApiServiceLogin() {
        return apiServiceLogin;
    }

    //TODO:добавить обработку null в активити
    public ApiService getApi() {
        if (isAuth) {
            if (apiService == null) {
                apiService = new ApiService(token.getValue());
            }
            return apiService;
        } else {

            return null;
        }
    }

    public void saveCredentials(String login, String password) {
        SharedPreferences sp = getSharedPreferences(SAVED_STATE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putString(USERNAME,login);
        e.putString(PASSWORD,password);
        e.commit();

    }

    public void loadCredentials() {
        SharedPreferences sp = getSharedPreferences(SAVED_STATE,
                Context.MODE_PRIVATE);

        String username= sp.getString(USERNAME,"");
        String password= sp.getString(PASSWORD,"");
        if(!username.equals("")&&!password.equals("")){
            startlogin(username,password);
        }

    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void login(String username, String password, MutableLiveData<LoginResult> loginResultMutableLiveData) {
        AuthenticationRequestDto authenticationRequestDto = new AuthenticationRequestDto();
        authenticationRequestDto.setUsername(username);
        authenticationRequestDto.setPassword(password);
        saveCredentials(username,password);
        apiServiceLogin.getApi().login(authenticationRequestDto).enqueue(new Callback<Responce>() {
            @Override
            public void onResponse(Call<Responce> call, @Nullable Response<Responce> response) {
                if (response.body().getStatusCode() != 200) {
                    loginResultMutableLiveData.setValue(new LoginResult(R.string.badCredential));
                    return;
                }
                AuthenticationToken authenticationToken = (AuthenticationToken) response.body().getDto();
                Account accountNew = new Account();
                accountNew.setLogin(authenticationToken.getUsername());
                accountNew.setToken(authenticationToken.getToken());
                account.setValue(accountNew);
                isAuth = true;
                loginResultMutableLiveData.setValue(new LoginResult(new LoggedInUserView(authenticationToken.getUsername())));
                token.setValue(authenticationToken.getToken());
                apiService = new ApiService(token.getValue());
            }

            @Override
            public void onFailure(Call<Responce> call, Throwable t) {
                loginResultMutableLiveData.setValue(new LoginResult(R.string.badCredential));
            }
        });
    }
    public void startlogin(String username, String password) {
        AuthenticationRequestDto authenticationRequestDto = new AuthenticationRequestDto();
        authenticationRequestDto.setUsername(username);
        authenticationRequestDto.setPassword(password);

        apiServiceLogin.getApi().login(authenticationRequestDto).enqueue(new Callback<Responce>() {
            @Override
            public void onResponse(Call<Responce> call, @Nullable Response<Responce> response) {
                if (response.body().getStatusCode() != 200) {
                    return;
                }
                AuthenticationToken authenticationToken = (AuthenticationToken) response.body().getDto();
                Account accountNew = new Account();
                accountNew.setLogin(authenticationToken.getUsername());
                accountNew.setToken(authenticationToken.getToken());
                account.setValue(accountNew);
                isAuth = true;
                token.setValue(authenticationToken.getToken());
            }

            @Override
            public void onFailure(Call<Responce> call, Throwable t) {
            }
        });
    }

    public void register(String username, String password, MutableLiveData<RegisterResult> registerResultMutableLiveData) {
        AuthenticationRequestDto authenticationRequestDto = new AuthenticationRequestDto();
        authenticationRequestDto.setUsername(username);
        authenticationRequestDto.setPassword(password);
        apiServiceLogin.getApi().register(authenticationRequestDto).enqueue(new Callback<Responce>() {
            @Override
            public void onResponse(Call<Responce> call, Response<Responce> response) {
                if (response.body().getStatusCode() != 200) {
                    registerResultMutableLiveData.setValue(new RegisterResult(R.string.badCredential));
                    return;
                }
                registerResultMutableLiveData.setValue(new RegisterResult(new LoggedInUserView(username)));
            }

            @Override
            public void onFailure(Call<Responce> call, Throwable t) {
                registerResultMutableLiveData.setValue(new RegisterResult(R.string.badCredential));
            }
        });
    }

    public MutableLiveData<Account> getAccount() {
        return account;
    }

    public MutableLiveData<CatalogDTO> getCurrentCatalog() {
        return currentCatalog;
    }
}
