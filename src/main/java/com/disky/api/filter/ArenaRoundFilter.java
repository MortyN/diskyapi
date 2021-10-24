package com.disky.api.filter;

import com.disky.api.model.Arena;
import com.disky.api.model.User;
import lombok.Data;

import java.util.List;

@Data
public class ArenaRoundFilter {
    private List<Integer> holeAmounts;
    private Arena connectedToArenaId;
    private User createdByUser;
    private Boolean needPayment;
    private Boolean isActive = true;
    private Boolean getArenaHoles = false;
    private Long arenaRoundId;
}
