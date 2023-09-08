package ru.sviridov.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.sviridov.entities.User;
import ru.sviridov.mappers.JdbcMapper;
import ru.sviridov.sessionManager.SessionManagerImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Testcontainers
public class UserRepositoryTest {
    private static Statement statement;
    private static Connection connection;
    private static UserRepository repository;

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
            repository = new UserRepository(new SessionManagerImpl(
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
    @DisplayName("get all users")
    void getAll() {
        List<User> userList = List.of(
                new User(1, "Bill"),
                new User(2, "Jack"),
                new User(3,"Kevin"),
                new User(4,"Michael"),
                new User(5,"Ann")
        );
        Assertions.assertEquals(userList, repository.getAll());
    }

    @Test
    @DisplayName("get user by id")
    void getById() {
        long id = 1;
        User expectedUser = new User(id, "Bill");
        User actualUser = repository.getById(id);
        Assertions.assertEquals(expectedUser, actualUser);
    }

    @Test
    @DisplayName("update user")
    void update() {
        long updatedRows = 1;
        long id = 1;
        User user = new User(id, "Michael");
        long actualRows = repository.update(id, user);
        Assertions.assertEquals(updatedRows, actualRows);
    }

    @Test
    @DisplayName("insert user")
    void insert() {
        User user = new User(10, "Pavel");
        repository.insert(user);
        Assertions.assertTrue(repository.insert(user));
    }

    @Test
    @DisplayName("delete user by id")
    void delete() {
        long userId = 2;
        long expected = 1;
        long actual = repository.delete(userId);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("get product users")
    void getProductUsers() {
        long productId = 1;
        List<User> expectedUsers = List.of(
                new User(2, "Jack"),
                new User(3, "Kevin"));
        List<User> actualUsers = repository.getProductUsers(productId);
        Assertions.assertEquals(expectedUsers, actualUsers);
    }

    @Test
    @DisplayName("get product user by user id")
    void getProductUserByUserId() {
        long userId = 2;
        long productId = 1;
        User expectedUser = new User(userId, "Jack");
        User actualUser = repository.getProductUserByUserId(userId, productId);
        Assertions.assertEquals(expectedUser, actualUser);
    }

    @Test
    @DisplayName("add product to user by name")
    void addProductToUser() {
        String productName = "Milk";
        String userName = "Jack";
        int insertedRows = 1;
        int actualRows = repository.addProductByUserName(productName, userName);
        Assertions.assertEquals(insertedRows, actualRows);
    }
}
