package com.disky.api.filter;

import com.disky.api.model.User;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class UserLinkFilter{
    private User user;
    private boolean fullUserObject;
    private Integer type;
    private Timestamp fromTs;
    private Timestamp toTs;
}
