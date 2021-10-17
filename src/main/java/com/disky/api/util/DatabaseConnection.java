package com.disky.api.util;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class DatabaseConnection {
    public static Connection connect() throws ClassNotFoundException, SQLException {
        DataSourceProperties properties = new DataSourceProperties();
        return DriverManager.getConnection(properties.getUrl(), properties.getName(), properties.getPassword());
    }

}
