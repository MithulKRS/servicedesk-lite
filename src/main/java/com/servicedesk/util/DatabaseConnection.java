package com.servicedesk.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL  = System.getenv().getOrDefault("DB_URL",
            "jdbc:mysql://localhost:3306/servicedesk?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
    private static final String USER = System.getenv().getOrDefault("DB_USER", "servicedesk_app");
    private static final String PASS = System.getenv().getOrDefault("DB_PASSWORD", "changeme");

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
