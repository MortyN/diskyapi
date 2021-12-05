package com.disky.api.controller;

import com.disky.api.Exceptions.ArenaRoundException;
import com.disky.api.filter.ArenaRoundFilter;
import com.disky.api.model.Arena;
import com.disky.api.model.ArenaRound;
import com.disky.api.model.ArenaRoundHole;
import com.disky.api.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ArenaHoleController {

    public static List<ArenaRoundHole> create(List<ArenaRoundHole> holes, ArenaRound arenaRound) throws ArenaRoundException {
        List<ArenaRoundHole> holeResult = new ArrayList<>();
        for(ArenaRoundHole hole : holes){
            hole.setArenaRound(arenaRound);
             holeResult.add(create(hole));
        }
        return holeResult;
    }
    public static ArenaRoundHole create(ArenaRoundHole hole) throws ArenaRoundException {
        Logger log = Logger.getLogger(String.valueOf(ArenaHoleController.class));

            if(hole.getArenaRoundHoleId() !=  null && !hole.getArenaRoundHoleId().equals(0L)) {
                update(hole);
                return null;
            } else {
                int psId = 1;
                validateCreate(hole);
                String fields = "";
                String marks = "";

                if(hole.getStart_latitude() != null && hole.getStart_longitude() != null && hole.getStart_latitude() != null && hole.getStart_longitude() != null){
                    fields = " , START_LATITUDE , START_LONGITUDE, END_LATITUDE , END_LONGITUDE ";
                    marks = ",?,?,?,?";
                }

                String sql = "INSERT INTO arena_rounds_hole (ARENA_ROUND_ID, HOLE_NAME, PAR_VALUE, SORT" + fields + ") values (?, ?, ?, ?" + marks + ")";

                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql);){

                    log.info(sql);

                    stmt.setLong(psId++, hole.getArenaRound().getArenaRoundId());
                    stmt.setString(psId++, "Hole " + hole.getOrder());
                    stmt.setInt(psId++, hole.getParValue());
                    stmt.setInt(psId++, hole.getOrder());

                    if(hole.getStart_latitude() != null && hole.getStart_longitude() != null && hole.getStart_latitude() != null && hole.getStart_longitude() != null){
                        stmt.setString(psId++, hole.getStart_latitude());
                        stmt.setString(psId++, hole.getStart_longitude());
                        stmt.setString(psId++, hole.getEnd_latitude());
                        stmt.setString(psId++, hole.getEnd_longitude());
                    }

                    hole.setArenaRound(ArenaRoundController.get(hole.getArenaRound()));

                    log.info("Rows affected: " + stmt.executeUpdate());
                } catch (SQLException | ArenaRoundException  e) {
                    throw new ArenaRoundException(e.getMessage());
                }
            }


        return hole;
    }

    private static void update(ArenaRoundHole hole) throws ArenaRoundException {
        Logger log = Logger.getLogger(String.valueOf(ArenaHoleController.class));

        String fields = "";

        if(hole.getOrder() != null && hole.getHoleName() != null && hole.getOrder().equals(hole.getHoleName().split(" ")[1])){
            fields = ", HOLE_NAME = '?', SORT = ? ";
        }
        if(hole.getStart_latitude() != null && hole.getStart_longitude() != null && hole.getStart_latitude() != null && hole.getStart_longitude() != null){
            fields = " , START_LATITUDE = ? , START_LONGITUDE = ?, END_LATITUDE = ? , END_LONGITUDE = ? ";
        }
        String sql = "UPDATE arena_rounds_hole SET PAR_VALUE = ?, ACTIVE = ? " + fields + "  WHERE ARENA_ROUND_HOLE_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);){
            int psId = 1;

            stmt.setInt(psId++, hole.getParValue());
            stmt.setBoolean(psId++, hole.getActive());
            if(hole.getOrder() != null && hole.getHoleName() != null && hole.getOrder().equals(hole.getHoleName().split(" ")[1])){
                stmt.setString(psId++, "Hole " + hole.getOrder());
                stmt.setInt(psId++,hole.getOrder());
            }
            if(hole.getStart_latitude() != null && hole.getStart_longitude() != null && hole.getStart_latitude() != null && hole.getStart_longitude() != null){
                stmt.setString(psId++, hole.getStart_latitude());
                stmt.setString(psId++, hole.getStart_longitude());
                stmt.setString(psId++, hole.getEnd_latitude());
                stmt.setString(psId++, hole.getEnd_longitude());
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

    public static ArenaRoundHole getHole(ArenaRoundHole hole) throws ArenaRoundException{
        Logger log = Logger.getLogger(String.valueOf(ArenaHoleController.class));

        String sql = "SELECT * FROM arena_rounds_hole.ARENA_ROUND_HOLE_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);){

            stmt.setLong(1, hole.getArenaRoundHoleId());

            try(ResultSet res = stmt.executeQuery();){
                ArenaRoundHole arenaRoundHole = new ArenaRoundHole(
                        res.getLong("arena_round_hole.ARENA_ROUND_HOLE_ID"),
                        ArenaRoundController.get(new ArenaRound(res.getLong("arena_round_hole.ARENA_ROUND_ID"))),
                        res.getString("arena_round_hole.HOLE_NAME"),
                        res.getInt("arena_round_hole.PAR_VALUE"),
                        res.getBoolean("arena_round_hole.ACTIVE"),
                        res.getString("arena_round_hole.START_LATITUDE"),
                        res.getString("arena_round_hole.START_LONGITUDE"),
                        res.getString("arena_round_hole.END_LATITUDE"),
                        res.getString("arena_round_hole.END_LONGITUDE"),
                        res.getInt("arena_round_hole.SORT ARENA_ROUNDS_HOLE_ORDER")
                );
                return arenaRoundHole;
            }
        } catch (SQLException e){
            throw new ArenaRoundException(e.getMessage());
        }
    }
}
