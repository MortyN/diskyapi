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
import java.util.Optional;
import java.util.logging.Logger;

public class ScoreCardController {
    public static void create(ScoreCard scoreCard) throws ScoreCardException {
        if (scoreCard.getCreatedBy() == null && scoreCard.getCreatedBy().getUserId() == 0)
            throw new ScoreCardException("User is required!");

        Logger log = Logger.getLogger(String.valueOf(ScoreCardController.class));
            int psId = 1;

            if (scoreCard.getCardId() != null && scoreCard.getCardId() != 0L) {
                update(scoreCard);
                return;
            }else{
                String sql = "INSERT INTO score_cards (ARENA_ROUND_ID, START_TS, CREATED_BY_USER_ID) values (?,?,?)";

                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ){

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
    }

    private static void update(ScoreCard scoreCard) throws ScoreCardException {
        Logger log = Logger.getLogger(String.valueOf(ScoreCardController.class));
        String sql = "UPDATE score_cards SET END_TS = ? WHERE CARD_ID = ?";


        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
        ){
            int psId = 1;

            Timestamp ts = new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(psId++, ts);
            stmt.setLong(psId++, scoreCard.getCardId());

            log.info("Rows affected: " + stmt.executeUpdate());
            scoreCard.setEndTs(ts);

        } catch (SQLException e) {
            throw new ScoreCardException(e.getMessage());
        }
    }

    public static List<ScoreCard> getScoreCard(ScoreCardFilter filter) throws ScoreCardException, GetUserException {
        List<ScoreCard> result = new ArrayList<>();
        Logger log = Logger.getLogger(String.valueOf(ScoreCardController.class));
        int psId = 1;
        String where = "";

        if(filter.getMember() != null && filter.getMember().getUserId() != null && !filter.getMember().getUserId().equals(0l)){
            where += "score_cards.CARD_ID IN ( " +
                    " SELECT score_cards.CARD_ID FROM score_cards " +
                            " LEFT JOIN score_card_members on score_cards.CARD_ID = score_card_members.CARD_ID " +
                            " WHERE score_card_members.USER_ID = ? )";
        }

        if(filter.getScoreCardId() != null && !filter.getScoreCardId().equals(0l)){
            where += "score_cards.CARD_ID = ?";
        }
            String sql = "SELECT "
                    +  ScoreCard.getColumns()
                    + ", " + ArenaRound.getColumns()
                    + ", " + Arena.getColumns()
                    + ", " + ArenaRoundHole.getColumns()
                    + ", " + ScoreCardMember.getColumns()
                    + ", " + ScoreCardResult.getColumns()
                    + ", " + getArenaRoundholeColumns() +
                         " FROM score_cards " +
                                    " INNER JOIN arena_rounds ON arena_rounds.ARENA_ROUND_ID = score_cards.ARENA_ROUND_ID " +
                                    " INNER JOIN arena on arena_rounds.ARENA_ID = arena.ARENA_ID " +
                                    " LEFT JOIN arena_rounds_hole on arena_rounds.ARENA_ROUND_ID = arena_rounds_hole.ARENA_ROUND_ID " +
                                    " LEFT JOIN score_card_members on score_cards.CARD_ID = score_card_members.CARD_ID " +
                                    " LEFT JOIN score_card_result on score_card_members.SCORE_CARD_MEMBER_ID = score_card_result.SCORE_CARD_MEMBER_ID " +
                                    " INNER JOIN arena_rounds_hole ar on score_card_result.ARENA_ROUND_HOLE_ID = ar.ARENA_ROUND_HOLE_ID " +
                        " WHERE 1=1 AND " + where;

            try(Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
            ){
                log.info(stmt.toString());

                if(filter.getMember() != null && filter.getMember().getUserId() != null && !filter.getMember().getUserId().equals(0l)){
                    stmt.setLong(psId++, filter.getMember().getUserId());
                }

                if(filter.getScoreCardId() != null && !filter.getScoreCardId().equals(0l)){
                    stmt.setLong(psId++, filter.getScoreCardId());
                }

                log.info(stmt.toString());

                ResultSet rs = stmt.executeQuery();
                ScoreCard scoreCard = null;
                ScoreCardMember member = null;

                while(rs.next()){
                    Long cardId = rs.getLong("SCORE_CARDS_CARD_ID");
                    if(!Utility.listContainsPrimaryKey(result, cardId)){
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

                        ArenaRound arenaRound = new ArenaRound(
                                rs.getLong("ARENA_ROUNDS_ARENA_ROUND_ID"),
                                arena,
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

                    long arenaRoundHole = rs.getLong("ARENA_ROUNDS_HOLE_ARENA_ROUND_HOLE_ID");
                    if(!Utility.listContainsPrimaryKey(scoreCard.getArenaRound().getHoles(), arenaRoundHole)){
                        scoreCard.getArenaRound().addHoles(new ArenaRoundHole(
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
                        ));
                    }

                    Long scoreCardMemberId =  rs.getLong("SCORE_CARD_MEMBERS_SCORE_CARD_MEMBER_ID");
                    if(!Utility.listContainsPrimaryKey(scoreCard.getMembers(), scoreCardMemberId)){
                        member = new ScoreCardMember(
                                rs.getLong("SCORE_CARD_MEMBERS_SCORE_CARD_MEMBER_ID"),
                                UserController.getOne(new User(rs.getLong("SCORE_CARD_MEMBERS_USER_ID"))),
                                new ScoreCard(cardId)
                        );
                        scoreCard.addMember(member);
                    }
                    ScoreCardResult scoreCardResult = new ScoreCardResult(
                            new ScoreCardMember(rs.getLong("RESULT_SCORE_CARD_MEMBER_ID")),
                            new ArenaRoundHole( rs.getLong("RESULT_SCORE_CARD_HOLE_ID")),
                            rs.getInt("RESULT_SCORE_VALUE"));

                    ScoreCardMember currentScoreCardMember = scoreCard.getMembers().stream()
                            .filter(m -> m.getScoreCardMemberId().equals(scoreCardResult.getScoreCardMember().getScoreCardMemberId()))
                            .findAny()
                            .orElse(null);

                    if(Utility.nullOrEmpty(currentScoreCardMember.getResults()) || !currentScoreCardMember.getResults().stream().anyMatch( o -> o.getScoreCardMember().getScoreCardMemberId().equals(scoreCardResult.getScoreCardMember().getScoreCardMemberId()) && o.getArenaRoundHole().getArenaRoundHoleId().equals(scoreCardResult.getArenaRoundHole().getArenaRoundHoleId()))){
                       if(scoreCardResult.getScoreCardMember() != null){
                           ArenaRoundHole arenaRoundHoleResult = new ArenaRoundHole(
                                   rs.getLong("AR_ARENA_ROUND_HOLE_ID"),
                                   new ArenaRound(rs.getLong("AR_ARENA_ROUND_ID")),
                                   rs.getString("AR_HOLE_NAME"),
                                   rs.getInt("AR_PAR_VALUE"),
                                   rs.getBoolean("AR_ACTIVE"),
                                   rs.getString("AR_START_LATITUDE"),
                                   rs.getString("AR_START_LONGITUDE"),
                                   rs.getString("AR_END_LATITUDE"),
                                   rs.getString("AR_LONGITUDE"),
                                   rs.getInt("AR_ORDER")
                           );
                           scoreCardResult.setArenaRoundHole(arenaRoundHoleResult);
                           currentScoreCardMember.addResult(scoreCardResult);
                       }
                    }
                }
            } catch (SQLException e){
                throw new ScoreCardException(e.getMessage());
            }
            return result;
    }

    private static String getArenaRoundholeColumns() {
        return " ar.ARENA_ROUND_HOLE_ID AR_ARENA_ROUND_HOLE_ID, " +
                " ar.ARENA_ROUND_ID AR_ARENA_ROUND_ID, " +
                " ar.HOLE_NAME AR_HOLE_NAME, " +
                " ar.PAR_VALUE AR_PAR_VALUE, " +
                " ar.ACTIVE AR_ACTIVE, " +
                " ar.START_LATITUDE AR_START_LATITUDE, " +
                " ar.START_LONGITUDE AR_START_LONGITUDE, " +
                " ar.END_LATITUDE AR_END_LATITUDE, " +
                " ar.END_LONGITUDE AR_LONGITUDE, " +
                " ar.SORT AR_ORDER ";
    }
}