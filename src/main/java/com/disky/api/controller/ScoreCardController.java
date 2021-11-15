package com.disky.api.controller;

import com.disky.api.Exceptions.ArenaRoundException;
import com.disky.api.Exceptions.GetUserException;
import com.disky.api.Exceptions.ScoreCardException;
import com.disky.api.Exceptions.ScoreCardMemberException;
import com.disky.api.filter.ScoreCardFilter;
import com.disky.api.model.*;
import com.disky.api.util.DatabaseConnection;
import com.disky.api.util.Utility;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ScoreCardController {
    public static void create(ScoreCard scoreCard) throws ScoreCardException {
        if (scoreCard.getCreatedBy() == null && scoreCard.getCreatedBy().getUserId() == 0)
            throw new ScoreCardException("User is required!");

        Logger log = Logger.getLogger(String.valueOf(ScoreCardController.class));
        Connection conn = DatabaseConnection.getConnection();
        try {
            int psId = 1;

            if (scoreCard.getCardId() != null && scoreCard.getCardId() != 0L) {
                update(scoreCard);
                return;
            }
            String sql = "INSERT INTO score_cards (ARENA_ROUND_ID, START_TS, CREATED_BY_USER_ID) values (?,?,?)";

            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (scoreCard.getStartTs() == null) {
                scoreCard.setStartTs(new Timestamp(System.currentTimeMillis()));
            }
            stmt.setLong(psId++, scoreCard.getArenaRound().getArenaRoundId());
            stmt.setTimestamp(psId++, scoreCard.getStartTs());
            stmt.setLong(psId++, scoreCard.getCreatedBy().getUserId());

            log.info("Rows affected: " + stmt.executeUpdate());

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                scoreCard.setCardId(rs.getLong(1));
            }
            scoreCard.setArenaRound(ArenaRoundController.get(scoreCard.getArenaRound()));
            scoreCard.setCreatedBy(UserController.getOne(scoreCard.getCreatedBy()));

            if (!Utility.nullOrEmpty(scoreCard.getMembers())) {
                scoreCard.getMembers().forEach((member) -> member.setScoreCard(new ScoreCard(scoreCard.getCardId())));
                ScoreCardMemberController.create(scoreCard.getMembers());
            }
        } catch (SQLException | ArenaRoundException | GetUserException | ScoreCardMemberException e) {
            throw new ScoreCardException(e.getMessage());
        }
    }

    private static void update(ScoreCard scoreCard) throws ScoreCardException, SQLException {
        Logger log = Logger.getLogger(String.valueOf(ScoreCardController.class));
        Connection conn = DatabaseConnection.getConnection();
        try {
            int psId = 1;

            String sql = "UPDATE score_cards SET END_TS = ? WHERE CARD_ID = ?";
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setTimestamp(psId++, ts);
            stmt.setLong(psId++, scoreCard.getCardId());

            log.info("Rows affected: " + stmt.executeUpdate());
            scoreCard.setEndTs(ts);

        } catch (SQLException e) {
            throw new ScoreCardException(e.getMessage());
        }
    }

    public static List<ScoreCard> getScoreCard(ScoreCardFilter filter) throws ScoreCardException {
        List<ScoreCard> result = new ArrayList<>();
        Logger log = Logger.getLogger(String.valueOf(ScoreCardController.class));
        Connection conn = DatabaseConnection.getConnection();
        int psId = 1;
        String fields = ScoreCard.getColumns() + ", " + ArenaRound.getColumns();
        String where = " WHERE 1=1 ";
        String innerJoin = "";
        String leftJoin = " LEFT JOIN arena_rounds ON arena_rounds.ARENA_ROUND_ID = score_cards.ARENA_ROUND_ID ";
        try {
            if(filter.getUser() != null && filter.getUser().getUserId() != null &&  filter.getUser().getUserId() != 0){
                where += " AND score_cards.CREATED_BY_USER_ID = ? ";
            }
            if(filter.getArena() != null && filter.getArena().getArenaId() != null &&  filter.getArena().getArenaId() != 0){
                innerJoin += " INNER JOIN arena_rounds ON score_cards.ARENA_ROUND_ID = arena_rounds.ARENA_ROUND_ID " +
                             " INNER JOIN arena on arena.ARENA_ID = arena_rounds.ARENA_ID ";
                where += " AND arena.ARENA_ID = ? ";
            }
            if(filter.getMember() != null && filter.getMember().getUserId() != null && filter.getMember().getUserId() != 0){
                where += " AND score_cards.CARD_ID IN (" +
                        "    SELECT sc.CARD_ID FROM score_cards sc" +
                        "    left join score_card_members s on sc.CARD_ID = s.CARD_ID" +
                        "    where s.USER_ID = ? )";
            }
            if(filter.isGetMembers()){
                leftJoin += " LEFT JOIN score_card_members lf_score_card_members ON lf_score_card_members.CARD_ID = score_cards.CARD_ID ";
                fields += ", " + "lf_score_card_members.SCORE_CARD_MEMBER_ID SCORE_CARD_MEMBERS_SCORE_CARD_MEMBER_ID, " +
                        "lf_score_card_members.USER_ID SCORE_CARD_MEMBERS_USER_ID," +
                        "lf_score_card_members.CARD_ID SCORE_CARD_MEMBERS_CARD_ID";
            }

            if(filter.isGetResult() && filter.isGetMembers()){
                leftJoin += " LEFT JOIN score_card_result ON score_card_result.SCORE_CARD_MEMBER_ID = lf_score_card_members.SCORE_CARD_MEMBER_ID "
                + " LEFT JOIN arena_rounds_hole ON score_card_result.ARENA_ROUND_HOLE_ID = arena_rounds_hole.ARENA_ROUND_HOLE_ID "
                + " LEFT JOIN arena ON arena_rounds.ARENA_ID = arena.ARENA_ID ";

                fields += ", " + ScoreCardResult.getColumns() + ", " + ArenaRoundHole.getColumns() + ", "  + Arena.getColumns();
            }

            String sql = " SELECT " + fields + " FROM score_cards " + leftJoin + innerJoin + where;
            PreparedStatement stmt = conn.prepareStatement(sql);
            log.info(stmt.toString());

            if(filter.getUser() != null && filter.getUser().getUserId() != null &&  filter.getUser().getUserId() != 0){
                stmt.setLong(psId++, filter.getUser().getUserId());
            }
            if(filter.getArena() != null && filter.getArena().getArenaId() != null &&  filter.getArena().getArenaId() != 0){
                stmt.setLong(psId++, filter.getArena().getArenaId());
            }
            if(filter.getMember() != null && filter.getMember().getUserId() != null && filter.getMember().getUserId() != 0){
                stmt.setLong(psId++, filter.getMember().getUserId());
            }

            log.info(stmt.toString());

            ResultSet rs = stmt.executeQuery();
            ScoreCard scoreCard = null;
            ScoreCardMember member = null;

            while(rs.next()){
                Long cardId = rs.getLong("SCORE_CARDS_CARD_ID");
                if(!Utility.listContainsPrimaryKey(result,cardId)){
                    ArenaRound arenaRound = new ArenaRound(
                            rs.getLong("ARENA_ROUNDS_ARENA_ROUND_ID"),
                            new Arena(rs.getLong("ARENA_ROUNDS_ARENA_ID")),
                            rs.getInt("ARENA_ROUNDS_HOLE_AMOUNT"),
                            rs.getBoolean("ARENA_ROUNDS_PAYMENT"),
                            rs.getString("ARENA_ROUNDS_DESCRIPTION"),
                            new User(rs.getLong("ARENA_ROUNDS_CREATED_BY_USER_ID")),
                            rs.getTimestamp("ARENA_ROUNDS_CREATED_TS"),
                            rs.getTimestamp("ARENA_ROUNDS_MODIFIED_TS"),
                            rs.getBoolean("ARENA_ROUNDS_ACTIVE")
                    );
                     scoreCard = new ScoreCard(
                             cardId,
                             arenaRound,
                            rs.getTimestamp("SCORE_CARDS_START_TS"),
                            rs.getTimestamp("SCORE_CARDS_END_TS"),
                            new User(rs.getLong("SCORE_CARDS_CREATED_BY_USER"))
                    );
                     result.add(scoreCard);
                }
                if(filter.isGetMembers() && !Utility.listContainsPrimaryKey(scoreCard.getMembers(), rs.getLong("SCORE_CARD_MEMBERS_SCORE_CARD_MEMBER_ID"))){
                    member = new ScoreCardMember(
                            rs.getLong("SCORE_CARD_MEMBERS_SCORE_CARD_MEMBER_ID"),
                            UserController.getOne(new User(rs.getLong("SCORE_CARD_MEMBERS_USER_ID"))),
                            new ScoreCard(cardId)
                    );
                    scoreCard.addMember(member);
                }
                if(filter.isGetResult() && filter.isGetMembers()){
                    Arena arena = new Arena(
                            rs.getLong("ARENA_ARENA_ID"),
                            rs.getString("ARENA_NAME"),
                            rs.getString("ARENA_DESCRIPTION"),
                            rs.getTimestamp("ARENA_ESTABLISHED"),
                            new User(rs.getLong("ARENA_CREATED_BY_USER_ID")),
                            rs.getTimestamp("ARENA_CREATED_TS"),
                            rs.getTimestamp("ARENA_MODIFIED_TS"),
                            rs.getString("ARENA_LATITUDE"),
                            rs.getString("ARENA_LONGITUDE"),
                            rs.getBoolean("ARENA_ACTIVE")
                    );

                    scoreCard.getArenaRound().setArena(arena);

                    ArenaRoundHole hole = new ArenaRoundHole(
                                rs.getLong("ARENA_ROUNDS_HOLE_ARENA_ROUND_HOLE_ID"),
                                new ArenaRound(rs.getLong("ARENA_ROUNDS_HOLE_ARENA_ROUND_ID")),
                                rs.getString("ARENA_ROUNDS_HOLE_HOLE_NAME"),
                                rs.getInt("ARENA_ROUNDS_HOLE_PAR_VALUE"),
                                rs.getBoolean("ARENA_ROUNDS_HOLE_ACTIVE"),
                                rs.getString("ARENA_ROUNDS_HOLE_START_LATITUDE"),
                                rs.getString("ARENA_ROUNDS_HOLE_START_LONGITUDE"),
                                rs.getString("ARENA_ROUNDS_HOLE_END_LATITUDE"),
                                rs.getString("ARENA_ROUNDS_HOLE_END_LONGITUDE"),
                                rs.getInt("ARENA_ROUNDS_HOLE_ORDER")
                            );
                    member.addResult(new ScoreCardResult(
                           new ScoreCardMember(rs.getLong("RESULT_SCORE_CARD_MEMBER_ID")),
                            hole,
                            rs.getInt("RESULT_SCORE_VALUE")
                    ));
                }
            }
            return result;
        } catch (SQLException | GetUserException e) {
            throw new ScoreCardException(e.getMessage());
        }
    }
}