package ru.sviridov.repositories;

import org.junit.ClassRule;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.sviridov.entities.Product;
import ru.sviridov.mappers.JdbcMapper;
import ru.sviridov.sessionManager.SessionManagerImpl;
import ru.sviridov.sessions.SessionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Testcontainers
public class ProductRepositoryTest {

    private static Statement statement;
    private static Connection connection;
    private static ProductRepository repository;

    private final JdbcMapper mapper = new JdbcMapper();

    static PostgreSQLContainer<?> container;

    @BeforeAll
    public static void connect() {
        try {
            container = new PostgreSQLContainer<>("postgres");
            container.start();
            String jdbcUrl = container.getJdbcUrl();
            String username = container.getUsername();
            String password = container.getPassword();
            connection = DriverManager.getConnection(jdbcUrl, username, password);
            statement = connection.createStatement();
            repository = new ProductRepository(new SessionManagerImpl(
                    "org.postgresql.Driver",
                    container.getJdbcUrl(),
                    container.getUsername(),
                    container.getPassword()));
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    @BeforeEach
    public void prepareData() {
        try {
            String sql = Files.lines(Paths.get("src/test/create-test-table.sql")).collect(Collectors.joining(" "));
            statement.execute(sql);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }


    @AfterAll
    public static void disconnect() {
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("get all products")
    void getAll() {
        List<Product> productList = new ArrayList<>();
        String sqlQuery = "SELECT * FROM products";
        try {
            ResultSet set = statement.executeQuery(sqlQuery);
            productList = mapper.mapToProducts(set);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals(productList, repository.getAll());
    }

    @Test
    @DisplayName("get product by id")
    void getById() {
        Product product;
        long id = 1;
        String sqlQuery = "SELECT * FROM products WHERE id = (?);";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setLong(1, id);
            ResultSet set = preparedStatement.executeQuery();
            product = mapper.mapToProduct(set);
        } catch (SQLException e) {
            throw new RuntimeException();
        }
        Assertions.assertEquals(product, repository.getById(id));
    }

    @Test
    @DisplayName("update product")
    void update() {
        long updatedRows;
        long id = 1;
        Product product = new Product();
        String sqlQuery = "UPDATE products set title = (?), price = (?) where id=(?)";
        product.setTitle("Almond Milk");
        product.setPrice(180);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, product.getTitle());
            preparedStatement.setInt(2, product.getPrice());
            preparedStatement.setLong(3, id);
            updatedRows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(updatedRows, repository.update(id, product));
    }

    @Test
    @DisplayName("insert product")
    void insert() {
        Product product = new Product();
        product.setId(25);
        product.setTitle("Tomatoes 1 kg");
        product.setPrice(50);
        String sqlQuery = "INSERT INTO products values ((?), (?), (?));";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setLong(1, product.getId());
            preparedStatement.setString(2, product.getTitle());
            preparedStatement.setInt(3, product.getPrice());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertTrue(repository.insert(product));
        Assertions.assertEquals(product, repository.getById(product.getId()));
    }

    @Test
    @DisplayName("delete product by id")
    void deleteById() {
        long firstProductId = 2;
        long secondProductId = 50;
        long deletedRows;
        String deleteSQLQuery = "DELETE FROM products WHERE id =(?)";
        String insertSQLQuery = "INSERT INTO products values (50, 'Cherry tomatoes', 100);";
        try {
            Statement statement = connection.createStatement();
            statement.execute(insertSQLQuery);
        } catch (SQLException e) {
            throw new RuntimeException();
        }
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(deleteSQLQuery);
            preparedStatement.setLong(1, firstProductId);
            deletedRows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(deletedRows, repository.delete(secondProductId));
        Assertions.assertNotEquals(deletedRows, repository.delete(firstProductId)); //already deleted
    }

    @Test
    @DisplayName("get user products")
    void getUserProductsById() {
        long id = 1;
        List<Product> products;
        String sqlQuery = "select p.id, p.title, p.price from products p join users_products up " +
                "on p.id = up.product_id join users u on u.id = up.user_id and u.id = (?);";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setLong(1, id);
            ResultSet set = preparedStatement.executeQuery();
            products = mapper.mapToProducts(set);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(products, repository.getUserProducts(id));
    }

    @Test
    @DisplayName("get user product by product id")
    void getUserProductByProductId() {
        Product product;
        long userId = 3;
        long productId = 5;
        String sqlQuery = "select p.id, p.title, p.price from products p join users_products up " +
                "on p.id = up.product_id and p.id = (?) join users u on u.id = up.user_id and u.id = (?);";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setLong(1, productId);
            preparedStatement.setLong(2, userId);
            ResultSet set = preparedStatement.executeQuery();
            product = mapper.mapToProduct(set);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(product, repository.getUserProductByProductId(5, 3));
    }


}
