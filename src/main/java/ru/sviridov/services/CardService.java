package ru.sviridov.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.sviridov.entities.Card;
import ru.sviridov.fabric.RepositoryFabric;
import ru.sviridov.mappers.JdbcMapper;
import ru.sviridov.repositories.CardRepository;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CardService implements EntityService<Card> {
    private final ObjectMapper objectMapper;

    private final CardRepository cardRepository;

    private final JdbcMapper mapper;

    public CardService() {
        mapper = new JdbcMapper();
        this.objectMapper = new ObjectMapper();
        this.cardRepository = RepositoryFabric.createCardRepository();
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
    public boolean insert(HttpServletRequest req) throws IOException {
        String body = req.getReader().lines().collect(Collectors.joining());
        Card card = objectMapper.readValue(body, Card.class);
        return cardRepository.insert(card);
    }

    @Override
    public long update(long id, HttpServletRequest req) throws IOException {
        Card card = mapper.mapJsonToCard(req);
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
