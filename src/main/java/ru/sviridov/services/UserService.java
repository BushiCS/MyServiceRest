package ru.sviridov.services;
import ru.sviridov.entities.User;
import ru.sviridov.fabric.RepositoryFabric;
import ru.sviridov.repositories.UserRepository;
import ru.sviridov.sessions.SessionManager;

import java.io.IOException;
import java.util.List;

public class UserService implements EntityService<User> {
    private final UserRepository userRepository;

    public UserService() { userRepository = RepositoryFabric.createUserRepository();}

    public UserService(SessionManager manager) {
        userRepository = new UserRepository(manager);
    }

    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    @Override
    public User getById(long id) {
        return userRepository.getById(id);
    }

    @Override
    public boolean insert(User user) throws IOException {
        return userRepository.insert(user);
    }

    @Override
    public long update(long id, User user) throws IOException {
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

    public int addProductToUser(String[] params) {
        int insertedRows;
        String userName = params[0];
        String productName = params[1];
        insertedRows = userRepository.addProductByUserName(productName, userName);
        return insertedRows;
    }
}
