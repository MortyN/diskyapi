package com.disky.api.model;

import com.disky.api.util.Utility;
import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@Data
public class Post extends GenericModel{
    public static final int POST_TYPE_STATUS_MSG = 1;
    public static  final  int POST_TYPE_SCORE_BOARD = 2;

    private Long postId;
    private User user;
    private String message;
    private int type;
    private ScoreCard scoreCard;

    private Timestamp postedTs;
    private Timestamp updatedTs;
    private List<Interaction> interactions;

    public Post(Long postId){
        this.postId = postId;
    }

    public Post(Long postId, User user, String message, int type, ScoreCard scoreCard, Timestamp postedTs, Timestamp updatedTs) {
        this.postId = postId;
        this.user = user;
        this.message = message;
        this.type = type;
        this.scoreCard = scoreCard;
        this.postedTs = postedTs;
        this.updatedTs = updatedTs;
    }

    public static String getColumns(){
        return "posts.POST_ID, posts.TEXT_MESSAGE,  posts.USER_ID, posts.POST_TYPE, posts.SCORE_CARD_LINK, posts.POSTED_TS, posts.UPDATED_TS ";
    }

    public void addInteraction(Interaction interaction){
        if(Utility.nullOrEmpty(this.interactions)) this.interactions = new ArrayList<>();
        this.interactions.add(interaction);
    }
    @Override
    public Long getPrimaryKey() {
        return this.postId;
    }
}
