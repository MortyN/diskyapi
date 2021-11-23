package com.disky.api.model;

import com.disky.api.util.Utility;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.ArrayList;
import java.util.List;

public class ScoreCardMember extends GenericModel{
    private Long scoreCardMemberId;
    private User user;
    private ScoreCard scoreCard;
    private List<ScoreCardResult> results;

    public ScoreCardMember(Long scoreCardMemberId) {
        this.scoreCardMemberId = scoreCardMemberId;
    }

    public ScoreCardMember(Long scoreCardMemberId, User user, ScoreCard scoreCard) {
        this.scoreCardMemberId = scoreCardMemberId;
        this.user = user;
        this.scoreCard = scoreCard;
    }

    public ScoreCardMember addResult(ScoreCardResult result){
        if(Utility.nullOrEmpty(this.results)) this.results = new ArrayList<>();
        this.results.add(result);
        return this;

    }

    public Integer getTotalThrows(){
        if(Utility.nullOrEmpty(this.results)) return null;
        final int[] sum = {0};
        this.results.forEach((result) -> {
            sum[0] += result.getScoreValue();
        });
        return sum[0];
    }

    public Integer getTotalPar(){
        if(Utility.nullOrEmpty(this.results)) return null;
        final int[] sum = {0};
        this.results.forEach((result) -> {
                sum[0] += result.getArenaRoundHole().getParValue();
        });
        return sum[0];
    }

    public Integer getTotalScore(){
        if(Utility.nullOrEmpty(this.results)) return null;
        final int[] sum = {0};
        this.results.forEach((result) -> {
            if(!result.getScoreValue().equals(0)) {
                sum[0] += result.getScoreValue() - result.getArenaRoundHole().getParValue();
            }
        });
        return sum[0];
    }
    public static String getColumns(){
        return " score_card_members.SCORE_CARD_MEMBER_ID SCORE_CARD_MEMBERS_SCORE_CARD_MEMBER_ID, " +
                "score_card_members.USER_ID SCORE_CARD_MEMBERS_USER_ID, " +
                "score_card_members.CARD_ID SCORE_CARD_MEMBERS_CARD_ID ";
    }

    @Override
    public Long getPrimaryKey() {
        return this.scoreCardMemberId;
    }

    public Long getScoreCardMemberId() {
        return this.scoreCardMemberId;
    }

    public User getUser() {
        return this.user;
    }

    public ScoreCard getScoreCard() {
        return this.scoreCard;
    }

    public List<ScoreCardResult> getResults() {
        return this.results;
    }

    public void setScoreCardMemberId(Long scoreCardMemberId) {
        this.scoreCardMemberId = scoreCardMemberId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setScoreCard(ScoreCard scoreCard) {
        this.scoreCard = scoreCard;
    }

    public void setResults(List<ScoreCardResult> results) {
        this.results = results;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ScoreCardMember)) return false;
        final ScoreCardMember other = (ScoreCardMember) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$scoreCardMemberId = this.getScoreCardMemberId();
        final Object other$scoreCardMemberId = other.getScoreCardMemberId();
        if (this$scoreCardMemberId == null ? other$scoreCardMemberId != null : !this$scoreCardMemberId.equals(other$scoreCardMemberId))
            return false;
        final Object this$user = this.getUser();
        final Object other$user = other.getUser();
        if (this$user == null ? other$user != null : !this$user.equals(other$user)) return false;
        final Object this$scoreCard = this.getScoreCard();
        final Object other$scoreCard = other.getScoreCard();
        if (this$scoreCard == null ? other$scoreCard != null : !this$scoreCard.equals(other$scoreCard)) return false;
        final Object this$results = this.getResults();
        final Object other$results = other.getResults();
        if (this$results == null ? other$results != null : !this$results.equals(other$results)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ScoreCardMember;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $scoreCardMemberId = this.getScoreCardMemberId();
        result = result * PRIME + ($scoreCardMemberId == null ? 43 : $scoreCardMemberId.hashCode());
        final Object $user = this.getUser();
        result = result * PRIME + ($user == null ? 43 : $user.hashCode());
        final Object $scoreCard = this.getScoreCard();
        result = result * PRIME + ($scoreCard == null ? 43 : $scoreCard.hashCode());
        final Object $results = this.getResults();
        result = result * PRIME + ($results == null ? 43 : $results.hashCode());
        return result;
    }

    public String toString() {
        return "ScoreCardMember(scoreCardMemberId=" + this.getScoreCardMemberId() + ", user=" + this.getUser() + ", scoreCard=" + this.getScoreCard() + ", results=" + this.getResults() + ")";
    }
}
