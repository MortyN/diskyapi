package com.disky.api.model;

import lombok.Data;

@Data
public class ScoreCardMember {
    private Long scoreCardMemberId;
    private User user;
    private ScoreCard scoreCard;

    public ScoreCardMember(Long scoreCardMemberId) {
        this.scoreCardMemberId = scoreCardMemberId;
    }

    public ScoreCardMember(Long scoreCardMemberId, User user, ScoreCard scoreCard) {
        this.scoreCardMemberId = scoreCardMemberId;
        this.user = user;
        this.scoreCard = scoreCard;
    }

    public static String getColumns(){
        return " score_card_members.SCORE_CARD_MEMBER_ID SCORE_CARD_MEMBERS_SCORE_CARD_MEMBER_ID, " +
                "score_card_members.USER_ID SCORE_CARD_MEMBERS_USER_ID, " +
                "score_card_members.CARD_ID SCORE_CARD_MEMBERS_CARD_ID ";
    }

}
