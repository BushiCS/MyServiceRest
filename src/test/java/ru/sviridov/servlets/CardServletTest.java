package ru.sviridov.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.sviridov.entities.Card;
import ru.sviridov.mappers.RequestMapper;
import ru.sviridov.services.CardService;
import ru.sviridov.servlets.RestCardServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CardServletTest {

    HttpServletRequest request;
    HttpServletResponse response;
    CardService cardService;
    PrintWriter writer;
    RestCardServlet restCardServlet;
    BufferedReader bufferedReader;
    ObjectMapper objectMapper;
    RequestMapper mapper;

    @BeforeEach
    void initMocks() throws IOException {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        cardService = mock(CardService.class);
        writer = mock(PrintWriter.class);
        objectMapper = mock(ObjectMapper.class);
        Mockito.when(response.getWriter()).thenReturn(writer);


        bufferedReader = Mockito.mock(BufferedReader.class);
        Mockito.when(request.getReader()).thenReturn(bufferedReader);

        restCardServlet = new RestCardServlet(cardService, request, response, objectMapper);
        mapper = new RequestMapper(objectMapper, bufferedReader);
    }


    @Test
    @DisplayName("do get")
    void doGet(){
        long cardId = 3;
        List<Card> expected = List.of(
                new Card(1, "VTB", "123 321", 1),
                new Card(2, "SBER", "231 412", 1),
                new Card(3, "TINKOFF", "531 516", 1)
        );
        when(request.getPathInfo()).thenReturn("/");
        restCardServlet.doGet(request, response);
        when(cardService.getAll()).thenReturn(expected);
        Assertions.assertEquals(expected,cardService.getAll());
    }


    @Test
    @DisplayName("do post")
    void doPost() throws IOException {
        Card card = new Card(1, "VTB", "123 321", 1);
        Mockito.when(request.getPathInfo()).thenReturn("/");
        restCardServlet.doPost(request,response);
        when(cardService.insert(card)).thenReturn(true);
        Assertions.assertTrue(cardService.insert(card));
    }

    @Test
    @DisplayName("do put")
    void doPut() throws IOException {
        Card card = new Card();
        card.setTitle("VTB");
        card.setNumber("123 321");
        card.setUserId(1);
        long id = 1;
        long updatedRows = 1;
        Mockito.when(request.getPathInfo()).thenReturn("/1");
        restCardServlet.doPut(request,response);
        when(objectMapper.readValue(bufferedReader, Card.class)).thenReturn(card);
        mapper = new RequestMapper(objectMapper, bufferedReader);
        when(mapper.mapJsonToCard(request)).thenReturn(card);
        when(cardService.update(id,card)).thenReturn(updatedRows);
        Assertions.assertEquals(updatedRows,cardService.update(id, card));
    }

    @Test
    @DisplayName("do delete")
    void doDelete(){
        long id = 1;
        long deletedRows = 1;
        when(request.getPathInfo()).thenReturn("/1");
        when(cardService.deleteById(id)).thenReturn(deletedRows);
        restCardServlet.doDelete(request, response);
        Assertions.assertEquals(deletedRows, cardService.deleteById(id));
    }
}
