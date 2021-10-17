package com.disky.api.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class User {
     @Getter @Setter long userId;
     @Getter @Setter String username;
     @Getter @Setter String firstName;
     @Getter @Setter String lastName;
     @Getter @Setter String phoneNumber;
     @Getter @Setter String password;

     public User(@NonNull Long userId) {
        this.userId = userId;
     }

     public User(long userId, String username, String firstName, String lastName, String phoneNumber, String password) {
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.password = password;
     }
}
