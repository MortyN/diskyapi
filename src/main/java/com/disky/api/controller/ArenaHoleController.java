package com.disky.api.controller;

import com.disky.api.Exceptions.ArenaRoundException;
import com.disky.api.filter.ArenaRoundFilter;
import com.disky.api.model.Arena;
import com.disky.api.model.ArenaRound;
import com.disky.api.model.ArenaRoundHole;
import com.disky.api.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ArenaHoleController {

    public static List<ArenaRoundHole> create(List<ArenaRoundHole> holes, ArenaRound arenaRound, Connection conn) throws ArenaRoundException {
        List<ArenaRoundHole> holeResult = new ArrayList<>();
        for(ArenaRoundHole hole : holes){
            hole.setArenaRound(arenaRound);
             holeResult.add(create(hole, true, conn));
        }
        return holeResult;
    }
    public static ArenaRoundHole create(ArenaRoundHole hole, Boolean transaction, Connection connection) throws ArenaRoundException {
        Logger log = Logger.getLogger(String.valueOf(ArenaHoleController.class));
        Connection conn = DatabaseConnection.getConnection();
        try {
            if(connection != null) conn = connection;
            int psId = 1;
            validateCreate(hole);
            String fields = "";
            String marks = "";
            if(hole.getArenaRoundHoleId() !=  null && hole.getArenaRoundHoleId() != 0L) {
                update(hole);
                return null;
            }
            if(hole.getLatitude() != null && hole.getLongitude() != null){
                fields = " , LATITUDE , LONGITUDE ";
                marks = ",?,?";
            }

            String sql = "INSERT INTO arena_rounds_hole (ARENA_ROUND_ID, HOLE_NAME, PAR_VALUE, SORT" + fields + ") values (?, ?, ?, ?" + marks + ")";

            log.info(sql);
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setLong(psId++, hole.getArenaRound().getArenaRoundId());
            stmt.setString(psId++, "Hole " + hole.getOrder());
            stmt.setInt(psId++, hole.getParValue());
            stmt.setInt(psId++, hole.getOrder());

            if(hole.getLatitude() != null && hole.getLongitude() != null){
                stmt.setString(psId++, hole.getLatitude());
                stmt.setString(psId++, hole.getLongitude());
            }

            hole.setArenaRound(ArenaRoundController.get(hole.getArenaRound()));

            log.info("Rows affected: " + stmt.executeUpdate());
        } catch (SQLException | ArenaRoundException  e) {
            throw new ArenaRoundException(e.getMessage());
        }
        return hole;
    }

    private static void update(ArenaRoundHole hole) throws ArenaRoundException {
        Logger log = Logger.getLogger(String.valueOf(ArenaHoleController.class));
        Connection conn = DatabaseConnection.getConnection();
        String fields = "";

        if(hole.getOrder() != null && hole.getHoleName() != null && hole.getOrder().equals(hole.getHoleName().split(" ")[1])){
            fields = ", HOLE_NAME = '?', SORT = ? ";
        }
        if(hole.getLatitude() != null && hole.getLongitude() != null){
            fields = " LATITUDE = ?, LONGITUDE = ? ";
        }
        try {
            int psId = 1;

            String sql = "UPDATE arena_rounds_hole SET PAR_VALUE = ?, ACTIVE = ? " + fields + "  WHERE ARENA_ROUND_HOLE_ID = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(psId++, hole.getParValue());
            stmt.setBoolean(psId++, hole.getActive());
            if(hole.getOrder() != null && hole.getHoleName() != null && hole.getOrder().equals(hole.getHoleName().split(" ")[1])){
                stmt.setString(psId++, "Hole " + hole.getOrder());
                stmt.setInt(psId++,hole.getOrder());
            }
            if(hole.getLatitude() != null && hole.getLongitude() != null){
                stmt.setString(psId++, hole.getLatitude());
                stmt.setString(psId++, hole.getLongitude());
            }

            stmt.setLong(psId++, hole.getArenaRoundHoleId());

            log.info("Rows affected: " + stmt.executeUpdate());
        } catch (SQLException e) {
            throw new ArenaRoundException(e.getMessage());
        }
    }

    private static void validateCreate(ArenaRoundHole hole) throws ArenaRoundException {
        if(hole.getArenaRound() == null || hole.getArenaRound().getArenaRoundId() == null || hole.getArenaRound().getArenaRoundId() == 0) throw new ArenaRoundException("ArenaRoundId is required");
        if(hole.getParValue() == null || hole.getParValue() == 0 ) throw new ArenaRoundException("ParValue is required");
    }

    private static void getHole(ArenaRoundHole hole){

    }
}
