package com.disky.api.controller;

import com.disky.api.Exceptions.GetUserException;
import com.disky.api.model.ScoreCard;
import com.disky.api.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

public class ScoreCardController {
    public void create(ScoreCard scoreCard){
        /*Logger log = Logger.getLogger(String.valueOf(ScoreCardController.class));
        Connection conn = DatabaseConnection.getConnection();
        try {
            int psId = 1;

            if(scoreCard.getCardId() != null && scoreCard.getCardId()  != 0L) {
                update(scoreCard);
                return;
            }

            String sql = "INSERT INTO score_cards (ARENA_ROUND_ID, START_TS, CREATED_BY_USER_ID) values (?,?,?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            if(scoreCard.getStartTs() == null){
                scoreCard.setStartTs(System.currentTimeMillis());
            }
            stmt.setLong(psId++, scoreCard.getArenaRound().getArenaRoundId());
            stmt.setString(psId++, user.getFirstName());
            stmt.setString(psId++, user.getLastName());


            log.info("Rows affected: " + stmt.executeUpdate());

        } catch (SQLException e) {
            throw new GetUserException(e.getMessage());
        }
    }

    private void update(ScoreCard scoreCard) {
    */
    }


}
