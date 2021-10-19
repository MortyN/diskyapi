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
}
