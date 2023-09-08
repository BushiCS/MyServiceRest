package ru.sviridov.services;

import org.junit.ClassRule;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.sviridov.sessionManager.SessionManagerImpl;
import ru.sviridov.entities.Card;
import ru.sviridov.sessions.SessionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Testcontainers
public class CardServiceTest {
    static CardService service;

    private static Statement statement;

    private static Connection connection;

    static PostgreSQLContainer<?> container;


    @BeforeAll
    public static void connect() {
        container = new PostgreSQLContainer<>("postgres");
        container.start();
        String jdbcUrl = container.getJdbcUrl();
        String username = container.getUsername();
        String password = container.getPassword();
        container.start();
        try {
            connection = DriverManager.getConnection(jdbcUrl, username, password);
            statement = connection.createStatement();
            SessionManager manager = new SessionManagerImpl("org.postgresql.Driver",
                    container.getJdbcUrl(),
                    container.getUsername(),
                    container.getPassword());
            service = new CardService(manager);
        } catch (SQLException e) {
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
    @DisplayName("get all cards")
    void getAll() {
        List<Card> cards = new ArrayList<>();
        Card vtb = new Card(1, "VTB", "123 321", 1);
        Card sber = new Card(2, "SBER", "235 211", 1);
        Card tinkoff1 = new Card(3, "TINKOFF", "542 243", 2);
        Card tinkoff2 = new Card(4, "TINKOFF", "233 712", 1);
        Card vtb2 = new Card(5, "VTB", "236 213", 3);
        Card vtb3 = new Card(6, "VTB", "213 217", 4);
        cards.add(vtb);
        cards.add(sber);
        cards.add(tinkoff1);
        cards.add(tinkoff2);
        cards.add(vtb2);
        cards.add(vtb3);

        Assertions.assertEquals(cards, service.getAll());
    }

    @Test
    @DisplayName("get card by id")
    void getById() {
        long id = 1;
        Card card = new Card(1, "VTB", "123 321", 1);
        Assertions.assertEquals(card, service.getById(id));
    }

    @Test
    @DisplayName("insert")
    void insert() throws IOException {
        Card card = new Card(10, "OTKRITIE", "922 275", 3);
        Assertions.assertTrue(service.insert(card));
    }

    @Test
    @DisplayName("update")
    void update() throws IOException {
        long id = 1;
        Card card = new Card();
        card.setTitle("SBER-");
        long updatedRows = 1;
        Assertions.assertEquals(updatedRows, service.update(id, card));
    }

    @Test
    @DisplayName("delete")
    void delete() {
        long id = 1;
        long deletedRows = 1;
        Assertions.assertEquals(deletedRows, service.deleteById(id));
    }

    @Test
    @DisplayName("get user cards")
    void getUserCards() {
        long userId = 1;
        List<Card> cards = List.of(new Card(1, "VTB", "123 321", 1),
                new Card(2, "SBER", "235 211", 1),
                new Card(4, "TINKOFF", "233 712", 1));
        Assertions.assertEquals(cards, service.getUserCards(userId));
    }

    @Test
    @DisplayName("get user cards by card id")
    void getUserCardById() {
        Card card = new Card(1, "VTB", "123 321", 1);
        long userId = 1;
        long cardId = 1;
        Assertions.assertEquals(card, service.getUserCardByCardId(1, 1));
    }

    @Test
    @DisplayName("test empty constructor")
    void testConstructor() {
        CardService cardService = new CardService();
        String expected = "CardRepository{sessionManager=JDBCSessionManager{connection=null}}";
        Assertions.assertEquals(expected, cardService.getCardRepository().toString());
    }
}
