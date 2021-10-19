package com.disky.api.model;

import lombok.Data;

@Data
public class ArenaRoundHole {
    private Long arenaRoundHoleId;
    private ArenaRound arenaRound;
    private String holeName;
    private Integer parValue;
    private Boolean active;

    public ArenaRoundHole(Long arenaRoundHoleId) {
        this.arenaRoundHoleId = arenaRoundHoleId;
    }

    public ArenaRoundHole(Long arenaRoundHoleId, ArenaRound arenaRound, String holeName, Integer parValue, Boolean active) {
        this.arenaRoundHoleId = arenaRoundHoleId;
        this.arenaRound = arenaRound;
        this.holeName = holeName;
        this.parValue = parValue;
        this.active = active;
    }
}
