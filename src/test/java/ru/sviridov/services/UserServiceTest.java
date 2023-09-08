package ru.sviridov.services;

import org.junit.ClassRule;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.sviridov.entities.User;
import ru.sviridov.sessionManager.SessionManagerImpl;
import ru.sviridov.sessions.SessionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

@Testcontainers
public class UserServiceTest {

    static UserService service;

    private static Statement statement;

    private static Connection connection;
    @Container
    @ClassRule
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres")
            .withUsername("postgres")
            .withDatabaseName("my_db")
            .withPassword("admin");


    @BeforeAll
    public static void connect() {
        postgreSQLContainer.start();
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(
                    postgreSQLContainer.getJdbcUrl(),
                    postgreSQLContainer.getUsername(),
                    postgreSQLContainer.getPassword()
            );
            statement = connection.createStatement();
            SessionManager manager = new SessionManagerImpl("org.postgresql.Driver",
                    postgreSQLContainer.getJdbcUrl(),
                    postgreSQLContainer.getUsername(),
                    postgreSQLContainer.getPassword());
            service = new UserService(manager);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
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
    @DisplayName("get all users")
    void getAllTest() {
        List<User> users = List.of(
                new User(1, "Bill"),
                new User(2, "Jack"),
                new User(3, "Kevin"),
                new User(4, "Michael"),
                new User(5, "Ann")
        );
        Assertions.assertEquals(users, service.getAll());
    }

    @Test
    @DisplayName("get user by id")
    void getUserById() {
        long id = 2;
        User user = new User(2, "Jack");
        Assertions.assertEquals(user, service.getById(id));
    }

    @Test
    @DisplayName("insert user")
    void insert() throws IOException {
        User user = new User(10, "Justin");
        Assertions.assertTrue(service.insert(user));
    }

    @Test
    @DisplayName("update user")
    void update() throws IOException {
        long id = 1;
        User user = new User();
        user.setName("Jack");
        int expectedRows = 1;
        Assertions.assertEquals(expectedRows, service.update(id, user));
    }

    @Test
    @DisplayName("delete user")
    void delete() {
        long id = 1;
        int deletedExpectedRows = 1;
        Assertions.assertEquals(deletedExpectedRows, service.deleteById(id));
    }

    @Test
    @DisplayName("get product users")
    void getProductUsers() {
        long id = 1;
        List<User> users = List.of(new User(2, "Jack"),
                new User(3, "Kevin"));
        Assertions.assertEquals(users, service.getProductUsers(id));
    }

    @Test
    @DisplayName("get product user by user id")
    void getProductUserByUserId() {
        long userId = 2;
        long productId = 1;
        User expectedUser = new User(2, "Jack");
        Assertions.assertEquals(expectedUser, service.getProductUserByUserId(userId, productId));
    }

    @Test
    @DisplayName("add product to user")
    void addProductToUser() {
        String[] params = new String[]{"Kevin", "Milk"};
        int expectedRows = 1;
        Assertions.assertEquals(expectedRows, service.addProductToUser(params));
    }

    @Test
    @DisplayName("test empty constructor")
    void testEmptyConstructor(){
        UserService testService = new UserService();
        String expected = "UserRepository{mapper=JdbcMapper{}, sessionManager=JDBCSessionManager{connection=null}}";
        Assertions.assertEquals(expected, testService.getUserRepository().toString());
    }
}
