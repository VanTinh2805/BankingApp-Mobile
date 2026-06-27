package com.example.proiectmobilebanking.network.model;

public class LoginResponse {

    private String token;
    private UserInfo user;

    public String getToken() {
        return token;
    }

    public UserInfo getUser() {
        return user;
    }
}