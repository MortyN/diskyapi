package com.disky.api.controller;

import com.disky.api.Exceptions.GetUserException;
import com.disky.api.Exceptions.UserLinkException;
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


    public static void changeActive(Arena arena) throws getArenaException {
        Connection conn = DatabaseConnection.getConnection();
        try {
            int psId = 1;
            Logger log = Logger.getLogger(String.valueOf(ArenaController.class));

            String sql = "UPDATE Arena SET ACTIVE = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setBoolean(psId++, Arena.getArena);

            log.info("Rows affected" + stmt.executeUpdate());
        } catch (SQLException throwables) {
            throw new GetArenaException("Unable to update arena")
        }
    }

    public static void save(Arena arena) throws getArenaException {
        Logger log = Logger.getLogger(String.valueOf(ArenaController.class));
        Connection conn = DatabaseConnection.getConnection();
        try {
            int psId = 1;
            if (Arena.getArenaId() != 0L) update(arena);
            String sql = "INSERT INTO arena (NAME, DESCRIPTION, ESTABLISHED, CREATED_BY_USER_ID, CREATED_TS, MODIFIED_TS, ACTIVE) VALUES (?,?,?,?,?,?,?)";

            PreparedStatement stmt = conn.prepareStatement(sql);

            Arena.setCreatedTs(new java.util.Date());

            stmt.setString(psId++, Arena.getArenaName);
            stmt.setString(psId++, Arena.getDescription);
            stmt.setInt(psId++, Arena.getEstablished);
            stmt.setLong(psId++, Arena.getCreatedBy);
            stmt.setDate(psId++, Arena.getCreatedTs);
            stmt.setDate(psId++, Arena.getCreatedTs);
            stmt.setBoolean(psId++, Arena.setActive);

            log.info("Rows affected: " + stmt.executeUpdate());

        } catch (SQLException throwables) {
            throw new ArenaException(e.getMessage);
        }
    }

    private static void update(Arena arena) throws getArenaException {
        Logger log = Logger.getLogger(String.valueOf(ArenaController.class));
        Connection conn = DatabaseConnection.getConnection();
        try {
            int psId = 1;

            String sql = "UPDATE arena SET NAME = ?, DESCRIPTION = ?, ESTABLISHED = ?, MODIFIED_TS = ?, ACTIVE = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            Arena.seModifiededTs(new java.util.Date());

            stmt.setString(psId++, Arena.getArenaName);
            stmt.setString(psId++, Arena.getDescription);
            stmt.setInt(psId++, Arena.getEstablished);
            stmt.setDate(psId++, Arena.getModifiedTs);
            stmt.setBoolean(psId++, Arena.setActive);

            log.info("Rows affected: " + stmt.executeUpdate());

        } catch (SQLException throwables) {
            throw new GetArenaException("unable to update Arena");
        }
    }

    public static Arena getOne(Arena arena) throws getArenaException {
        ArenaFilter filter = new ArenaFilter();
        filter.addArenaIds(Arena.getArenId);
        return getOne(filter);
    }

    public static Arena getOne(ArenaFilter filter) throws GetArenaException {
        List<Arena> arenas = get(filter);

        if (Parse.nullOrEmpty(arenas))
            return null;
        else if (arenas.size() > 1)
            throw new GetArenaException("Expected one Arena, got " + arenas.size());

        return arenas.get(0);
    }

    public static List<Arena> get(ArenaFilter filter) throws GetArenaException {
        Logger log = Logger.getLogger(String.valueOf(ArenaController.class));
        List<Arena> arenaResult = new ArrayList<>();

        Connection conn = DatabaseConnection.getConnection();


        try {
            String where = "WHERE 1=1 ";

            if (!Parse.nullOrEmpty(filter.getArenaIds())) {
                where += " AND arena.ARENA_ID in ( " + Parse.listAsQuestionMarks(filter.getArenaIds())
            }
            if (!Parse.nullOrEmpty(filter.getName())) {
                where += " AND arena.NAME in ( " + Parse.listAsQuestionMarks(filter.Name()) + ")";
            }

            if (!Parse.nullOrEmpty(filter.getDescription())) {
                where += " AND arena.DESCRPITION in ( " + Parse.listAsQuestionMarks(filter.getDescription()) + ")";
            }

            if (!Parse.nullOrEmpty(filter.getEstablished())) {
                where += " AND arena.ESTABLISHED in ( " + Parse.listAsQuestionMarks(filter.getEstablished()) + ")";
            }

            if (!Parse.nullOrEmpty(filter.getCreatedBy())) {
                where += " AND arena.CREATED_BY_USER_ID in ( " + Parse.listAsQuestionMarks(filter.getCreatedBy()) + ")";
            }
            if (!Parse.nullOrEmpty(filter.getCreatedTS())) {
                where += " AND arena.CREATED_TS in ( " + Parse.listAsQuestionMarks(filter.getCreatedTS()) + ")";
            }
            if (!Parse.nullOrEmpty(filter.getUpdateTS())) {
                where += " AND arena.MODIFIED_TS in ( " + Parse.listAsQuestionMarks(filter.getUpdateTS()) + ")";
            }
            if (!Parse.nullOrEmpty(filter.getActive())) {
                where += " AND arena.ACTIVE in ( " + Parse.listAsQuestionMarks(filter.getActive()) + ")";
            }
            String sql = "SELECT " + Arena.getColumns() + " FROM arena " + where;
            PreparedStatement stmt = conn.prepareStatement(sql);
            int psId = 1;

            if (!Parse.nullOrEmpty(filter.getArenaIds())) {
                for (Long id : filter.getArenaIds()) {
                    stmt.setLong(psId++, id);
                }
            }
                if (!Parse.nullOrEmpty(filter.getName())) {
                    for (String name : filter.getName()) {
                        stmt.setString(psId++, name);
                    }
                }
                if (!Parse.nullOrEmpty(filter.getDescription())) {
                    for (String description : filter.getDescription()) {
                        stmt.setString(psId++, description);
                    }
                }
                if (!Parse.nullOrEmpty(filter.getEstablished())) {
                    for (Integer Established : filter.getEstablished()) {
                        stmt.setString(psId++, established);
                    }
                }
                if (!Parse.nullOrEmpty(filter.getCreatedBy())) {
                    for (Long createdById : filter.getCreatedBy()) {
                        stmt.setLong(psId++, createdById);
                    }
                }
                if (!Parse.nullOrEmpty(filter.getCreatedTS())) {
                    for (Date createdTS : filter.getCreatedTS) {
                        stmt.setDate(psId++, createdTS);
                    }
                }
                if (!Parse.nullOrEmpty(filter.getCreatedTS())) {
                    for (Date createdTS : filter.getCreatedTS) {
                        stmt.setDate(psId++, createdTS);
                    }
                }
                if (!Parse.nullOrEmpty(filter.getModifiedTS())) {
                    for (Date modifiedTS : filter.getModifiedTS) {
                        stmt.setDate(psId++, modifiedTS);
                    }
                }
                if (!Parse.nullOrEmpty(filter.getActive())) {
                    for (Boolean active : filter.getActive) {
                        stmt.setBoolean(psId++, active);
                    }
                }
                log.info(stmt.toString());

                ResultSet res = stmt.executeQuery()
                while (res.next()) {
                    Arena arena = new Arena(
                            res.getLong("ARENA_ID"),
                            res.getString("NAME"),
                            res.getString("DESCRIPTION"),
                            res.getInt("ESTABLISHED"),
                            res.getLong("CREATED_BY_USER_ID"),
                            res.getDate("CREATED_TS"),
                            res.getDate("MODIFIED_TS"),
                            res.getBoolean("ACTIVE"));

                    arenaResult.add(Arena);

                    if (filter.isGetArenaLinks()) {
                        ArenaLinkFilter arenaLinkFilter = new ArenalinkFilter();
                        arenaLinkFilter.setArena(arena);
                        arena.setArenaLinks(ArenaLinkController.getArenaLinks(ArenaLinkFilter));
                    }
                }
                log.info("Successfully retrieved " + arenaResult.size() + " users.");
                return arenaResult;
            } catch(SQLException | ArenaLinkException e){
                throw new GetArenaException("Unable to get Arena");
            }
        }


}

