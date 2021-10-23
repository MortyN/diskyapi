package com.disky.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Date;
import java.sql.Timestamp;

@Data
public class Arena {
    private Long arenaId;
    private String arenaName;
    private String description;
    private Date established;
    private User createdBy;
    private Timestamp createdTs;
    private Timestamp updateTs;
    private boolean active;

    public Arena(Long arenaId) {
        this.arenaId = arenaId;
    }

    public Arena(Long arenaId, String arenaName, String description, Date established, User createdBy, Timestamp createdTs, Timestamp updateTs, boolean active) {
        this.arenaId = arenaId;
        this.arenaName = arenaName;
        this.description = description;
        this.established = established;
        this.createdBy = createdBy;
        this.createdTs = createdTs;
        this.updateTs = updateTs;
        this.active = active;
    }

    public static String getColumns(){
        return "arena.ARENA_ID, arena.NAME, arena.DESCRIPTION, arena.ESTABLISHED, arena.CREATED_BY_USER_ID, arena.CREATED_TS, arena.MODIFIED_TS, arena.ACTIVE";
    }
}
