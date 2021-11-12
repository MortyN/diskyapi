package com.disky.api.endpoint;

import com.disky.api.Exceptions.GetUserException;
import com.disky.api.controller.UserController;
import com.disky.api.filter.UserFilter;
import com.disky.api.model.User;
import com.disky.api.util.S3Util;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RequestMapping("/api/v1/user")
@RestController
@CrossOrigin
public class UserEndpoint {
    @GetMapping("/getOne/{userId}")
    public User getOne(@PathVariable("userId") Long userId) throws  GetUserException{
        UserFilter filter = new UserFilter();
        filter.addUserIds(userId);
        filter.setGetUserLinks(true);
        return UserController.getOne(filter);
    }

    @PostMapping(path = "/create", consumes = {"multipart/form-data"})
    public User createUser(@RequestPart(required = true, name = "user") User user, @RequestPart(required = false, name = "image") MultipartFile file) throws  GetUserException {
        UserController.save(user, file);
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
