package com.disky.api.filter;

import lombok.Data;

import java.util.List;

@Data
public class ArenaFilter {
    private List<Long> arenaIds;
    private List<String> names;
    private List<Long> createdBy;

    private boolean isActive = true;
}
