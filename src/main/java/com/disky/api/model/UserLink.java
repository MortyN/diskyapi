package com.disky.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode
@Data
public class UserLink {
    public static final int USER_LINK_STATUS_PENDING = 1;
    public static final int USER_LINK_STATUS_ACCEPTED = 2;
    public static final int USER_LINK_STATUS_DECLINED = 3;

    public static final int USER_LINK_TYPE_FRIEND_CONNECTION = 1;

    private User userLink1;
    private User userLink2;
    private Integer status;
    private Integer type;
    private Date createdTimeStamp;

    public UserLink(User userLink1, User userLink2, Integer status, Integer type, Date createdTimeStamp) {
        this.userLink1 = userLink1;
        this.userLink2 = userLink2;
        this.status = status;
        this.type = type;
        this.createdTimeStamp = createdTimeStamp;
    }

    public static String getColumns(){
        return " user_links.USER_ID_LINK1, user_links.USER_ID_LINK2, user_links.STATUS, user_links.TYPE, user_links.CREATED_TS ";
    }
}
