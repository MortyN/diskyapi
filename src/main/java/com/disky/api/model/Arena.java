package com.disky.api.model;

import com.disky.api.util.Utility;
import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
public class Arena extends GenericModel{
    private Long arenaId;
    private String arenaName;
    private String description;
    private Date established;
    private User createdBy;
    private Timestamp createdTs;
    private Timestamp updateTs;
    private String latitude;
    private String longitude;
    private List<ArenaRound> rounds;
    private boolean active;

    public Arena(Long arenaId) {
        this.arenaId = arenaId;
    }

    public Arena(Long arenaId, String arenaName, String description, Date established, User createdBy, Timestamp createdTs, Timestamp updateTs, String latitude, String longitude, boolean active) {
        this.arenaId = arenaId;
        this.arenaName = arenaName;
        this.description = description;
        this.established = established;
        this.createdBy = createdBy;
        this.createdTs = createdTs;
        this.updateTs = updateTs;
        this.latitude = latitude;
        this.longitude = longitude;
        this.active = active;
    }

    public Arena addRounds(ArenaRound round){
        if(Utility.nullOrEmpty(this.rounds)) this.rounds = new ArrayList<>();
        this.rounds.add(round);
        return this;
    }

    @Override
    public Long getPrimaryKey() {
        return this.getArenaId();
    }

    public static String getColumns(){
        return " arena.ARENA_ID ARENA_ARENA_ID, arena.NAME ARENA_NAME, arena.DESCRIPTION ARENA_DESCRIPTION," +
                " arena.ESTABLISHED ARENA_ESTABLISHED, arena.CREATED_BY_USER_ID ARENA_CREATED_BY_USER_ID, " +
                "arena.CREATED_TS ARENA_CREATED_TS, arena.MODIFIED_TS ARENA_MODIFIED_TS," +
                "arena.LATITUDE ARENA_LATITUDE, arena.LONGITUDE ARENA_LONGITUDE, arena.ACTIVE ARENA_ACTIVE ";
    }
}
