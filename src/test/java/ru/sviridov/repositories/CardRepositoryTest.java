package ru.sviridov.repositories;

import org.junit.ClassRule;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.sviridov.entities.Card;
import ru.sviridov.mappers.JdbcMapper;
import ru.sviridov.sessionManager.SessionManagerImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;
@Testcontainers
public class CardRepositoryTest {

    private static Statement statement;
    private static Connection connection;
    private static CardRepository repository;

    private final JdbcMapper mapper = new JdbcMapper();

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
        List<Card> cards;
        String sqlQuery = "SELECT * FROM cards;";
        try {
            ResultSet set = statement.executeQuery(sqlQuery);
            cards = mapper.mapToCards(set);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(cards, repository.getAll());
    }

    @Test
    @DisplayName("get cards by id")
    void getById() {
        Card card;
        long id = 1;
        String sqlQuery = "SELECT * FROM cards WHERE id = (?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setLong(1, id);
            ResultSet set = preparedStatement.executeQuery();
            card = mapper.mapToCard(set);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(card, repository.getById(id));
    }

    @Test
    @DisplayName("insert card")
    void insert() {
        Card card = new Card();
        card.setId(10);
        card.setTitle("VTB+");
        card.setNumber("236 124");
        card.setUserId(3);
        String sqlQuery = "INSERT INTO cards values ((?), (?), (?), (?));";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setLong(1, card.getId());
            preparedStatement.setString(2, card.getTitle());
            preparedStatement.setString(3, card.getNumber());
            preparedStatement.setLong(4, card.getUserId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertTrue(repository.insert(card));
        Assertions.assertEquals(card, repository.getById(card.getId()));
    }


    @Test
    @DisplayName("update card")
    void update() {
        long updatedRows;
        long id = 1;
        String sqlQuery = "UPDATE cards set title = (?) where id = (?);";
        Card card = new Card();
        card.setTitle("VTB+");
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, card.getTitle());
            preparedStatement.setLong(2, id);
            updatedRows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(updatedRows, repository.update(id, card));
    }

    @Test
    @DisplayName("delete card")
    void delete() {
        long firstCardId = 2;
        long secondCardId = 50;
        long deletedRows;
        String deleteSQLQuery = "DELETE FROM cards WHERE id =(?)";
        String insertSQLQuery = "INSERT INTO cards values (50, 'OTKRITIE','231 678', 2);";
        try {
            Statement statement = connection.createStatement();
            statement.execute(insertSQLQuery);
        } catch (SQLException e) {
            throw new RuntimeException();
        }
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(deleteSQLQuery);
            preparedStatement.setLong(1, firstCardId);
            deletedRows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(deletedRows, repository.delete(secondCardId));
        Assertions.assertNotEquals(deletedRows, repository.delete(firstCardId)); //already deleted
    }

    @Test
    @DisplayName("get user cards")
    void getUserCards(){
        List<Card> cards;
        long userId = 2;
        String sqlQuery = "SELECT c.id, c.title, c.number, c.fk_cards_users from cards c " +
                "where fk_cards_users =(?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setLong(1, userId);
            ResultSet set = preparedStatement.executeQuery();
            cards = mapper.mapToCards(set);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(cards, repository.getUserCards(userId));
    }

    @Test
    @DisplayName("get user card by card id")
    void getUserCardByCardId(){
        Card card;
        long userId = 1;
        long cardId = 2;
        String sqlQuery = "SELECT id, title, number, fk_cards_users from cards " +
                "where fk_cards_users = (?) and id = (?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, cardId);
            ResultSet set = preparedStatement.executeQuery();
            card = mapper.mapToCard(set);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(card, repository.getUserCardByCardId(userId, cardId));
    }


}
