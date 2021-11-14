package com.disky.api.filter;

import com.disky.api.model.ScoreCard;
import com.disky.api.model.User;
import lombok.Data;

@Data
public class PostFilter {
    private User user;
    private Integer type;
    private ScoreCard scoreCardId;
    private boolean getFromConnections;
    private boolean getUserLinks = false;
}
