package ru.sviridov.sessions;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCSessionManager implements SessionManager, AutoCloseable {

    private Connection connection;
    private static final Properties properties = new Properties();

    @Override
    public void beginSession() {
        try {
            properties.load(JDBCSessionManager.class.getResourceAsStream("/jdbc.properties"));
            String driver = properties.getProperty("db.driver");
            String host = properties.getProperty("db.host");
            String login = properties.getProperty("db.login");
            String password = properties.getProperty("db.password");
            String schema = properties.getProperty("db.schema");
            Class.forName(driver);
            connection = DriverManager.getConnection(host, login, password);
            connection.setSchema(schema);
            connection.setAutoCommit(false);
        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void commitSession() {
        try {
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rollbackSession() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection getCurrentSession() {
        return connection;
    }

    @Override
    public String toString() {
        return "JDBCSessionManager{" +
                "connection=" + connection +
                '}';
    }
}
