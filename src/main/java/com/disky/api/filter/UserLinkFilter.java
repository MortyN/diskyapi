package com.disky.api.filter;

import com.disky.api.model.User;
import lombok.Data;

import java.util.Date;

@Data
public class UserLinkFilter{
    private User user;
    private Integer type;
    private Integer status;
    private Date fromTs;
    private Date toTs;
}
