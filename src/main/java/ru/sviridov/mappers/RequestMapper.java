package ru.sviridov.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.sviridov.entities.Card;
import ru.sviridov.entities.Product;
import ru.sviridov.entities.User;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.stream.Collectors;

public class RequestMapper {
    ObjectMapper objectMapper = new ObjectMapper();

    public String[] mapRequestForProductAndUser(HttpServletRequest req) throws IOException {
        String[] names = new String[0];
        String body = req.getReader().lines().collect(Collectors.joining());
        String cutArrayBrackets = body.substring(1, body.length() - 1);
        if (cutArrayBrackets.split(",").length != 0) {
            String[] split = cutArrayBrackets.split(",");
            String userBody = split[0];
            String productBody = split[1];
            User user = objectMapper.readValue(userBody, User.class);
            Product product = objectMapper.readValue(productBody, Product.class);
            names = new String[]{user.getName(), product.getTitle()};
        }
        return names;
    }

    public User mapToUser(HttpServletRequest req) throws IOException {
        String body = req.getReader().lines().collect(Collectors.joining());
        return objectMapper.readValue(body, User.class);
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

    public Card mapToCard(HttpServletRequest req) throws IOException {
        String body = req.getReader().lines().collect(Collectors.joining());
        return objectMapper.readValue(body, Card.class);
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

    public Product mapToProduct(HttpServletRequest req) throws IOException {
        String body = req.getReader().lines().collect(Collectors.joining());
        return objectMapper.readValue(body, Product.class);
    }

    @Override
    public String toString() {
        return "RequestMapper{" +
                "objectMapper=" + objectMapper +
                '}';
    }
}
