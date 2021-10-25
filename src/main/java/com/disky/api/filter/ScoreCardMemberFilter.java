package com.disky.api.filter;
import com.disky.api.model.ScoreCard;
import com.disky.api.model.ScoreCardMember;
import com.disky.api.model.User;
import lombok.Data;

@Data
public class ScoreCardMemberFilter {
    private Long scoreCardMemberId;
    private User user;
    private ScoreCard scoreCard;
}
