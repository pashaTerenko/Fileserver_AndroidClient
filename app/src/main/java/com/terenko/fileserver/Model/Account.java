package com.terenko.fileserver.Model;

public class Account {
    private  String login;
    private  String token;

    public Account() {

    }

    public Account(String login, String uuid, String pictureUrl) {
        this.login = login;

    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
