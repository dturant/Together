package com.example.dagna.together.helpers;

/**
 * Created by Dagna on 18/04/2016.
 */
public class Users {

    private String login,id;

    public Users(String id,String login){
        this.setId(id);
        this.setLogin(login);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
