package com.disky.api.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class User {
    private long userId;
    private String userName;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String password;

    public User(long userId) {
        this.userId = userId;
    }

    public User(long userId, String userName, String firstName, String lastName, String phoneNumber, String password) {
        this.userId = userId;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }
}
