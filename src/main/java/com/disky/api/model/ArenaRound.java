package com.disky.api.model;

import com.disky.api.util.Parse;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class ArenaRound {
    private Long arenaRoundId;

    private Arena arena;
    private Integer holeAmount;
    private Boolean payment;
    private String description;
    private User createdBy;
    private Timestamp creationTs;
    private Timestamp updateTs;
    private boolean active;
    private List<ArenaRoundHole> holes;

    public ArenaRound(Long arenaRoundId){
        this.arenaRoundId = arenaRoundId;
    }
    public ArenaRound(Long arenaRoundId, Arena arena, Integer holeAmount, Boolean payment, String description, User createdBy, Timestamp creationTs, Timestamp updateTs, boolean active) {
        this.arenaRoundId = arenaRoundId;
        this.arena = arena;
        this.holeAmount = holeAmount;
        this.payment = payment;
        this.description = description;
        this.createdBy = createdBy;
        this.creationTs = creationTs;
        this.updateTs = updateTs;
        this.active = active;

    }

    public ArenaRound addHoles(ArenaRoundHole hole){
        if(Parse.nullOrEmpty(this.holes)) this.holes = new ArrayList<>();
        this.holes.add(hole);
        return this;
    }
    public static String getColumns(){
        return " arena_rounds.ARENA_ROUND_ID ARENA_ROUNDS_ARENA_ROUND_ID, arena_rounds.ARENA_ID ARENA_ROUNDS_ARENA_ID," +
                " arena_rounds.PAYMENT ARENA_ROUNDS_PAYMENT, arena_rounds.DESCRIPTION ARENA_ROUNDS_DESCRIPTION," +
                " arena_rounds.HOLE_AMOUNT ARENA_ROUNDS_HOLE_AMOUNT, arena_rounds.CREATED_BY_USER_ID ARENA_ROUNDS_CREATED_BY_USER_ID," +
                " arena_rounds.CREATED_TS ARENA_ROUNDS_CREATED_TS, arena_rounds.MODIFIED_TS ARENA_ROUNDS_MODIFIED_TS," +
                " arena_rounds.ACTIVE ARENA_ROUNDS_ACTIVE ";
    }
}
