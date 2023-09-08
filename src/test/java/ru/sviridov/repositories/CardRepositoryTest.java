package ru.sviridov.repositories;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.sviridov.entities.Card;
import ru.sviridov.sessionManager.SessionManagerImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Testcontainers
public class CardRepositoryTest {

    private static Statement statement;
    private static Connection connection;
    private static CardRepository repository;
    public static PostgreSQLContainer<?> container;

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
            repository = new CardRepository(new SessionManagerImpl(
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
    @DisplayName("get all cards")
    void getAll() {
        List<Card> expectedCards = List.of(
                new Card(1, "VTB", "123 321", 1),
                new Card(2, "SBER", "235 211", 1),
                new Card(3, "TINKOFF", "542 243", 2),
                new Card(4, "TINKOFF", "233 712", 1),
                new Card(5, "VTB", "236 213", 3),
                new Card(6, "VTB", "213 217", 4)
        );

        List<Card> actualCards = repository.getAll();
        Assertions.assertEquals(expectedCards, actualCards);
    }

    @Test
    @DisplayName("get cards by id")
    void getById() {
        long id = 1;
        Card expectedCard = new Card(id, "VTB", "123 321", 1);
        Card actualCard = repository.getById(id);
        Assertions.assertEquals(expectedCard, actualCard);
    }

    @Test
    @DisplayName("insert card")
    void insert() {
        Card card = new Card(10, "VTB+", "236 214", 3);
        boolean actual = repository.insert(card);
        Assertions.assertTrue(actual);
    }


    @Test
    @DisplayName("update card")
    void update() {
        long expected = 1;
        long id = 1;
        Card card = new Card();
        card.setTitle("VTB+SBER");
        long actual = repository.update(id, card);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("delete card")
    void delete() {
        long id = 1;
        long expected = 1;
        long actual = repository.delete(id);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("get user cards")
    void getUserCards() {
        List<Card> expected = List.of(
                new Card(3, "TINKOFF", "542 243", 2)
        );
        long userId = 2;
        List<Card> actual = repository.getUserCards(userId);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("get user card by card id")
    void getUserCardByCardId() {
        long userId = 1;
        long cardId = 2;
        Card expected = new Card(2, "SBER", "235 211", 1);
        Card actual = repository.getUserCardByCardId(userId, cardId);
        Assertions.assertEquals(expected, actual);
    }
}
