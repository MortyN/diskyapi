package com.disky.api.model;

import com.disky.api.util.Parse;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class ScoreCard {
    private Long cardId;
    private ArenaRound arenaRound;
    private Timestamp startTs; //Auto fill
    private Timestamp endTs; // only for update
    private User createdBy;

    private List<ScoreCardMember> members;

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

    public ScoreCard addMember(ScoreCardMember member){
        if(Parse.nullOrEmpty(this.members)) this.members = new ArrayList<>();
        this.members.add(member);
        return this;

    }

    public static String getColumns(){
        return " score_cards.CARD_ID SCORE_CARDS_CARD_ID, " +
                "score_cards.ARENA_ROUND_ID SCORE_CARDS_ARENA_ROUND_ID, " +
                "score_cards.START_TS SCORE_CARDS_START_TS, " +
                "score_cards.END_TS SCORE_CARDS_END_TS, " +
                "score_cards.CREATED_BY_USER_ID SCORE_CARDS_CREATED_BY_USER ";
    }
}
