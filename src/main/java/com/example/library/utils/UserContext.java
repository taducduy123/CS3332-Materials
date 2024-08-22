package com.example.library.utils;

public class UserContext {
    private static UserContext instance;
    private String role;
    private String username;
    private String readerId;

    public String getReaderId() {
        return readerId;
    }

    public void setReaderId(String readerId) {
        this.readerId = readerId;
    }

    private UserContext() {
    }

    public static  UserContext getInstance() {
        if (instance == null) {
            instance = new UserContext();
        }
        return instance;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getUsername() {
        return username;
    }

    public void clearContext() {
        this.role = "";
        this.readerId = "";
        this.username = "";
    }

    public String getRole() {
        return role;
    }
}
