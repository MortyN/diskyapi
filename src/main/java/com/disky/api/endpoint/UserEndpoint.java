package com.disky.api.endpoint;

import com.disky.api.Exceptions.GetUserException;
import com.disky.api.controller.UserController;
import com.disky.api.filter.UserFilter;
import com.disky.api.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/user")
@RestController
@CrossOrigin
public class UserEndpoint {
    @GetMapping("/getOne")
    public User getOne(@RequestParam Long userId) throws  GetUserException{
        return UserController.getOne(new User(userId));
    }

    @PostMapping("/create")
    public User createUser(@RequestBody(required = true) User user) throws  GetUserException {
        UserController.save(user);
        return user;
    }

    @PostMapping("/get")
    @ResponseBody
    public static List<User> getUsers(@RequestBody(required = false) UserFilter userFilter) throws GetUserException {
        return UserController.get(userFilter);
    }

    @ResponseBody
    @DeleteMapping
    public static void deleteUsers(@RequestParam(required = true)  Long userId) throws  GetUserException {
         UserController.delete(new User(userId));
    }

    @GetMapping("/search")
    public List<User> userSearch(@RequestParam("keyword") String keyword) throws  GetUserException {
        if(keyword.length() < 2) throw new GetUserException("Search string size must be longer than 2");
        return UserController.search(keyword);
    }
}
