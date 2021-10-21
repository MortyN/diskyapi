package com.disky.api.filter;

import com.fasterxml.jackson.annotation.JsonProperty;

public class testFilter {
    private int id;
    private String name;

    public testFilter(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
