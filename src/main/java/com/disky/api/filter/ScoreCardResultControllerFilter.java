package com.disky.api.filter;
import com.disky.api.model.ArenaRoundHole;
import com.disky.api.model.ScoreCardMember;
import lombok.Data;

@Data
public class ScoreCardResultControllerFilter {
    private ScoreCardMember scoreCardMember;
    private ArenaRoundHole arenaRoundHole;
    private Integer scoreValue;

}
