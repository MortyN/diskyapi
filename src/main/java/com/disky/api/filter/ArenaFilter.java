package com.disky.api.filter;

import com.disky.api.util.Utility;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ArenaFilter {
    private List<Long> arenaIds;
    private List<String> names;
    private List<Long> createdBy;

    private boolean getArenaRounds = false;

    private boolean isActive = true;

    public ArenaFilter addArenaIds(Long arenaId){
        if(Utility.nullOrEmpty(this.arenaIds)) this.arenaIds = new ArrayList<>();
        this.arenaIds.add(arenaId);
        return this;
    }
}
