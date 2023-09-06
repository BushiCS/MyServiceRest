package ru.sviridov.listener;

import ru.sviridov.sessions.JDBCSessionManager;
import ru.sviridov.sessions.SessionManager;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@WebListener
public class ContextListener implements ServletContextListener {

    private SessionManager manager;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        manager = new JDBCSessionManager();
        ServletContext context = sce.getServletContext();
        context.setAttribute("manager", manager);
        createTableFromFile();
    }


    public void createTableFromFile() {
        String sqlFile = "/create-table-script.SQL";
        manager.beginSession();
        try (Connection connection = manager.getCurrentSession();
             Statement statement = connection.createStatement();
             InputStream inputStream = JDBCSessionManager.class.getResourceAsStream(sqlFile)) {
            if (inputStream != null) {
                String query = new String(inputStream.readAllBytes());
                statement.execute(query);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

    }
}
