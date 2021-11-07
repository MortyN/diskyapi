package com.disky.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

public class ScoreCardResult extends GenericModel{
    private ScoreCardMember scoreCardMember;
    private ArenaRoundHole arenaRoundHole;
    private Integer scoreValue;

    public ScoreCardResult(ScoreCardMember scoreCardMember, ArenaRoundHole arenaRoundHole, Integer scoreValue) {
        this.scoreCardMember = scoreCardMember;
        this.arenaRoundHole = arenaRoundHole;
        this.scoreValue = scoreValue;
    }

    public static String getColumns(){
       return  " score_card_result.SCORE_CARD_MEMBER_ID RESULT_SCORE_CARD_MEMBER_ID," +
               " score_card_result.ARENA_ROUND_HOLE_ID RESULT_SCORE_CARD_MEMBER_ID," +
               " score_card_result.SCORE_VALUE RESULT_SCORE_VALUE";
    }

    @Override
    public Long getPrimaryKey() {
        return this.getPrimaryKey();
    }
    public ScoreCardMember getScoreCardMember() {
        return this.scoreCardMember;
    }

    public ArenaRoundHole getArenaRoundHole() {
        return this.arenaRoundHole;
    }

    public Integer getScoreValue() {
        return this.scoreValue;
    }

    public void setScoreCardMember(ScoreCardMember scoreCardMember) {
        this.scoreCardMember = scoreCardMember;
    }

    public void setArenaRoundHole(ArenaRoundHole arenaRoundHole) {
        this.arenaRoundHole = arenaRoundHole;
    }

    public void setScoreValue(Integer scoreValue) {
        this.scoreValue = scoreValue;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ScoreCardResult)) return false;
        final ScoreCardResult other = (ScoreCardResult) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$scoreCardMember = this.getScoreCardMember();
        final Object other$scoreCardMember = other.getScoreCardMember();
        if (this$scoreCardMember == null ? other$scoreCardMember != null : !this$scoreCardMember.equals(other$scoreCardMember))
            return false;
        final Object this$arenaRoundHole = this.getArenaRoundHole();
        final Object other$arenaRoundHole = other.getArenaRoundHole();
        if (this$arenaRoundHole == null ? other$arenaRoundHole != null : !this$arenaRoundHole.equals(other$arenaRoundHole))
            return false;
        final Object this$scoreValue = this.getScoreValue();
        final Object other$scoreValue = other.getScoreValue();
        if (this$scoreValue == null ? other$scoreValue != null : !this$scoreValue.equals(other$scoreValue))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ScoreCardResult;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $scoreCardMember = this.getScoreCardMember();
        result = result * PRIME + ($scoreCardMember == null ? 43 : $scoreCardMember.hashCode());
        final Object $arenaRoundHole = this.getArenaRoundHole();
        result = result * PRIME + ($arenaRoundHole == null ? 43 : $arenaRoundHole.hashCode());
        final Object $scoreValue = this.getScoreValue();
        result = result * PRIME + ($scoreValue == null ? 43 : $scoreValue.hashCode());
        return result;
    }

    public String toString() {
        return "ScoreCardResult(scoreCardMember=" + this.getScoreCardMember() + ", arenaRoundHole=" + this.getArenaRoundHole() + ", scoreValue=" + this.getScoreValue() + ")";
    }
}
