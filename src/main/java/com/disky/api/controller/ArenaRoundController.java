package com.disky.api.controller;

import com.disky.api.Exceptions.ArenaException;
import com.disky.api.Exceptions.ArenaRoundException;
import com.disky.api.Exceptions.GetUserException;
import com.disky.api.filter.ArenaRoundFilter;
import com.disky.api.model.Arena;
import com.disky.api.model.ArenaRound;
import com.disky.api.model.ArenaRoundHole;
import com.disky.api.model.User;
import com.disky.api.util.DatabaseConnection;
import com.disky.api.util.Parse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ArenaRoundController {
     public static void create(ArenaRound round) throws ArenaRoundException {
         Logger log = Logger.getLogger(String.valueOf(ArenaRoundController.class));
         Connection conn = DatabaseConnection.getConnection();
         try {
             conn.setAutoCommit(false);

             int psId = 1;
             validateCreate(round);

             if(round.getArenaRoundId() !=  null && round.getArenaRoundId() != 0L) {
                 update(round);
                 return;
             }
             Timestamp ts =  new Timestamp(System.currentTimeMillis());
             round.setCreationTs(ts);
             round.setUpdateTs(ts);
             String sql = "INSERT INTO arena_rounds (ARENA_ID, HOLE_AMOUNT, PAYMENT, DESCRIPTION, CREATED_BY_USER_ID, CREATED_TS, MODIFIED_TS) values (?,?,?,?,?, ?,?)";

             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

             stmt.setLong(psId++, round.getArena().getArenaId());
             stmt.setInt(psId++, round.getHoleAmount());
             stmt.setBoolean(psId++, round.getPayment());
             stmt.setString(psId++, round.getDescription());
             stmt.setLong(psId++, round.getCreatedBy().getUserId());
             stmt.setTimestamp(psId++, round.getCreationTs());
             stmt.setTimestamp(psId++, round.getUpdateTs());

             round.setArena(ArenaController.get(round.getArena()));
             round.setCreatedBy(UserController.getOne(round.getCreatedBy()));

             log.info("Rows affected: " + stmt.executeUpdate());
             ResultSet rs = stmt.getGeneratedKeys();
             if(rs.next()){
                 round.setArenaRoundId(rs.getLong(1));
             }

             if(!Parse.nullOrEmpty(round.getHoles())){
                 round.setHoles(ArenaHoleController.create(round.getHoles(),round, conn));
             }
             conn.commit();
         } catch (SQLException | ArenaRoundException | GetUserException | ArenaException e) {
             try {
                 conn.rollback();
             } catch (SQLException throwables) {
                 throw new ArenaRoundException(e.getMessage());
             }
             throw new ArenaRoundException(e.getMessage());
         }
     }

    public static void delete(ArenaRound arenaRound) throws ArenaRoundException {
        Connection conn = DatabaseConnection.getConnection();
        try {
            Logger log = Logger.getLogger(String.valueOf(ArenaController.class));

            String sql = "UPDATE arena_rounds SET ACTIVE = ?, MODIFIED_TS = ? WHERE ARENA_ROUND_ID = ? ";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setBoolean(1, false);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setLong(3, arenaRound.getArenaRoundId());

            arenaRound.setActive(!arenaRound.isActive());
            arenaRound.setUpdateTs(new Timestamp(System.currentTimeMillis()));

            log.info(sql);
            log.info("Rows affected " + stmt.executeUpdate());
        } catch (SQLException e) {
            throw new ArenaRoundException(e.getMessage());
        }
    }
    private static void update(ArenaRound round) throws ArenaRoundException {
        Logger log = Logger.getLogger(String.valueOf(ArenaRoundController.class));
        if(round.getArenaRoundId() == null) throw new ArenaRoundException("ArenaRoundId is required");
        Connection conn = DatabaseConnection.getConnection();
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        round.setUpdateTs(ts);

        try {
            int psId = 1;

            String sql = "UPDATE arena_rounds SET PAYMENT = ?, DESCRIPTION = ?, MODIFIED_TS = ? WHERE ARENA_ROUND_ID = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setBoolean(psId++, round.getPayment());
            stmt.setString(psId++, round.getDescription());
            stmt.setTimestamp(psId++, round.getUpdateTs());
            stmt.setLong(psId++, round.getArenaRoundId());

            log.info("Rows affected: " + stmt.executeUpdate());
        } catch (SQLException e) {
            throw new ArenaRoundException(e.getMessage());
        }
    }

    private List<ArenaRound> get(Arena arena) throws ArenaRoundException {
         if(arena.getArenaId() == null) throw new ArenaRoundException("ArenaId is required");
         ArenaRoundFilter filter = new ArenaRoundFilter();
         filter.setConnectedToArenaId(arena);
         return get(filter);
    }
    public static ArenaRound get(ArenaRound arenaRound) throws ArenaRoundException {
        if(arenaRound.getArenaRoundId() == null) throw new ArenaRoundException("ArenaRoundId is required");
        ArenaRoundFilter filter = new ArenaRoundFilter();
        filter.setArenaRoundId(arenaRound.getArenaRoundId());

        List<ArenaRound> arenaRounds = get(filter);
        if(arenaRounds.size() > 1) throw new ArenaRoundException("Expected one, got " + arenaRounds.size());
        return arenaRounds.get(0);
    }
    public static List<ArenaRound> get(ArenaRoundFilter filter) throws ArenaRoundException {
        Logger log = Logger.getLogger(String.valueOf(ArenaRoundController.class));
        List<ArenaRound> arenaRoundResult = new ArrayList<>();
        String fields = ArenaRound.getColumns();
        Connection conn = DatabaseConnection.getConnection();

        try {
            String where = "WHERE arena_rounds.ACTIVE = ?  ";
            String leftJoin = null;

            if(filter.getArenaRoundId() != null && filter.getArenaRoundId() != 0){
                where += " AND arena_rounds.ARENA_ROUND_ID = ? ";
            }
            if (!Parse.nullOrEmpty(filter.getHoleAmounts())) {
                where += " AND arena_rounds.HOLE_AMOUNT in ( " + Parse.listAsQuestionMarks(filter.getHoleAmounts()) + ")";
            }

            if (filter.getConnectedToArenaId() != null && filter.getConnectedToArenaId().getArenaId() != 0) {
                where += " AND arena_rounds.ARENA_ID = ? ";
            }

            if (filter.getCreatedByUser() != null && filter.getCreatedByUser().getUserId() != null) {
                where += " AND arena_rounds.CREATED_BY_USER_ID = ?";
            }

            if(filter.getNeedPayment() != null){
                where += " AND arena_rounds.PAYMENT = ?";
            }

            if(filter.getGetArenaHoles() != null){
                leftJoin = " LEFT JOIN arena_rounds_hole USING(ARENA_ROUND_ID) ";
                fields += ", " + ArenaRoundHole.getColumns();
            }

            String sql = "SELECT " + fields + " FROM arena_rounds " + leftJoin + where ;
            PreparedStatement stmt = conn.prepareStatement(sql);
            int psId = 1;

            stmt.setBoolean(psId++, filter.getIsActive());

            if(filter.getArenaRoundId() != null && filter.getArenaRoundId() != 0){
                stmt.setLong(psId++, filter.getArenaRoundId());
            }
            if (!Parse.nullOrEmpty(filter.getHoleAmounts())) {
               for(Integer holeAmount : filter.getHoleAmounts()){
                   stmt.setInt(psId++, holeAmount);
               }
            }
            if (filter.getConnectedToArenaId() != null && filter.getConnectedToArenaId().getArenaId() != 0) {
                stmt.setLong(psId++, filter.getConnectedToArenaId().getArenaId());
            }

            if (filter.getCreatedByUser() != null && filter.getCreatedByUser().getUserId() != null) {
                stmt.setLong(psId++, filter.getCreatedByUser().getUserId());
            }

            if(filter.getNeedPayment() != null){
                stmt.setBoolean(psId++, filter.getNeedPayment());
            }

            log.info(stmt.toString());

            ResultSet res = stmt.executeQuery();

            ArenaRound arenaRound = null;
            while (res.next()) {
                Long arenaRoundId = res.getLong("ARENA_ROUNDS_ARENA_ROUND_ID");

                if(!arenaRoundResult.stream().anyMatch(o -> o.getArenaRoundId().equals(arenaRoundId))){
                    if(arenaRound != null && filter.getGetArenaHoles()){
                        arenaRound.setHoleAmount(arenaRound.getHoles().size());
                    }
                    User user = UserController.getOne(new User(res.getLong("ARENA_ROUNDS_CREATED_BY_USER_ID")));
                    arenaRound = new ArenaRound(
                            arenaRoundId,
                            ArenaController.get(new Arena(res.getLong("ARENA_ROUNDS_ARENA_ID"))),
                            0,
                            res.getBoolean("ARENA_ROUNDS_PAYMENT"),
                            res.getString("ARENA_ROUNDS_DESCRIPTION"),
                            user,
                            res.getTimestamp("ARENA_ROUNDS_CREATED_TS"),
                            res.getTimestamp("ARENA_ROUNDS_MODIFIED_TS"),
                            res.getBoolean("ARENA_ROUNDS_ACTIVE"));

                    arenaRoundResult.add(arenaRound);
                }
                if(filter.getGetArenaHoles()){
                    arenaRound.addHoles(new ArenaRoundHole(
                                    res.getLong("ARENA_ROUNDS_HOLE_ARENA_ROUND_HOLE_ID"),
                                    new ArenaRound(arenaRound.getArenaRoundId()),
                                    res.getString("ARENA_ROUNDS_HOLE_HOLE_NAME"),
                                    res.getInt("ARENA_ROUNDS_HOLE_PAR_VALUE"),
                                    res.getBoolean("ARENA_ROUNDS_HOLE_ACTIVE"),
                                    res.getString("ARENA_ROUNDS_HOLE_LATITUDE"),
                                    res.getString("ARENA_ROUNDS_HOLE_LONGITUDE"),
                                    res.getInt("ARENA_ROUNDS_HOLE_ORDER")
                            )
                    );
                }
            }
            if(arenaRound != null && filter.getGetArenaHoles()){
                arenaRound.setHoleAmount(arenaRound.getHoles().size());
            }
            log.info("Successfully retrieved " + arenaRoundResult.size() + " ArenaRounds.");
            return arenaRoundResult;
        } catch(SQLException | GetUserException | ArenaException e){
            throw new ArenaRoundException(e.getMessage());
        }
    }
    private static void validateCreate(ArenaRound round) throws ArenaRoundException {
        Logger log = Logger.getLogger(String.valueOf(ArenaRoundController.class));
        if(round.getArena() == null || round.getArena().getArenaId() == 0) throw new ArenaRoundException("Arena is required!");
        if(round.getHoleAmount() == null){
            round.setHoleAmount(0);
            log.info("HoleAmount was set to null, set value to '0'");
        }
        if(round.getPayment() == null){
            round.setPayment(false);
            log.info("Payment was set to null, set value to 'false'");
        }
        if(round.getCreatedBy() == null || round.getCreatedBy().getUserId() == 0) throw new ArenaRoundException("User created by is required!");

     }
}
