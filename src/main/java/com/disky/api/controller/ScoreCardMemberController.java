package com.disky.api.controller;

import com.disky.api.Exceptions.ScoreCardMemberException;
import com.disky.api.filter.ScoreCardMemberFilter;
import com.disky.api.model.ScoreCardMember;
import com.disky.api.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ScoreCardMemberController {
    //TODO: Make a filter - Done
    //TODO: Make an exceptionHandler - Done
    //TODO: DELTE - Potentially not something we want to do.
    //TODO: SAVE - Done
    //TODO: UPDATE - Not needed. We won't be updating the IDs?
    //TODO: GET -

    public static void save(ScoreCardMember scoreCardMember) throws ScoreCardMemberException{
        Logger log = Logger.getLogger(String.valueOf(ScoreCardMemberController.class));
        Connection conn = DatabaseConnection.getConnection();
        try {
            int psId = 1;

            String sql = "INSERT INTO score_card_members (USER_ID, CARD_ID) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setLong(psId++, scoreCardMember.getUser().getUserId());
            stmt.setLong(psId++, scoreCardMember.getScoreCard().getCardId());

            log.info("Rows affected: " + stmt.executeUpdate());
        }catch (SQLException e){
            throw new ScoreCardMemberException(e.getMessage());
        }
    }

    public static List<ScoreCardMember> get(ScoreCardMemberFilter filter) throws ScoreCardMemberException{
        Logger log = Logger.getLogger(String.valueOf(ScoreCardMemberController.class));
        List<ScoreCardMember> scoreCardMemberResult = new ArrayList<>();

        Connection conn = DatabaseConnection.getConnection();

        try {
            String where = "WHERE ";
            if (filter.getScoreCardMemberId() != null){
              where+= "score_card_members.SCORE_CARD_MEMBER_ID = ?";
            }
            if (filter.getUser() != null){
                where += "score_card_members.USER_ID = ?";
            }
            /*if (filter.getCardId() != null){
                where += "score_card_members.CARD_ID = ?";
            }*/
            String sql = "SELECT " + where;
            PreparedStatement stmt = conn.prepareStatement(sql);
            int psId = 1;

            stmt.setLong(psId++, filter.getScoreCardMemberId());
            stmt.setLong(psId++, filter.getUser().getUserId());
            stmt.setLong(psId++, filter.getScoreCard().getCardId());

            ResultSet res = stmt.executeQuery();

            while (res.next()){
                ScoreCardMember scoreCardMember = new ScoreCardMember(
                        res.getLong("score_card_members.SCORE_CARD_MEMBER_ID"),
                        UserController.get(res.getLong("score_card_members.USERID")),
                        ScoreCardController.get(res.getLong("score_card_members.CARD_ID"))
                );
                scoreCardMemberResult.add(scoreCardMember);
            }
            log.info("Successfully retrieved: " + scoreCardMemberResult.size());
            return scoreCardMemberResult;
        }catch (SQLException | ScoreCardMemberException e){
            throw new ScoreCardMemberException(e.getMessage());
        }
    }
}
