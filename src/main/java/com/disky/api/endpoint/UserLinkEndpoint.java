package com.disky.api.endpoint;

import com.disky.api.Exceptions.GetUserException;
import com.disky.api.Exceptions.UserLinkException;
import com.disky.api.controller.UserController;
import com.disky.api.controller.UserLinkController;
import com.disky.api.filter.UserFilter;
import com.disky.api.filter.UserLinkFilter;
import com.disky.api.model.ToggleUserWrapper;
import com.disky.api.model.User;
import com.disky.api.model.UserLink;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RequestMapping("/api/v1/userLink")
@RestController
@CrossOrigin
public class UserLinkEndpoint {

    @PostMapping(path="/toggle")
    public UserLink create(@RequestBody(required = true) ToggleUserWrapper toggleUserWrapper) throws UserLinkException {
        return UserLinkController.toggleFriend(toggleUserWrapper.getSenderUser(), toggleUserWrapper.getRecipientUser());
    }

    @PostMapping("/update")
    public static UserLink update(@RequestBody(required = true) UserLink userLink) throws  UserLinkException {
        if(UserLinkController.update(userLink) < 0) return null;
        return userLink;
    }

    @DeleteMapping()
    public static void delete(@RequestBody(required = true) UserLink userLink) throws UserLinkException {
        UserLinkController.delete(userLink);
    }

    @PostMapping("/getLinks")
    public static List<UserLink> getUserLinks(@RequestBody(required = true) UserLinkFilter userLinkFilter) throws  UserLinkException {
        return UserLinkController.getUserLinks(userLinkFilter);
    }
}



