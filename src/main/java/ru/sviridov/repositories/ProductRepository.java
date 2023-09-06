package ru.sviridov.repositories;

import ru.sviridov.entities.Product;
import ru.sviridov.mappers.JdbcMapper;
import ru.sviridov.sessions.JDBCSessionManager;
import ru.sviridov.sessions.SessionManager;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository implements JDBCRepository<Product> {
    private final JdbcMapper mapper = new JdbcMapper();

    private final SessionManager sessionManager;

    public ProductRepository(SessionManager sessionManager){
        this.sessionManager = sessionManager;
    }

    public ProductRepository() {
        this.sessionManager = new JDBCSessionManager();
    }


    @Override
    public List<Product> getAll() {
        List<Product> products = new ArrayList<>();
        sessionManager.beginSession();
        String sqlQuery = "SELECT id, title, price FROM products;";
        try (Connection connection = sessionManager.getCurrentSession();
             Statement statement = connection.createStatement()) {
            ResultSet set = statement.executeQuery(sqlQuery);
            products = mapper.mapToProducts(set);
            sessionManager.commitSession();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public Product getById(long id) {
        Product product = null;
        sessionManager.beginSession();
        try (Connection connection = sessionManager.getCurrentSession();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM products WHERE id = (?);")) {
            ps.setLong(1, id);
            ResultSet set = ps.executeQuery();
            product = mapper.mapToProduct(set);
            sessionManager.commitSession();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }


    @Override
    public long update(long id, Product product) {
        long updatedRows = 0;
        String sqlQuery = "UPDATE products set title = (?), price = (?) where id=(?)";
        sessionManager.beginSession();
        try (Connection connection = sessionManager.getCurrentSession();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setString(1, product.getTitle());
            statement.setInt(2, product.getPrice());
            statement.setLong(3, id);
            updatedRows = statement.executeUpdate();
            sessionManager.commitSession();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updatedRows;
    }

    @Override
    public boolean insert(Product product) {
        sessionManager.beginSession();
        String sqlQuery = "INSERT INTO products (title, price) values ((?), (?));";
        try (Connection connection = sessionManager.getCurrentSession();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setString(1, product.getTitle());
            statement.setInt(2, product.getPrice());
            statement.executeUpdate();
            sessionManager.commitSession();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public long delete(long id) {
        long deletedRows = 0;
        String sqlQuery = "DELETE FROM products WHERE id =(?)";
        sessionManager.beginSession();
        try (Connection connection = sessionManager.getCurrentSession();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setLong(1, id);
            deletedRows = statement.executeUpdate();
            sessionManager.commitSession();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deletedRows;
    }

    public List<Product> getUserProducts(long userId) {
        List<Product> productList = new ArrayList<>();
        String sqlQuery = "select p.id, p.title, p.price from products p join users_products up " +
                "on p.id = up.product_id join users u on u.id = up.user_id and u.id = (?);";
        sessionManager.beginSession();
        try (Connection connection = sessionManager.getCurrentSession();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            productList = mapper.mapToProducts(resultSet);
            sessionManager.commitSession();
        } catch (SQLException e) {
            sessionManager.rollbackSession();
            e.printStackTrace();
        }
        return productList;
    }

    public Product getUserProductByProductId(long productId, long userId) {
        Product product = null;
        String sqlQuery = "select p.id, p.title, p.price from products p join users_products up " +
                "on p.id = up.product_id and p.id = (?) join users u on u.id = up.user_id and u.id = (?);";
        sessionManager.beginSession();
        try (Connection connection = sessionManager.getCurrentSession();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setLong(1, productId);
            statement.setLong(2, userId);
            ResultSet set = statement.executeQuery();
            product = mapper.mapToProduct(set);
            sessionManager.commitSession();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }
}
