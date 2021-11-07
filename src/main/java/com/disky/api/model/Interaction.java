package com.disky.api.model;

import lombok.Data;

@Data
public class Interaction {
   private Post post;
   private User user;
   private Integer type;

   public Interaction(Post post, User user, Integer type) {
      this.post = post;
      this.user = user;
      this.type = type;

   }
}
