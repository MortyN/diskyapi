package com.disky.api.model;

import com.disky.api.util.Utility;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ScoreCard extends GenericModel{
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
        if(Utility.nullOrEmpty(this.members)) this.members = new ArrayList<>();
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


    public Long getCardId() {
        return this.cardId;
    }

    public ArenaRound getArenaRound() {
        return this.arenaRound;
    }

    public Timestamp getStartTs() {
        return this.startTs;
    }

    public Timestamp getEndTs() {
        return this.endTs;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }
    public List<ScoreCardMember> getMembers() {
        return this.members;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public void setArenaRound(ArenaRound arenaRound) {
        this.arenaRound = arenaRound;
    }

    public void setStartTs(Timestamp startTs) {
        this.startTs = startTs;
    }

    public void setEndTs(Timestamp endTs) {
        this.endTs = endTs;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public void setMembers(List<ScoreCardMember> members) {
        this.members = members;
    }

    public String toString() {
        return "ScoreCard(cardId=" + this.getCardId() + ", arenaRound=" + this.getArenaRound() + ", startTs=" + this.getStartTs() + ", endTs=" + this.getEndTs() + ", createdBy=" + this.getCreatedBy() + ", members=" + this.getMembers() + ")";
    }

    @Override
    public Long getPrimaryKey() {
        return this.cardId;
    }
}
