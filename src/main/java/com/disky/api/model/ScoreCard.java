package com.disky.api.model;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class ScoreCard {
    private Long cardId;
    private ArenaRound arenaRound;
    private Timestamp startTs;
    private Timestamp endTs;
    private User createdBy;

    public ScoreCard(Long cardId) {
        this.cardId = cardId;
    }

    public ScoreCard(Long cardId, ArenaRound arenaRound, Timestamp startTs, Timestamp endTs, User createdBy) {
        this.cardId = cardId;
        this.arenaRound = arenaRound;
        this.startTs = startTs;
        this.endTs = endTs;
        this.createdBy = createdBy;
    }
}
