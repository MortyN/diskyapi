package com.disky.api.model;

import lombok.Data;

import java.util.Date;

@Data
public class ArenaRound {
    private Long arenaRoundId;
    private Arena arena;
    private Integer holeAmount;
    private Boolean payment;
    private String description;
    private User createdBy;
    private Date creationTs;
    private Date updateTs;
}
