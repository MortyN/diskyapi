package com.disky.api.model;

import com.disky.api.util.Parse;
import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Data
public class User {
    private long userId;
    private String userName;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String password;

    private List<UserLink> userLinks;

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

    public void addUserLink(UserLink link){
        if(!Parse.nullOrEmpty(this.userLinks)) this.userLinks = new ArrayList<>();
        this.userLinks.add(link);
    }

    public static String getColumns(){
        return "users.USER_ID, users.USERNAME, users.FIRST_NAME, " +
                "users.LAST_NAME, users.PHONE_NUMBER, users.PASSWORD ";
    }
}
