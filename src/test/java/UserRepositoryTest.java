import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.ClassRule;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.sviridov.entities.User;
import ru.sviridov.mappers.JdbcMapper;
import ru.sviridov.repositories.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Testcontainers
public class UserRepositoryTest {
    private static Statement statement;
    private static Connection connection;
    private static UserRepository repository;

    private JdbcMapper mapper = new JdbcMapper();
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
            repository = new UserRepository(new SessionManagerImpl(
                    "org.postgresql.Driver",
                    postgreSQLContainer.getJdbcUrl(),
                    postgreSQLContainer.getUsername(),
                    postgreSQLContainer.getPassword()));
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
    void getAll() {
        List<User> userList = new ArrayList<>();
        String sqlQuery = "SELECT * FROM users";
        try {
            ResultSet set = statement.executeQuery(sqlQuery);
            userList = mapper.mapToUsers(set);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals(userList, repository.getAll());
    }

    @Test
    @DisplayName("get user by id")
    void getById() {
        long id = 1;
        User user = null;
        String sqlQuery = "SELECT * FROM users where id = (?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setLong(1, id);
            ResultSet set = preparedStatement.executeQuery();
            user = mapper.mapToUser(set);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals(user, repository.getById(id));
    }

    @Test
    @DisplayName("update user")
    void update() {
        long updatedRows = 0;
        long id = 1;
        User user = new User();
        user.setId(1);
        user.setName("Michael");
        String sqlQuery = "UPDATE users set name= (?) where id=(?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setLong(2, id);
            updatedRows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException();
        }
        Assertions.assertEquals(updatedRows, repository.update(id, user));
    }

    @Test
    @DisplayName("insert user")
    void insert() {
        String sqlQuery = "INSERT INTO users VALUES ((?), (?));";
        User user = new User();
        user.setId(25);
        user.setName("Eugene");
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setLong(1, user.getId());
            preparedStatement.setString(2, user.getName());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertTrue(repository.insert(user));
        Assertions.assertEquals(user, repository.getById(user.getId()));
    }

    @Test
    @DisplayName("delete user by id")
    void delete() {
        long firstUserId = 2;
        long secondUserId = 50;
        long deletedRows;
        String deleteSQLQuery = "DELETE FROM users WHERE id = (?)";
        String insertSQLQuery = "INSERT INTO users values (50, 'Mick');";
        try {
            Statement statement = connection.createStatement();
            statement.execute(insertSQLQuery);
        } catch (SQLException e) {
            throw new RuntimeException();
        }
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(deleteSQLQuery);
            preparedStatement.setLong(1, firstUserId);
            deletedRows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException();
        }
        Assertions.assertEquals(deletedRows, repository.delete(secondUserId));
        Assertions.assertNotEquals(deletedRows, repository.delete(firstUserId)); //already deleted
    }

    @Test
    @DisplayName("get product users")
    void getProductUsers() {
        long productId = 1;
        List<User> users = new ArrayList<>();
        String sqlQuery = "select u.id, u.name from users u join users_products up " +
                "on u.id = up.user_id join products p on p.id = up.product_id and p.id = (?);";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setLong(1, productId);
            ResultSet set = preparedStatement.executeQuery();
            users = mapper.mapToUsers(set);
        } catch (SQLException e) {
            throw new RuntimeException();
        }
        Assertions.assertEquals(users, repository.getProductUsers(productId));
    }

    @Test
    @DisplayName("get product user by user id")
    void getProductUserByUserId() {
        long userId = 2;
        long productId = 1;
        User user = null;
        String sqlQuery = "select u.id, u.name from users u join users_products up " +
                "on u.id = up.user_id and u.id = (?) join products p on p.id = up.product_id and p.id = (?);";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, productId);
            ResultSet set = preparedStatement.executeQuery();
            user = mapper.mapToUser(set);
        } catch (SQLException e) {
            throw new RuntimeException();
        }
        Assertions.assertEquals(user, repository.getProductUserByUserId(userId, productId));
    }

    @Test
    @DisplayName("add product to user by name")
    void addProductToUser() {
        String productName = "Milk";
        String userName = "Jack";
        String sqlQuery = "insert into users_products (user_id, product_id) select u.id, p.id from users u, products p" +
                " where u.name = ? and p.title = ?;";
        int insertedRows = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, productName);
            insertedRows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException();
        }
        Assertions.assertEquals(insertedRows, repository.addProductByUserName(productName, userName));
    }


}
