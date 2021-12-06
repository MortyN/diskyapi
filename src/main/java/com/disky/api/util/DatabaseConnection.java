package com.disky.api.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {

    private DatabaseConnection() {}

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;
    private static final int DALMANTINERE = 30;

    static {
        config.setJdbcUrl("jdbc:mysql://"+System.getenv("DISKYAPIDBSERVERNAME")+".mysql.database.azure.com:3306/"+System.getenv("DISKYAPIDBNAME")+"?useSSL=TRUE&SSLMODE=VERIFY-FULL&sslrootcert="+System.getenv("MYSQL_SSL_CA"));
        config.setUsername( System.getenv("DISKYAPIADMINUSER") );
        config.setPassword( System.getenv("DISKYAPIADMINPASS") );
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
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        ds = new HikariDataSource( config );
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}