package com.disky.api.model;

import com.disky.api.util.Parse;
import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Data
public class User {
    private Long userId;
    private String userName;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String password;
    private String imgKey;

    private List<UserLink> userLinks;

    public User(Long userId) {
        this.userId = userId;
    }

    public User(Long userId, String userName, String firstName, String lastName, String phoneNumber, String password, String imgKey) {
        this.userId = userId;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.imgKey = imgKey;
    }

    public void addUserLink(UserLink link){
        if(!Parse.nullOrEmpty(this.userLinks)) this.userLinks = new ArrayList<>();
        this.userLinks.add(link);
    }

    public static String getColumns(){
        return "users.USER_ID, users.USERNAME, users.FIRST_NAME, " +
                "users.LAST_NAME, users.PHONE_NUMBER, users.PASSWORD, users.IMG_KEY ";
    }
}
