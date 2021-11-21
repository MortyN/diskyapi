package com.disky.api.controller;

import com.disky.api.Exceptions.ArenaException;
import com.disky.api.Exceptions.ArenaRoundException;
import com.disky.api.Exceptions.GetUserException;
import com.disky.api.filter.ArenaFilter;
import com.disky.api.model.*;
import com.disky.api.util.DatabaseConnection;
import com.disky.api.util.Utility;

import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class ArenaController {


    public static void delete(Arena arena) throws ArenaException {
        Connection conn = DatabaseConnection.getConnection();
        try {
            Logger log = Logger.getLogger(String.valueOf(ArenaController.class));

            String sql = "UPDATE arena SET ACTIVE = ?, MODIFIED_TS = ? WHERE ARENA_ID = ? ";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setBoolean(1, false);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setLong(3, arena.getArenaId());

            arena.setActive(!arena.isActive());
            arena.setUpdateTs(new Timestamp(System.currentTimeMillis()));
            log.info(sql);
            log.info("Rows affected " + stmt.executeUpdate());
        } catch (SQLException e) {
            throw new ArenaException(e.getMessage());
        }
    }

    public static Arena create(Arena arena) throws ArenaException{
        Logger log = Logger.getLogger(String.valueOf(ArenaController.class));
        Connection conn = DatabaseConnection.getConnection();
        try {
            int psId = 1;
            if (arena.getArenaId() != null && !arena.getArenaId().equals(0L)) update(arena);
            String sql = "INSERT INTO arena (NAME, DESCRIPTION, CREATED_BY_USER_ID, CREATED_TS, MODIFIED_TS, LONGITUDE, LATITUDE, ACTIVE) VALUES (?,?,?,?,?,?,?,?)";

            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            arena.setCreatedTs(new Timestamp(System.currentTimeMillis()));
            arena.setUpdateTs(arena.getCreatedTs());
            arena.setActive(true);

            stmt.setString(psId++, arena.getArenaName());
            stmt.setString(psId++, arena.getDescription());
            stmt.setLong(psId++, arena.getCreatedBy().getUserId());
            stmt.setTimestamp(psId++, arena.getCreatedTs());
            stmt.setTimestamp(psId++, arena.getCreatedTs());
            stmt.setString(psId++, arena.getLongitude());
            stmt.setString(psId++, arena.getLatitude());
            stmt.setBoolean(psId++, true);

            log.info("Rows affected: " + stmt.executeUpdate());

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                arena.setArenaId(rs.getLong(1));
            }

            if(Utility.nullOrEmpty(arena.getRounds())){
                for(ArenaRound round : arena.getRounds()){
                    ArenaRoundController.create(round);
                }
            }

        } catch (SQLException | ArenaRoundException e) {
            throw new ArenaException(e.getMessage());
        }
        return arena;
    }

    private static void update(Arena arena) throws ArenaException {
        Logger log = Logger.getLogger(String.valueOf(ArenaController.class));
        Connection conn = DatabaseConnection.getConnection();
        try {
            int psId = 1;

            String sql = "UPDATE arena SET NAME = ?, DESCRIPTION = ?, ESTABLISHED = ?, MODIFIED_TS = ?, LATITUDE = ?, LONGITUDE = ?, ACTIVE = ? WHERE ARENA_ID = ? ";

            PreparedStatement stmt = conn.prepareStatement(sql);
            arena.setUpdateTs(new Timestamp(System.currentTimeMillis()));

            stmt.setString(psId++, arena.getArenaName());
            stmt.setString(psId++, arena.getDescription());
            stmt.setTimestamp(psId++, arena.getEstablished());
            stmt.setTimestamp(psId++, arena.getUpdateTs());
            stmt.setString(psId++, arena.getLatitude());
            stmt.setString(psId++, arena.getLongitude());
            stmt.setBoolean(psId++, arena.isActive());
            stmt.setLong(psId++, arena.getArenaId());


            log.info("Rows affected: " + stmt.executeUpdate());

            if(Utility.nullOrEmpty(arena.getRounds())){
                for(ArenaRound round : arena.getRounds()){
                    ArenaRoundController.create(round);
                }
            }
        } catch (SQLException | ArenaRoundException e) {
            throw new ArenaException(e.getMessage());
        }
    }
    public static Arena get(Arena arena) throws ArenaException {
        ArenaFilter arenaFilter = new ArenaFilter();
        arenaFilter.addArenaIds(arena.getArenaId());
        List<Arena> arenas = get(arenaFilter);
        if(arenas.size() > 1) throw new ArenaException("Expected one Arena got: " + arenas.size() + ". for ArenaId: " + arena.getArenaId());

        return arenas.get(0);
    }
    public static List<Arena> get(ArenaFilter filter) throws ArenaException {
        Logger log = Logger.getLogger(String.valueOf(ArenaController.class));
        List<Arena> arenaResult = new ArrayList<>();

        Connection conn = DatabaseConnection.getConnection();
        String leftJoin = "";
        String fields = Arena.getColumns();

        try {
            String where = "WHERE arena.ACTIVE = ?  ";

            if (!Utility.nullOrEmpty(filter.getArenaIds())) {
                where += " AND arena.ARENA_ID in ( " + Utility.listAsQuestionMarks(filter.getArenaIds()) + ")";
            }
            if (!Utility.nullOrEmpty(filter.getNames())) {
                where += " AND arena.NAME in ( " + Utility.listAsQuestionMarks(filter.getNames()) + ")";
            }

            if (!Utility.nullOrEmpty(filter.getCreatedBy())) {
                where += " AND arena.CREATED_BY_USER_ID in ( " + Utility.listAsQuestionMarks(filter.getCreatedBy()) + ")";
            }

            if(filter.isGetArenaRounds()){
                leftJoin += " LEFT JOIN arena_rounds USING(ARENA_ID) LEFT JOIN arena_rounds_hole ON arena_rounds.ARENA_ROUND_ID = arena_rounds_hole.ARENA_ROUND_ID ";
                fields += ", " + ArenaRound.getColumns() + ", " + ArenaRoundHole.getColumns();
                where += " ORDER BY arena.ARENA_ID, arena_rounds.ARENA_ROUND_ID, arena_rounds_hole.SORT ";
            }

            String sql = "SELECT " + fields + " FROM arena " + leftJoin +  where;
            PreparedStatement stmt = conn.prepareStatement(sql);
            int psId = 1;

            stmt.setBoolean(psId++, filter.isActive());

            if (!Utility.nullOrEmpty(filter.getArenaIds())) {
                for (Long id : filter.getArenaIds()) {
                    stmt.setLong(psId++, id);
                }
            }
                if (!Utility.nullOrEmpty(filter.getNames())) {
                    for (String name : filter.getNames()) {
                        stmt.setString(psId++, name);
                    }
                }

                if (!Utility.nullOrEmpty(filter.getCreatedBy())) {
                    for (Long createdById : filter.getCreatedBy()) {
                        stmt.setLong(psId++, createdById);
                    }
                }

                log.info(stmt.toString());

                ResultSet res = stmt.executeQuery();

                Arena arena = null;
                User user = null;
                ArenaRound arenaRound = null;

                while (res.next()) {
                Long arenaId =  res.getLong("ARENA_ARENA_ID");

                if(!arenaResult.stream().anyMatch(o -> o.getArenaId().equals(arenaId))){
                   user =  UserController.getOne(new User(res.getLong("ARENA_CREATED_BY_USER_ID")));
                        arena = new Arena(
                                res.getLong("ARENA_ARENA_ID"),
                                res.getString("ARENA_NAME"),
                                res.getString("ARENA_DESCRIPTION"),
                                res.getTimestamp("ARENA_ESTABLISHED"),
                                user,
                                res.getTimestamp("ARENA_CREATED_TS"),
                                res.getTimestamp("ARENA_MODIFIED_TS"),
                                res.getString("ARENA_LATITUDE"),
                                res.getString("ARENA_LONGITUDE"),
                                res.getBoolean("ARENA_ACTIVE"));

                        arenaResult.add(arena);
                    }
                    if(filter.isGetArenaRounds() && res.getLong("ARENA_ROUNDS_ARENA_ROUND_ID") != 0){
                        Long arenaRoundId = res.getLong("ARENA_ROUNDS_ARENA_ROUND_ID");

                        if(Utility.nullOrEmpty(arena.getRounds()) || (!arena.getRounds().stream().anyMatch(o -> o.getArenaRoundId().equals(arenaRoundId)))) {
                            arenaRound = new ArenaRound(
                                            arenaRoundId,
                                            new Arena(arena.getArenaId()),
                                            res.getInt("ARENA_ROUNDS_HOLE_AMOUNT"),
                                            res.getBoolean("ARENA_ROUNDS_PAYMENT"),
                                            res.getString("ARENA_ROUNDS_DESCRIPTION"),
                                            user,
                                            res.getTimestamp("ARENA_ROUNDS_CREATED_TS"),
                                            res.getTimestamp("ARENA_ROUNDS_MODIFIED_TS"),
                                            res.getBoolean("ARENA_ACTIVE")
                                    );
                            arena.addRounds(arenaRound);
                        }
                        Long arenaRoundHoleId = res.getLong("ARENA_ROUNDS_HOLE_ARENA_ROUND_HOLE_ID");
                        if(Utility.nullOrEmpty(arenaRound.getHoles()) || (!arenaRound.getHoles().stream().anyMatch(o -> o.getArenaRoundHoleId().equals(arenaRoundHoleId)))){
                            arenaRound.addHoles(new ArenaRoundHole(
                                                    arenaRoundHoleId,
                                                    new ArenaRound(arenaRoundId),
                                                    res.getString("ARENA_ROUNDS_HOLE_HOLE_NAME"),
                                                    res.getInt("ARENA_ROUNDS_HOLE_PAR_VALUE"),
                                                    res.getBoolean("ARENA_ROUNDS_HOLE_ACTIVE"),
                                                    res.getString("ARENA_ROUNDS_HOLE_START_LATITUDE"),
                                                    res.getString("ARENA_ROUNDS_HOLE_START_LONGITUDE"),
                                                    res.getString("ARENA_ROUNDS_HOLE_END_LATITUDE"),
                                                    res.getString("ARENA_ROUNDS_HOLE_END_LONGITUDE"),
                                                    res.getInt("ARENA_ROUNDS_HOLE_ORDER")
                                                ));
                        }
                    }
                }
                log.info("Successfully retrieved " + arenaResult.size() + " arenas.");
                return arenaResult;
            } catch(SQLException | GetUserException e){
                throw new ArenaException(e.getMessage());
            }
        }


}

