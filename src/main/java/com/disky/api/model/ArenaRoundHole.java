package com.disky.api.model;

import lombok.Data;

@Data
public class ArenaRoundHole extends GenericModel{
    private Long arenaRoundHoleId;
    private ArenaRound arenaRound;
    private String holeName;
    private Integer parValue;
    private Boolean active;
    private String start_latitude;
    private String start_longitude;
    private String end_latitude;
    private String end_longitude;
    private Integer order;

    public ArenaRoundHole(Long arenaRoundHoleId) {
        this.arenaRoundHoleId = arenaRoundHoleId;
    }

    public ArenaRoundHole(Long arenaRoundHoleId, ArenaRound arenaRound, String holeName, Integer parValue, Boolean active, String start_latitude, String start_longitude, String end_latitude, String end_longitude, Integer order) {
        this.arenaRoundHoleId = arenaRoundHoleId;
        this.arenaRound = arenaRound;
        this.holeName = holeName;
        this.parValue = parValue;
        this.active = active;
        this.start_latitude = start_latitude;
        this.start_longitude = start_longitude;
        this.end_latitude = end_latitude;
        this.end_longitude = end_longitude;
        this.order = order;
    }

    public static String getColumns(){
        return " arena_rounds_hole.ARENA_ROUND_HOLE_ID ARENA_ROUNDS_HOLE_ARENA_ROUND_HOLE_ID, " +
                " arena_rounds_hole.ARENA_ROUND_ID ARENA_ROUNDS_HOLE_ARENA_ROUND_ID, " +
                " arena_rounds_hole.HOLE_NAME ARENA_ROUNDS_HOLE_HOLE_NAME, " +
                " arena_rounds_hole.PAR_VALUE ARENA_ROUNDS_HOLE_PAR_VALUE, " +
                " arena_rounds_hole.ACTIVE ARENA_ROUNDS_HOLE_ACTIVE, " +
                " arena_rounds_hole.START_LATITUDE ARENA_ROUNDS_HOLE_START_LATITUDE, " +
                " arena_rounds_hole.START_LONGITUDE ARENA_ROUNDS_HOLE_START_LONGITUDE, " +
                " arena_rounds_hole.END_LATITUDE ARENA_ROUNDS_HOLE_END_LATITUDE, " +
                " arena_rounds_hole.END_LONGITUDE ARENA_ROUNDS_HOLE_END_LONGITUDE, " +
                " arena_rounds_hole.SORT ARENA_ROUNDS_HOLE_ORDER ";
    }

    @Override
    public Long getPrimaryKey() {
        return this.arenaRoundHoleId;
    }
}
