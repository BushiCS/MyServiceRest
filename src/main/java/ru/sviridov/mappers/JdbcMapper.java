package ru.sviridov.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.sviridov.entities.Card;
import ru.sviridov.entities.Product;
import ru.sviridov.entities.User;
import ru.sviridov.repositories.CardRepository;
import ru.sviridov.repositories.JDBCRepository;
import ru.sviridov.repositories.ProductRepository;
import ru.sviridov.repositories.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class JdbcMapper {
    private final ObjectMapper objectMapper = new ObjectMapper();



    public List<User> mapToUsers(ResultSet resultSet) throws SQLException {
        List<User> users = new ArrayList<>();
        CardRepository cardRepository = new CardRepository();
        ProductRepository productRepository = new ProductRepository();
        while (resultSet.next()) {
            String name = resultSet.getString("name");
            long id = resultSet.getLong("id");
            User user = new User();
            user.setId(id);
            user.setName(name);
            user.setCards(cardRepository.getUserCards(id));
            user.setProducts(productRepository.getUserProducts(id));
            users.add(user);
        }
        return users;
    }

    public List<User> mapToProductUsers(ResultSet resultSet) throws SQLException {
        List<User> users = new ArrayList<>();
        CardRepository cardRepository = new CardRepository();
        while (resultSet.next()) {
            String name = resultSet.getString("name");
            long id = resultSet.getLong("id");
            User user = new User();
            user.setId(id);
            user.setName(name);//TODO задать список продуктов
            user.setCards(cardRepository.getUserCards(id));
            users.add(user);
        }
        return users;
    }

    public List<Card> mapToCards(ResultSet resultSet) throws SQLException {
        List<Card> cards = new ArrayList<>();
        while (resultSet.next()) {
            long id = resultSet.getLong("id");
            String cardName = resultSet.getString("title");
            String number = resultSet.getString("number");
            long idCardsUsers = resultSet.getLong("fk_cards_users");
            Card card;
            card = new Card(id, cardName, number, idCardsUsers);
            cards.add(card);
        }
        return cards.stream().distinct()
                .sorted(Comparator.comparingLong(Card::getId))
                .collect(Collectors.toList());
    }

    public Card mapToCard(ResultSet resultSet) throws SQLException {
        Card card = null;
        while (resultSet.next()) {
            long id = resultSet.getLong("id");
            String title = resultSet.getString("title");
            String number = resultSet.getString("number");
            long idCardsUsers = resultSet.getLong("fk_cards_users");
            card = new Card(id, title, number, idCardsUsers);
        }
        return card;
    }

    public User mapToUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        CardRepository cardRepository = new CardRepository();
        ProductRepository productRepository = new ProductRepository();
        while (resultSet.next()) {
            long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            user = new User();
            user.setId(id);
            user.setName(name);
            user.setCards(cardRepository.getUserCards(id));
            user.setProducts(productRepository.getUserProducts(id));
        }
        return user;
    }

    public User mapToProductUser(ResultSet resultSet) throws SQLException {
        User user = null;
        CardRepository cardRepository = new CardRepository();
        while (resultSet.next()) {
            long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            user = new User();
            user.setId(id);
            user.setName(name);
            user.setCards(cardRepository.getUserCards(id));
        }
        return user;
    }


    public List<Product> mapToProducts(ResultSet resultSet) throws SQLException {
        List<Product> products = new ArrayList<>();
        UserRepository userRepository = new UserRepository();
        while (resultSet.next()) {
            long id = resultSet.getLong("id");
            String title = resultSet.getString("title");
            int price = resultSet.getInt("price");
            Product product;
            product = new Product(id, title, price);
            product.setUsers(userRepository.getProductUsers(id));
            products.add(product);
        }
        return products;
    }

    public List<Product> mapToUserProducts(ResultSet resultSet) throws SQLException {
        List<Product> products = new ArrayList<>();
        while (resultSet.next()) {
            long id = resultSet.getLong("id");
            String title = resultSet.getString("title");
            int price = resultSet.getInt("price");
            Product product;
            product = new Product(id, title, price);
            products.add(product);
        }
        return products;
    }

    public Product mapToProduct(ResultSet resultSet) throws SQLException {
        Product product = null;
        UserRepository userRepository = new UserRepository();
        while (resultSet.next()) {
            long id = resultSet.getLong("id");
            String title = resultSet.getString("title");
            int price = resultSet.getInt("price");
            product = new Product(id, title, price);
            product.setUsers(userRepository.getProductUsers(id));
        }
        return product;
    }

    public Product mapToUserProduct(ResultSet resultSet) throws SQLException {
        Product product = null;
        while (resultSet.next()) {
            long id = resultSet.getLong("id");
            String title = resultSet.getString("title");
            int price = resultSet.getInt("price");
            product = new Product(id, title, price);
        }
        return product;
    }

    public User mapJsonToUser(HttpServletRequest req) {
        User user = null;
        try {
            String requestBody = req.getReader().lines().collect(Collectors.joining());
            user = objectMapper.readValue(requestBody, User.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }

    public Card mapJsonToCard(HttpServletRequest req) {
        Card card = null;
        try {
            String requestBody = req.getReader().lines().collect(Collectors.joining());
            card = objectMapper.readValue(requestBody, Card.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return card;
    }

    public Product mapJsonToProduct(HttpServletRequest req) throws IOException {
        Product product = null;
        try {
            String requestBody = req.getReader().lines().collect(Collectors.joining());
            product = objectMapper.readValue(requestBody, Product.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return product;
    }
}
