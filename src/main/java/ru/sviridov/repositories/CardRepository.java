package ru.sviridov.repositories;

import ru.sviridov.entities.Card;
import ru.sviridov.mappers.JdbcMapper;
import ru.sviridov.sessions.JDBCSessionManager;
import ru.sviridov.sessions.SessionManager;

import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CardRepository implements JDBCRepository<Card> {
    private final JdbcMapper mapper = new JdbcMapper();

    private final SessionManager sessionManager;

    public CardRepository() {
        sessionManager = new JDBCSessionManager();
    }

    public List<Card> getAll() {
        List<Card> cards = new ArrayList<>();
        sessionManager.beginSession();
        String sqlQuery = "SELECT * FROM cards;";
        try (Connection connection = sessionManager.getCurrentSession();
             Statement statement = connection.createStatement()) {
            ResultSet set = statement.executeQuery(sqlQuery);
            cards = mapper.mapToCards(set);
            sessionManager.commitSession();
        } catch (SQLException e) {
            sessionManager.rollbackSession();
            e.printStackTrace();
        }
        return cards;
    }

    @Override
    public Card getById(long id) {
        Card card = null;
        sessionManager.beginSession();
        String sqlQuery = "SELECT * from cards where id = (?)";
        try (Connection connection = sessionManager.getCurrentSession();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setLong(1, id);
            ResultSet set = statement.executeQuery();
            card = mapper.mapToCard(set);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return card;
    }

    @Override
    public boolean insert(Card card){
        sessionManager.beginSession();
        String sqlQuery = "INSERT INTO cards (title, number, fk_cards_users) values ((?), (?), (?));";
        try (Connection connection = sessionManager.getCurrentSession();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setString(1, card.getTitle());
            statement.setString(2, card.getNumber());
            statement.setLong(3, card.getUserId());
            statement.executeUpdate();
            sessionManager.commitSession();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public long update(long id, Card card) {
        long updatedRows = 0;
        String sqlQuery = "UPDATE cards set title = (?) where id = (?);";
        sessionManager.beginSession();
        try (Connection connection = sessionManager.getCurrentSession();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setString(1, card.getTitle());
            statement.setLong(2, id);
            updatedRows = statement.executeUpdate();
            sessionManager.commitSession();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updatedRows;
    }

    @Override
    public long delete(long id) {
        long deletedRows = 0;
        String sqlQuery = "DELETE FROM cards WHERE id = (?)";
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

    public List<Card> getUserCards(long userId) {
        List<Card> userCards = new ArrayList<>();
        sessionManager.beginSession();
        String sqlQuery = "SELECT c.id, c.title, c.number, c.fk_cards_users from cards c " +
                "where fk_cards_users =(?)";
        try (Connection connection = sessionManager.getCurrentSession();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            userCards = mapper.mapToCards(resultSet);
            sessionManager.commitSession();
        } catch (SQLException e) {
            sessionManager.rollbackSession();
            e.printStackTrace();
        }
        return userCards;
    }

    public Card getUserCardByCardId(long userId, long cardId) {
        Card card = null;
        String sqlQuery = "SELECT id, title, number, fk_cards_users from cards " +
                "where fk_cards_users = (?) and id = (?)";
        sessionManager.beginSession();
        try (Connection connection = sessionManager.getCurrentSession();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setLong(1, userId);
            statement.setLong(2, cardId);
            ResultSet resultSet = statement.executeQuery();
            card = mapper.mapToCard(resultSet);
            sessionManager.commitSession();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return card;
    }
}
