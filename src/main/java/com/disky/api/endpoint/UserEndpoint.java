package com.disky.api.endpoint;

import com.disky.api.model.User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/v1/user")
@RestController
@CrossOrigin
public class UserEndpoint {
    @GetMapping("/getOne")
    public User getOne() {
        return new User(1L);
    }

}
