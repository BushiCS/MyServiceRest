package ru.sviridov.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.sviridov.entities.Card;
import ru.sviridov.fabric.RepositoryFabric;
import ru.sviridov.mappers.JdbcMapper;
import ru.sviridov.repositories.CardRepository;
import ru.sviridov.repositories.UserRepository;
import ru.sviridov.sessions.SessionManager;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CardService implements EntityService<Card> {

    private final CardRepository cardRepository;


    public CardService() {
        this.cardRepository = RepositoryFabric.createCardRepository();
    }

    public CardService(SessionManager manager) {
        cardRepository = new CardRepository(manager);
    }

    @Override
    public List<Card> getAll() {
        return cardRepository.getAll();
    }

    @Override
    public Card getById(long id) {
        return cardRepository.getById(id);
    }

    @Override
    public boolean insert(Card card) throws IOException {
        return cardRepository.insert(card);
    }

    public CardRepository getCardRepository() {
        return cardRepository;
    }

    @Override
    public long update(long id, Card card) throws IOException {
        return cardRepository.update(id, card);
    }

    @Override
    public long deleteById(long id) {
        return cardRepository.delete(id);
    }

    public Card getUserCardByCardId(long cardId, long userId) {
        return cardRepository.getUserCardByCardId(cardId, userId);
    }

    public List<Card> getUserCards(long userId) {
        return cardRepository.getUserCards(userId);
    }
}
