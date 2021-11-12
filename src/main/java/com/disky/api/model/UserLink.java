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
        return " user_links.USER_ID_LINK1, user_links.USER_ID_LINK2, user_links.TYPE, user_links.CREATED_TS ";
    }
}
