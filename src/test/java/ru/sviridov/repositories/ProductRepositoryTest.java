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
import java.util.stream.Stream;

@Testcontainers
public class ProductRepositoryTest {

    private static Statement statement;
    private static Connection connection;
    private static ProductRepository repository;
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
        try (Stream<String> lines = Files.lines(Paths.get("src/test/create-test-table.sql"))) {
            String sql = lines.collect(Collectors.joining());
            statement.execute(sql);
        } catch (IOException | SQLException e) {
            e.getLocalizedMessage();

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
        List<Product> expected = List.of(
                new Product(1, "Milk", 80),
                new Product(2, "Cheese", 200),
                new Product(3, "Bread", 60),
                new Product(4, "Pasta", 70),
                new Product(5, "Eggs", 90)
        );
        List<Product> actual = repository.getAll();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("get product by id")
    void getById() {
        Product expected = new Product(1, "Milk", 80);
        long id = 1;
        Product actual = repository.getById(id);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("update product")
    void update() {
        long id = 1;
        Product product = new Product(1, "Coconut Milk", 150);
        long expected = 1;
        long actual = repository.update(id, product);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("insert product")
    void insert() {
        Product product = new Product(25, "Tomatoes 1 kg", 90);
        boolean actual = repository.insert(product);
        Assertions.assertTrue(actual);
    }

    @Test
    @DisplayName("delete product by id")
    void deleteById() {
        long id = 1;
        long expected = 1;
        long actual = repository.delete(id);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("get user products")
    void getUserProductsById() {
        long id = 2;
        List<Product> expected = List.of(
                new Product(1, "Milk", 80),
                new Product(3, "Bread", 60)
        );
        List<Product> actual = repository.getUserProducts(id);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("get user product by product id")
    void getUserProductByProductId() {
        Product expected = new Product(5, "Eggs", 90);
        long userId = 3;
        long productId = 5;
        Product actual = repository.getUserProductByProductId(productId, userId);
        Assertions.assertEquals(expected, actual);
    }


}
