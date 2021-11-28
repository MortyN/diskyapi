package com.disky.api;

import com.disky.api.util.DatabaseConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.TimeZone;

@RestController
@SpringBootApplication
@CrossOrigin(origins = "*")
public class DiskyApiApplication extends SpringBootServletInitializer {
    private static final Logger log = LogManager.getLogger(DiskyApiApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DiskyApiApplication.class, args);

        log.info("DiskyApi is now running...");
    }

    @RequestMapping(value = "/")
    public String hello() {
        return "DiskyApi health check 200 OK";
    }


}
