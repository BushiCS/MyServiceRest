package ru.sviridov.services;

import org.junit.ClassRule;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.sviridov.entities.Product;
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
public class ProductServiceTest {
    static ProductService service;

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
            service = new ProductService(manager);
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
    @DisplayName("get all products")
    void getAllProducts() {
        List<Product> products = List.of(
                new Product(1, "Milk", 80),
                new Product(2, "Cheese", 200),
                new Product(3, "Bread", 60),
                new Product(4, "Pasta", 70),
                new Product(5, "Eggs", 90)
        );
        Assertions.assertEquals(products, service.getAll());
    }

    @Test
    @DisplayName("get product by id")
    void getById(){
        Product product = new Product();
        long id = 1;
        product.setId(1);
        product.setTitle("Milk");
        product.setPrice(80);
        Assertions.assertEquals(product,service.getById(id));
    }

    @Test
    @DisplayName("update product")
    void update() throws IOException {
        long id = 1;
        Product product = new Product();
        product.setTitle("Milkshake");
        long updatedRows = 1;
        Assertions.assertEquals(updatedRows,service.update(id, product));
    }

    @Test
    @DisplayName("insert product")
    void insert() throws IOException {
        Product product = new Product();
        product.setTitle("Pechen'e");
        product.setId(10);
        product.setPrice(80);
        Assertions.assertTrue(service.insert(product));
    }

    @Test
    @DisplayName("delete product")
    void delete(){
        long id = 1;
        long deletedRows = 1;
        Assertions.assertEquals(deletedRows, service.deleteById(id));
    }

    @Test
    @DisplayName("get user products")
    void getUserProducts(){
        long userId = 3;
        List<Product> products = List.of(
                new Product(1, "Milk", 80),
                new Product(5, "Eggs", 90)
        );
        Assertions.assertEquals(products, service.getUserProducts(userId));
    }

    @Test
    @DisplayName("get user product by id")
    void getUserProductById(){
        long userId = 3;
        long productId = 1;
        Product product = new Product(1, "Milk", 80);
        Assertions.assertEquals(product,service.getUserProductByProductId(productId, userId));
    }

    @Test
    @DisplayName("test empty constructor")
    void testEmptyConstructor(){
        ProductService productService = new ProductService();
        String expected = "ProductRepository{sessionManager=JDBCSessionManager{connection=null}}";
        Assertions.assertEquals(expected, productService.getProductRepository().toString());
    }
}
