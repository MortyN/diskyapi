package com.disky.api.util;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
@PropertySource("classpath:application.properties")
public class DatabaseConnection {
    static Connection con = null;

    public static Connection getConnection() {
        if (con != null) return con;
        return getConnection("jdbc:mysql://92.220.179.219:3306/MOB3100_DEV", "MOB3100_DEV", "Platinum");
    }

    private static Connection getConnection(String url, String user_name, String password) {
        try {
            con = DriverManager.getConnection(url, user_name, password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return con;
    }

}
