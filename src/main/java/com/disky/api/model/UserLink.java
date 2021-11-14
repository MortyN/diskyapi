package com.disky.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;
import java.util.Date;

@EqualsAndHashCode
@Data
public class UserLink {
    public static final int USER_LINK_TYPE_PENDING = 1;
    public static final int USER_LINK_TYPE_ACCEPTED = 2;

    private User userLink1;
    private User userLink2;
    private Integer type;
    private Timestamp createdTimeStamp;

    public UserLink(User userLink1, User userLink2, Integer type, Timestamp createdTimeStamp) {
        this.userLink1 = userLink1;
        this.userLink2 = userLink2;
        this.type = type;
        this.createdTimeStamp = createdTimeStamp;
    }

    public static String getColumns(){
        return " user_links.USER_ID_LINK1, user_links.USER_ID_LINK2, user_links.TYPE, user_links.CREATED_TS," +
                " user_links1.USER_ID USER_LINKS1_USER_ID," +
                " user_links1.USERNAME USER_LINKS1_USERNAME," +
                " user_links1.FIRST_NAME USER_LINKS1_FIRST_NAME," +
                " user_links1.LAST_NAME USER_LINKS1_LAST_NAME, " +
                " user_links1.PHONE_NUMBER USER_LINKS1_PHONE_NUMBER, " +
                " user_links1.IMG_KEY USER_LINKS1_IMG_KEY, " +

                " user_links2.USER_ID USER_LINKS2_USER_ID," +
                " user_links2.USERNAME USER_LINKS2_USERNAME," +
                " user_links2.FIRST_NAME USER_LINKS2_FIRST_NAME, " +
                " user_links2.LAST_NAME USER_LINKS2_LAST_NAME, " +
                " user_links2.PHONE_NUMBER USER_LINKS2_PHONE_NUMBER, " +
                " user_links2.IMG_KEY USER_LINKS2_IMG_KEY ";
    }
}
