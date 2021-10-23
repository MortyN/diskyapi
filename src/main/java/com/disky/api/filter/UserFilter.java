package com.disky.api.filter;

import com.disky.api.util.Parse;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class UserFilter {
    private List<Long> userIds;
    private List<String> userNames;
    private List<String> firstNames;
    private List<String> lastNames;
    private List<String> phoneNumbers;

    private boolean getUserLinks = false;

    public UserFilter addUserIds(Long id){
        if(Parse.nullOrEmpty(this.userIds)) this.userIds = new ArrayList<>();
        this.userIds.add(id);
        return this;
    }

}
