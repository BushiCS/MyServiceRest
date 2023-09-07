package ru.sviridov.repositories;

import ru.sviridov.entities.User;
import ru.sviridov.mappers.JdbcMapper;
import ru.sviridov.sessions.JDBCSessionManager;
import ru.sviridov.sessions.SessionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository implements JDBCRepository<User> {
    private final JdbcMapper mapper = new JdbcMapper();

    private final SessionManager sessionManager;

    public UserRepository(SessionManager sessionManager){
        this.sessionManager = sessionManager;
    }
    public UserRepository() {
        this.sessionManager = new JDBCSessionManager();
    }

    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        sessionManager.beginSession();
        String sqlQuery = "SELECT id, name FROM users;";
        try (Connection connection = sessionManager.getCurrentSession();
             Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(sqlQuery);
            users = mapper.mapToUsers(rs);
            sessionManager.commitSession();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public User getById(long id) {
        User user = null;
        sessionManager.beginSession();
        try (Connection connection = sessionManager.getCurrentSession();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE id = (?);")) {
            ps.setLong(1, id);
            ResultSet resultSet = ps.executeQuery();
            user = mapper.mapToUser(resultSet);
            sessionManager.commitSession();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public long update(long id, User user) {
        long updatedRows = 0;
        String sqlQuery = "UPDATE users set name= (?) where id=(?)";
        sessionManager.beginSession();
        try (Connection connection = sessionManager.getCurrentSession();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setString(1, user.getName());
            statement.setLong(2, id);
            updatedRows = statement.executeUpdate();
            sessionManager.commitSession();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updatedRows;
    }

    @Override
    public long delete(long userId) {
        long deletedRows = 0;
        String sqlQuery = "DELETE FROM users WHERE id =(?)";
        sessionManager.beginSession();
        try (Connection connection = sessionManager.getCurrentSession();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setLong(1, userId);
            deletedRows = statement.executeUpdate();
            sessionManager.commitSession();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deletedRows;
    }

    @Override
    public boolean insert(User user) {
        sessionManager.beginSession();
        String sqlQuery = "INSERT INTO users (name) VALUES ((?));";
        try (Connection connection = sessionManager.getCurrentSession();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setString(1, user.getName());
            statement.executeUpdate();
            sessionManager.commitSession();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public User getProductUserByUserId(long userId, long productId) {
        User user = null;
        String sqlQuery = "select u.id, u.name from users u join users_products up " +
                "on u.id = up.user_id and u.id = (?) join products p on p.id = up.product_id and p.id = (?);";
        sessionManager.beginSession();
        try (Connection connection = sessionManager.getCurrentSession();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setLong(1, userId);
            statement.setLong(2, productId);
            ResultSet set = statement.executeQuery();
            user = mapper.mapToProductUser(set);
            sessionManager.commitSession();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public List<User> getProductUsers(long productId) {
        List<User> userList = new ArrayList<>();
        String sqlQuery = "select u.id, u.name from users u join users_products up " +
                "on u.id = up.user_id join products p on p.id = up.product_id and p.id = (?);";
        sessionManager.beginSession();
        try (Connection connection = sessionManager.getCurrentSession();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setLong(1, productId);
            ResultSet set = statement.executeQuery();
            userList = mapper.mapToProductUsers(set);
            sessionManager.commitSession();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

    public int addProductByUserName(String productName, String userName) {
        int insertedRows = 0;
        String sqlQuery = "insert into users_products (user_id, product_id) select u.id, p.id from users u, products p" +
                " where u.name = ? and p.title = ?;";
        sessionManager.beginSession();
        try (Connection connection = sessionManager.getCurrentSession();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setString(1, userName);
            statement.setString(2, productName);
            insertedRows = statement.executeUpdate();
            sessionManager.commitSession();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return insertedRows;
    }
}
