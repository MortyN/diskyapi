package com.disky.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;


@SpringBootApplication
public class DiskyApiApplication {
    private static final Logger log = LogManager.getLogger(DiskyApiApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DiskyApiApplication.class, args);
        log.info("DiskyApi is now running...");
    }

}
