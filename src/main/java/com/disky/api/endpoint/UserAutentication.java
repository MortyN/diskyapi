package com.disky.api.endpoint;

import com.disky.api.Exceptions.GetUserException;
import com.disky.api.controller.UserController;
import com.disky.api.filter.testFilter;
import com.disky.api.model.User;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RequestMapping("/api/v1/auth")
@RestController
@CrossOrigin
public class UserAutentication {

    @PostMapping
    public static User auth(@RequestParam(required = true) String userName, @RequestParam(required = true) String password) throws SQLException, GetUserException {
        return UserController.authUser(userName, password);
    }
}