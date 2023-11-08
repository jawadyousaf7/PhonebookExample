package org.vaadin.dao;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static Connection connection;

    /* Getting database connection */

    static {
        try {
            FileReader file = new FileReader(DBConnection.class.getClassLoader().getResource("application.properties").getFile());
            Properties properties = new Properties();
            properties.load(file);
            connection = DriverManager.getConnection(properties.getProperty("db.url"), properties.getProperty("db.username"), properties.getProperty("db.password"));
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private DBConnection() {
    }

    public static Connection getConnection() {
        return connection;
    }

}
