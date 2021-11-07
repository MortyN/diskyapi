package com.disky.api.model;

import java.util.List;

public class Interactions {
    private Boolean likedByUser;
    private List<Interaction> interactions;

   public Interactions(List<Interaction> interactions){
        this.interactions = interactions;
        this.likedByUser = null;
    }

    public Interactions(Boolean likedByUser, List<Interaction> interactions){
        this.interactions = interactions;
        this.likedByUser = null;
    }
}
