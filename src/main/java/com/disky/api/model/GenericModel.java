package com.disky.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class GenericModel {

    @JsonIgnore
    public abstract <T> T getPrimaryKey();
}
