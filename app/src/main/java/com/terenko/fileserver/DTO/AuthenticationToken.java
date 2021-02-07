package com.terenko.fileserver.DTO;

public class AuthenticationToken  implements DTO{
    String username;
    String token;
    private final String className;

    public AuthenticationToken( ) {
        this.className = getClass().getName();
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String getClassName() {
        return className;
    }
}
