package com.disky.api.controller;

import com.disky.api.Exceptions.ArenaException;
import com.disky.api.Exceptions.ArenaRoundException;
import com.disky.api.Exceptions.ScoreCardMemberException;
import com.disky.api.Exceptions.ScoreCardResultException;
import com.disky.api.filter.ScoreCardResultControllerFilter;
import com.disky.api.model.Arena;
import com.disky.api.model.ArenaRoundHole;
import com.disky.api.model.ScoreCardMember;
import com.disky.api.model.ScoreCardResult;
import com.disky.api.util.DatabaseConnection;
import org.springframework.transaction.TransactionManager;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ScoreCardResultController {

    public static void save(ScoreCardResult scoreCardResult) throws ScoreCardResultException{
        Logger log = Logger.getLogger(String.valueOf(ScoreCardResultController.class));
        String sql = "INSERT INTO score_card_result (SCORE_CARD_MEMBER_ID, ARENA_ROUND_HOLE_ID, SCORE_VALUE) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
        ){
            int psId = 1;

            stmt.setLong(psId++, scoreCardResult.getScoreCardMember().getScoreCardMemberId());
            stmt.setLong(psId++, scoreCardResult.getArenaRoundHole().getArenaRoundHoleId());
            stmt.setInt(psId++, scoreCardResult.getScoreValue());

            log.info("Rows affected: " + stmt.executeUpdate());
        }catch (SQLException e){
            throw new ScoreCardResultException(e.getMessage());
        }
    }
}
