package com.disky.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
public class Arena {
    private Long arenaId;
    private String arenaName;
    private String description;
    private Integer established;
    private User createdBy;
    private Date createdTs;
    private Date updateTs;
    private boolean active;

    public Arena(Long arenaId) {
        this.arenaId = arenaId;
    }

    public Arena(Long arenaId, String arenaName, String description, Integer established, User createdBy, Date createdTs, Date updateTs, boolean active) {
        this.arenaId = arenaId;
        this.arenaName = arenaName;
        this.description = description;
        this.established = established;
        this.createdBy = createdBy;
        this.createdTs = createdTs;
        this.updateTs = updateTs;
        this.active = active;
    }
}
