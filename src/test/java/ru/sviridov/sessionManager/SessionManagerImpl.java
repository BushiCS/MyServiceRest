package ru.sviridov.sessionManager;

import ru.sviridov.sessions.SessionManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SessionManagerImpl implements SessionManager {
    private String driver;
    private String host;
    private String login;
    private String password;

    private Connection connection;

    public SessionManagerImpl(String driver, String host, String login, String password) {
        this.driver = driver;
        this.host = host;
        this.login = login;
        this.password = password;
    }

    @Override
    public void beginSession() {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(host, login, password);
            connection.setAutoCommit(false);
        } catch (SQLException | ClassNotFoundException e){
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
}
