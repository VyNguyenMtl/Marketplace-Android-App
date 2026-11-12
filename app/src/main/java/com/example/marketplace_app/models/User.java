package com.example.marketplace_app.models;

public class User {

    // Use these constants everywhere instead of typing "BUYER" / "SELLER" by hand.
    public static final String ROLE_BUYER = "BUYER";
    public static final String ROLE_SELLER = "SELLER";

    private String uid;
    private String name;
    private String email;
    private String role;

    // Firebase needs an empty constructor to turn database data back into a User
    public User() {
    }

    public User(String uid, String name, String email, String role) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
