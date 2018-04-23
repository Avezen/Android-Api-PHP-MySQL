package com.example.maniekcs1995.defotapp;

/**
 * Created by maniekcs1995 on 2018-04-21.
 */

class User {
    private int id;
    private String login;
    private String password;
    private String email;
    private boolean isAdmin;
    private boolean isActive;

    public User(int id, String login, String password, String email, boolean isAdmin, boolean isActive) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.email = email;
        this.isAdmin = isAdmin;
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean isActive() {
        return isActive;
    }
}
