package com.disky.api.model;

import lombok.Data;

@Data
public class ScoreCardResult {
    private ScoreCardMember scoreCardMember;
    private ArenaRoundHole arenaRoundHole;
    private Integer scoreValue;

    public ScoreCardResult(ScoreCardMember scoreCardMember, ArenaRoundHole arenaRoundHole, Integer scoreValue) {
        this.scoreCardMember = scoreCardMember;
        this.arenaRoundHole = arenaRoundHole;
        this.scoreValue = scoreValue;
    }
}
