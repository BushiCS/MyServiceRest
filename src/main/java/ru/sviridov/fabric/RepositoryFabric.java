package ru.sviridov.fabric;

import ru.sviridov.repositories.CardRepository;
import ru.sviridov.repositories.ProductRepository;
import ru.sviridov.repositories.UserRepository;

public class RepositoryFabric {

    private RepositoryFabric() {
    }

    public static UserRepository createUserRepository() {
        return new UserRepository();
    }

    public static CardRepository createCardRepository() {
        return new CardRepository();
    }

    public static ProductRepository createProductRepository() {
        return new ProductRepository();
    }
}
