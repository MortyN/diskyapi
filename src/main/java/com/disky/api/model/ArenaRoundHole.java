package com.disky.api.model;

import lombok.Data;

@Data
public class ArenaRoundHole extends GenericModel{
    private Long arenaRoundHoleId;
    private ArenaRound arenaRound;
    private String holeName;
    private Integer parValue;
    private Boolean active;
    private String latitude;
    private String longitude;
    private Integer order;

    public ArenaRoundHole(Long arenaRoundHoleId) {
        this.arenaRoundHoleId = arenaRoundHoleId;
    }

    public ArenaRoundHole(Long arenaRoundHoleId, ArenaRound arenaRound, String holeName, Integer parValue, Boolean active, String latitude, String longitude, Integer order) {
        this.arenaRoundHoleId = arenaRoundHoleId;
        this.arenaRound = arenaRound;
        this.holeName = holeName;
        this.parValue = parValue;
        this.active = active;
        this.latitude = latitude;
        this.longitude = longitude;
        this.order = order;
    }

    public static String getColumns(){
        return " arena_rounds_hole.ARENA_ROUND_HOLE_ID ARENA_ROUNDS_HOLE_ARENA_ROUND_HOLE_ID, " +
                " arena_rounds_hole.ARENA_ROUND_ID ARENA_ROUNDS_HOLE_ARENA_ROUND_ID, " +
                " arena_rounds_hole.HOLE_NAME ARENA_ROUNDS_HOLE_HOLE_NAME, " +
                " arena_rounds_hole.PAR_VALUE ARENA_ROUNDS_HOLE_PAR_VALUE, " +
                " arena_rounds_hole.ACTIVE ARENA_ROUNDS_HOLE_ACTIVE, " +
                " arena_rounds_hole.LATITUDE ARENA_ROUNDS_HOLE_LATITUDE, " +
                " arena_rounds_hole.LONGITUDE ARENA_ROUNDS_HOLE_LONGITUDE, " +
                " arena_rounds_hole.SORT ARENA_ROUNDS_HOLE_ORDER ";
    }

    @Override
    public Long getPrimaryKey() {
        return this.arenaRoundHoleId;
    }
}
