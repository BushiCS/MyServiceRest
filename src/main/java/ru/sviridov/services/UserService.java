package ru.sviridov.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.sviridov.entities.Product;
import ru.sviridov.entities.User;
import ru.sviridov.fabric.RepositoryFabric;
import ru.sviridov.mappers.JdbcMapper;
import ru.sviridov.repositories.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class UserService implements EntityService<User> {
    private final UserRepository userRepository;

    private final ObjectMapper objectMapper;
    private final JdbcMapper mapper;
    public UserService() {
        mapper = new JdbcMapper();
        objectMapper = new ObjectMapper();
        userRepository = RepositoryFabric.createUserRepository();
    }

    @Override
    public List<User> getAll() {
        return userRepository.getAll();

    }

    @Override
    public User getById(long id) {
        return userRepository.getById(id);
    }

    @Override
    public boolean insert(HttpServletRequest req) throws IOException {
        String body = req.getReader().lines().collect(Collectors.joining());
        User user = objectMapper.readValue(body, User.class);
        return userRepository.insert(user);
    }

    @Override
    public long update(long id, HttpServletRequest req) throws IOException {
        User user = mapper.mapJsonToUser(req);
        return userRepository.update(id, user);
    }

    @Override
    public long deleteById(long id) {
        return userRepository.delete(id);
    }


    public List<User> getProductUsers(long productId) {
        return userRepository.getProductUsers(productId);
    }

    public User getProductUserByUserId(long userId, long productId) {
        return userRepository.getProductUserByUserId(userId, productId);
    }

    public int addProductToUser(HttpServletRequest req) throws IOException {
        int insertedRows = 0;
        String body = req.getReader().lines().collect(Collectors.joining());
        String cutArrayBrackets = body.substring(1, body.length() - 1);
        if (cutArrayBrackets.split(",").length != 0) {
            String[] split = cutArrayBrackets.split(",");
            String userBody = split[0];
            String productBody = split[1];
            User user = objectMapper.readValue(userBody, User.class);
            Product product = objectMapper.readValue(productBody, Product.class);
            insertedRows = userRepository.addProductByUserName(product.getTitle(), user.getName());
        }
        return insertedRows;
    }
}
