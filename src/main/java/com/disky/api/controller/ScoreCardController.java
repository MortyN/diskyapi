package com.disky.api.controller;

import com.disky.api.Exceptions.ArenaRoundException;
import com.disky.api.Exceptions.GetUserException;
import com.disky.api.Exceptions.ScoreCardException;
import com.disky.api.Exceptions.ScoreCardMemberException;
import com.disky.api.model.ScoreCard;
import com.disky.api.util.DatabaseConnection;
import com.disky.api.util.Parse;

import java.sql.*;
import java.util.logging.Logger;

public class ScoreCardController {
    public static void create(ScoreCard scoreCard) throws ScoreCardException {
        if(scoreCard.getCreatedBy() == null && scoreCard.getCreatedBy().getUserId() == 0)
            throw new ScoreCardException("User is required!");

        Logger log = Logger.getLogger(String.valueOf(ScoreCardController.class));
        Connection conn = DatabaseConnection.getConnection();
        try {
            int psId = 1;

            if(scoreCard.getCardId() != null && scoreCard.getCardId()  != 0L) {
               // update(scoreCard);
                return;
            }
            String sql = "INSERT INTO score_cards (ARENA_ROUND_ID, START_TS, CREATED_BY_USER_ID) values (?,?,?)";

            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if(scoreCard.getStartTs() == null){
                scoreCard.setStartTs(new Timestamp(System.currentTimeMillis()));
            }
            stmt.setLong(psId++, scoreCard.getArenaRound().getArenaRoundId());
            stmt.setTimestamp(psId++, scoreCard.getStartTs());
            stmt.setLong(psId++, scoreCard.getCreatedBy().getUserId());

            log.info("Rows affected: " + stmt.executeUpdate());

            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next()){
                scoreCard.setCardId(rs.getLong(1));
            }
            scoreCard.setArenaRound(ArenaRoundController.get(scoreCard.getArenaRound()));
            scoreCard.setCreatedBy(UserController.getOne(scoreCard.getCreatedBy()));

            if(!Parse.nullOrEmpty(scoreCard.getMembers())){
                scoreCard.getMembers().forEach((member) -> member.setScoreCard(new ScoreCard(scoreCard.getCardId())));
                ScoreCardMemberController.create(scoreCard.getMembers());
            }
        } catch (SQLException | ArenaRoundException | GetUserException | ScoreCardMemberException e) {
            throw new ScoreCardException(e.getMessage());
        }
    }

    private static void update(ScoreCard scoreCard) {

    }


}
