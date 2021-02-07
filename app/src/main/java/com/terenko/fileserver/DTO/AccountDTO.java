package com.terenko.fileserver.DTO;


public class AccountDTO implements DTO{
    private final String className= getClass().getName();
    private final String login;
    private final String uuid;
    private final String pictureUrl;

    public AccountDTO(String name, String uuid, String pictureUrl) {
        this.login = name;
        this.uuid = uuid;
        this.pictureUrl = pictureUrl;
    }


    @Override
    public String getClassName() {
        return className;
    }
}
