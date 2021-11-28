package com.disky.api.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {

    private DatabaseConnection() {}

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;
    private static final int DALMANTINERE = 101;
    static {
        config.setJdbcUrl( "jdbc:mysql://"+System.getenv("DISKY_DB_IP")+":3306/MOB3100_DEV" );
        config.setUsername( "MOB3100_DEV" );
        config.setPassword( "Platinum" );
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setMaximumPoolSize(DALMANTINERE);
        config.setConnectionTimeout(5000);
        config.setLeakDetectionThreshold(10000);
        config.setLeakDetectionThreshold(10000);
        config.setIdleTimeout(30000);
        config.setMaxLifetime(50000);
        config.addDataSourceProperty( "leakDetectionThreshold" , "10000" );
        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        ds = new HikariDataSource( config );
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}