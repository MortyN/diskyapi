package com.disky.api.controller;

import com.disky.api.Exceptions.ArenaRoundException;
import com.disky.api.Exceptions.GetUserException;
import com.disky.api.Exceptions.ScoreCardException;
import com.disky.api.Exceptions.ScoreCardMemberException;
import com.disky.api.filter.ScoreCardFilter;
import com.disky.api.model.ArenaRound;
import com.disky.api.model.ScoreCard;
import com.disky.api.model.ScoreCardMember;
import com.disky.api.model.User;
import com.disky.api.util.DatabaseConnection;
import com.disky.api.util.Parse;

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

            if (!Parse.nullOrEmpty(scoreCard.getMembers())) {
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
        if(filter.getUser() != null && filter.getUser().getUserId() != null &&  filter.getUser().getUserId() == 0)
            throw new ScoreCardException("User is required");
        List<ScoreCard> result = new ArrayList<>();
        Logger log = Logger.getLogger(String.valueOf(ScoreCardController.class));
        Connection conn = DatabaseConnection.getConnection();
        int psId = 1;
        String fields = ScoreCard.getColumns();
        String where = " WHERE 1=1 ";
        String innerJoin = "";
        String leftJoin = "";
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
                innerJoin += " INNER JOIN score_card_members ON score_card_member.CARD_ID = score_card.CARD_ID ";
                where += " AND score_card_member.USER_ID = ? ";
            }
            if(filter.isGetMembers()){
                leftJoin += " LEFT JOIN score_card_members ON score_card_members.CARD_ID = score_cards.CARD_ID ";
                fields += ", " + ScoreCardMember.getColumns();
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

            while(rs.next()){
                Long cardId = rs.getLong("SCORE_CARDS_CARD_ID");

                if(!result.stream().anyMatch(o -> o.getCardId().equals(cardId))){
                     scoreCard = new ScoreCard(
                             cardId,
                            new ArenaRound(rs.getLong("SCORE_CARDS_ARENA_ROUND_ID")),
                            rs.getTimestamp("SCORE_CARDS_START_TS"),
                            rs.getTimestamp("SCORE_CARDS_END_TS"),
                            new User(rs.getLong("SCORE_CARDS_CREATED_BY_USER"))
                    );
                     result.add(scoreCard);
                }
                if(filter.isGetMembers()){
                    ScoreCardMember member = new ScoreCardMember(
                            rs.getLong("SCORE_CARD_MEMBERS_SCORE_CARD_MEMBER_ID"),
                            UserController.getOne(new User(rs.getLong("SCORE_CARD_MEMBERS_USER_ID"))),
                            new ScoreCard(cardId)
                    );
                    scoreCard.addMember(member);

                }
            }
            return result;
        } catch (SQLException | GetUserException e) {
            throw new ScoreCardException(e.getMessage());
        }
    }
}