package com.disky.api.controller;

import com.disky.api.Exceptions.ArenaException;
import com.disky.api.Exceptions.GetUserException;
import com.disky.api.Exceptions.UserLinkException;
import com.disky.api.filter.ArenaFilter;
import com.disky.api.model.Arena;
import com.disky.api.model.User;
import com.disky.api.model.UserLink;
import com.disky.api.util.DatabaseConnection;
import com.disky.api.util.Parse;

import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;


public class ArenaController {


    public static Arena changeActive(Arena arena) throws ArenaException {
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
            return arena;
        } catch (SQLException e) {
            throw new ArenaException(e.getMessage());
        }
    }

    public static void create(Arena arena) throws ArenaException{
        Logger log = Logger.getLogger(String.valueOf(ArenaController.class));
        Connection conn = DatabaseConnection.getConnection();
        try {
            int psId = 1;
            if (arena.getArenaId() != null && arena.getArenaId() != 0L) update(arena);
            String sql = "INSERT INTO arena (NAME, DESCRIPTION, ESTABLISHED, CREATED_BY_USER_ID, CREATED_TS, MODIFIED_TS, ACTIVE) VALUES (?,?,?,?,?,?,?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            arena.setCreatedTs(new Timestamp(System.currentTimeMillis()));
            arena.setUpdateTs(arena.getCreatedTs());
            arena.setActive(true);

            stmt.setString(psId++, arena.getArenaName());
            stmt.setString(psId++, arena.getDescription());
            stmt.setDate(psId++, arena.getEstablished());
            stmt.setLong(psId++, arena.getCreatedBy().getUserId());
            stmt.setTimestamp(psId++, arena.getCreatedTs());
            stmt.setTimestamp(psId++, arena.getCreatedTs());
            stmt.setBoolean(psId++, true);

            log.info("Rows affected: " + stmt.executeUpdate());

        } catch (SQLException e) {
            throw new ArenaException(e.getMessage());
        }
    }

    private static void update(Arena arena) throws ArenaException {
        Logger log = Logger.getLogger(String.valueOf(ArenaController.class));
        Connection conn = DatabaseConnection.getConnection();
        try {
            int psId = 1;

            String sql = "UPDATE arena SET NAME = ?, DESCRIPTION = ?, ESTABLISHED = ?, MODIFIED_TS = ?, ACTIVE = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            arena.setUpdateTs(new Timestamp(System.currentTimeMillis()));

            stmt.setString(psId++, arena.getArenaName());
            stmt.setString(psId++, arena.getDescription());
            stmt.setDate(psId++, arena.getEstablished());
            stmt.setTimestamp(psId++, arena.getUpdateTs());
            stmt.setBoolean(psId++, arena.isActive());

            log.info("Rows affected: " + stmt.executeUpdate());

        } catch (SQLException e) {
            throw new ArenaException(e.getMessage());
        }
    }

    public static List<Arena> get(ArenaFilter filter) throws ArenaException {
        Logger log = Logger.getLogger(String.valueOf(ArenaController.class));
        List<Arena> arenaResult = new ArrayList<>();

        Connection conn = DatabaseConnection.getConnection();

        try {
            String where = "WHERE ACTIVE = ?  ";

            if (!Parse.nullOrEmpty(filter.getArenaIds())) {
                where += " AND arena.ARENA_ID in ( " + Parse.listAsQuestionMarks(filter.getArenaIds()) + ")";
            }
            if (!Parse.nullOrEmpty(filter.getNames())) {
                where += " AND arena.NAME in ( " + Parse.listAsQuestionMarks(filter.getNames()) + ")";
            }

            if (!Parse.nullOrEmpty(filter.getCreatedBy())) {
                where += " AND arena.CREATED_BY_USER_ID in ( " + Parse.listAsQuestionMarks(filter.getCreatedBy()) + ")";
            }

            String sql = "SELECT " + Arena.getColumns() + " FROM arena " + where;
            PreparedStatement stmt = conn.prepareStatement(sql);
            int psId = 1;

            stmt.setBoolean(psId++, filter.isActive());

            if (!Parse.nullOrEmpty(filter.getArenaIds())) {
                for (Long id : filter.getArenaIds()) {
                    stmt.setLong(psId++, id);
                }
            }
                if (!Parse.nullOrEmpty(filter.getNames())) {
                    for (String name : filter.getNames()) {
                        stmt.setString(psId++, name);
                    }
                }

                if (!Parse.nullOrEmpty(filter.getCreatedBy())) {
                    for (Long createdById : filter.getCreatedBy()) {
                        stmt.setLong(psId++, createdById);
                    }
                }
                log.info(stmt.toString());

                ResultSet res = stmt.executeQuery();
                while (res.next()) {
                    User user = UserController.getOne(new User(res.getLong("CREATED_BY_USER_ID")));
                    Arena arena = new Arena(
                            res.getLong("ARENA_ID"),
                            res.getString("NAME"),
                            res.getString("DESCRIPTION"),
                            res.getDate("ESTABLISHED"),
                            user,
                            res.getTimestamp("CREATED_TS"),
                            res.getTimestamp("MODIFIED_TS"),
                            res.getBoolean("ACTIVE"));

                    arenaResult.add(arena);
                }
                log.info("Successfully retrieved " + arenaResult.size() + " users.");
                return arenaResult;
            } catch(SQLException | GetUserException e){
                throw new ArenaException(e.getMessage());
            }
        }


}

