package ru.sviridov.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.sviridov.entities.Card;
import ru.sviridov.entities.Product;
import ru.sviridov.entities.User;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class RequestMapper {
    private ObjectMapper objectMapper;
    private BufferedReader bufferedReader;

    public RequestMapper() {
        objectMapper = new ObjectMapper();
    }

    public RequestMapper(ObjectMapper objectMapper, BufferedReader bufferedReader) {
        this.objectMapper = objectMapper;
        this.bufferedReader = bufferedReader;
    }

    public String[] mapRequestForProductAndUser(HttpServletRequest req) throws IOException {
        String[] names = new String[0];
        Stream<String> lines = bufferedReader.lines();
        String body = lines.collect(Collectors.joining());
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
        bufferedReader = req.getReader();
        return objectMapper.readValue(bufferedReader, User.class);
    }

    public User mapJsonToUser(HttpServletRequest req) {
        User user = null;
        try {
            bufferedReader = req.getReader();
            user = objectMapper.readValue(bufferedReader, User.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }

    public Card mapJsonToCard(HttpServletRequest req) {
        Card card = null;
        try {
            bufferedReader = req.getReader();
            card = objectMapper.readValue(bufferedReader, Card.class);
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
            bufferedReader = req.getReader();
            product = objectMapper.readValue(bufferedReader, Product.class);
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
