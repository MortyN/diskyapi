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
import lombok.SneakyThrows;
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
    @SneakyThrows
    public static void save(ScoreCardResult scoreCardResult) throws ScoreCardResultException{
        Logger log = Logger.getLogger(String.valueOf(ScoreCardResultController.class));
        Connection conn = DatabaseConnection.getConnection();
        try {
            int psId = 1;

            String sql = "INSERT INTO score_card_result (SCORE_CARD_MEMBER_ID, ARENA_ROUND_HOLE_ID, SCORE_VALUE) VALUES (?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(psId++, scoreCardResult.getScoreCardMember().getScoreCardMemberId());
            stmt.setLong(psId++, scoreCardResult.getArenaRoundHole().getArenaRoundHoleId());
            stmt.setInt(psId++, scoreCardResult.getScoreValue());

            log.info("Rows affected: " + stmt.executeUpdate());
        }catch (SQLException e){
            throw new ScoreCardResultException(e.getMessage());
        }
    }
    @SneakyThrows
    public static void Update(ScoreCardResult scoreCardResult) throws ScoreCardResultException {
        Logger log = Logger.getLogger(String.valueOf(ScoreCardResultController.class));
        Connection conn = DatabaseConnection.getConnection();
        try {
            int psId = 1;

            String sql = "UPDATE score_card_result SET SCORE_VALUE = ? WHERE SCORE_CARD_MEMBER_ID = ? AND ARENA_ROUND_HOLE_ID = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(psId++, scoreCardResult.getScoreValue());
            stmt.setLong(psId++, scoreCardResult.getScoreCardMember().getScoreCardMemberId());
            stmt.setLong(psId++, scoreCardResult.getArenaRoundHole().getArenaRoundHoleId());

            log.info("Rows affected: " + stmt.executeUpdate());
        }catch (SQLException e){
            throw new ScoreCardResultException(e.getMessage());
        }
    }
    @SneakyThrows
    public static List<ScoreCardResult> get(ScoreCardResultControllerFilter filter) throws ScoreCardResultException {
        Logger log = Logger.getLogger(String.valueOf(ScoreCardResultController.class));
        List<ScoreCardResult> scoreCardResults = new ArrayList<>();

        Connection conn = DatabaseConnection.getConnection();

        try {
            String where = "where 1=1 ";
            if (filter.getScoreCardMember() != null && filter.getScoreCardMember().getScoreCardMemberId() != null){
                where += "AND score_card_result.SCORE_CARD_MEMBER_ID = ?";
            }
            if (filter.getArenaRoundHole() != null && filter.getArenaRoundHole().getArenaRoundHoleId() != null){
                where += "AND score_card_result.ARENA_ROUND_HOLE_ID = ?";
            }
            if (filter.getScoreValue() != null){
                where += "AND score_card_result.SCORE_VALUE = ?";
            }
            // INNER JOIN: arenaRoundHole
            // Lag metode i ArenaRoundHole.java get columns med ALIAS
            // Lag metode i ScoreCardResult.java get columns med ALIAS
            // hente ut ful object ved hjelp avc constructor i whilen
            String sql = "SELECT * FROM " + where;
            PreparedStatement stmt = conn.prepareStatement(sql);
            int psId = 1;

            stmt.setLong(psId++, filter.getScoreCardMember().getScoreCardMemberId());
            stmt.setLong(psId++, filter.getArenaRoundHole().getArenaRoundHoleId());
            stmt.setInt(psId++, filter.getScoreValue());
            ResultSet res = stmt.executeQuery();

            while (res.next()){
                Arena arena = ArenaController.get(new Arena(res.getLong("score_card_result.ARENA_ROUND_HOLE_ID")));
                ScoreCardResult scoreCardResult = new ScoreCardResult(
                        ScoreCardMemberController.get(new ScoreCardMember(res.getLong("score_card_result.SCORE_CARD_MEMBER_ID"))),
                        ArenaHoleController.getHole(new ArenaRoundHole(res.getLong("score_card_result.ARENA_ROUND_HOLE_ID"))),
                        res.getInt("score_card_result.SCORE_VALUE")
                );
            }
            log.info("Successfully retrieved: " + scoreCardResults.size());
            return scoreCardResults;
        } catch (SQLException | ArenaException | ScoreCardMemberException | ArenaRoundException e){
            throw new ScoreCardResultException(e.getMessage());
        }

    }
}
