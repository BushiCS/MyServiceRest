package ru.sviridov.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.sviridov.entities.Card;
import ru.sviridov.mappers.RequestMapper;
import ru.sviridov.services.CardService;
import ru.sviridov.services.ProductService;
import ru.sviridov.services.UserService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

@WebServlet({"/cards/*", "/cards"})
public class RestCardServlet extends HttpServlet {
    private CardService cardService;
    private RequestMapper mapper;

    private PrintWriter printWriter;
    private String pathInfo;
    private BufferedReader bufferedReader;

    public RestCardServlet(CardService cardService, HttpServletRequest request, HttpServletResponse response, ObjectMapper objectMapper) throws IOException {
        this.cardService = cardService;
        printWriter = response.getWriter();
        pathInfo = request.getPathInfo();
        bufferedReader = request.getReader();
        mapper = new RequestMapper(objectMapper, bufferedReader);
    }

    public RestCardServlet() {
        cardService = new CardService();
        mapper = new RequestMapper();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        pathInfo = req.getPathInfo();
        setCharacterEncoding(req);
        resp.setContentType("application/json; charset=UTF-8");
        try {
            printWriter = resp.getWriter();
            if (pathInfo == null || pathInfo.split("/").length == 0) {
                String jsonCards = new ObjectMapper().writeValueAsString(cardService.getAll());
                resp.setStatus(200);
                printWriter.write(jsonCards);
            } else {
                String[] parts = pathInfo.split("/");
                String firstId = parts[1];
                long cardId = Long.parseLong(firstId);
                Card card = cardService.getById(cardId);
                if (card == null) {
                    printWriter.write("Not found");
                    resp.setStatus(404);
                } else {
                    final String jsonCard = new ObjectMapper().writeValueAsString(card);
                    resp.setStatus(200);
                    printWriter.write(jsonCard);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        pathInfo = req.getPathInfo();
        setCharacterEncoding(req);
        if (pathInfo.matches("^/$")) {
            try {
                Card card = mapper.mapToCard(req);
                boolean isInserted = cardService.insert(card);
                if (isInserted) {
                    printWriter.write("Card has added");
                    resp.setStatus(200);
                } else {
                    printWriter.write("Card hasn't been added");
                    resp.setStatus(400);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) {
        pathInfo = req.getPathInfo();
        setCharacterEncoding(req);
        if (pathInfo.matches("^/\\d+$")) {
            String[] parts = pathInfo.split("/");
            try {
                printWriter = resp.getWriter();
                long cardId = Long.parseLong(parts[1]);
                Card card = mapper.mapJsonToCard(req);
                long updatedRows = cardService.update(cardId, card);
                if (updatedRows != 0) {
                    printWriter.write("Card was updated");
                    resp.setStatus(200);
                } else {
                    printWriter.write("Card not found");
                    resp.setStatus(400);
                }
            } catch (IOException e) {
                e.printStackTrace();
                resp.setStatus(400);
            }
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        pathInfo = req.getPathInfo();
        setCharacterEncoding(req);
        if (pathInfo.matches("^/\\d+$")) {
            String[] parts = pathInfo.split("/");
            try {
                long cardId = Long.parseLong(parts[1]);
                long deletedRows = cardService.deleteById(cardId);
                if (deletedRows != 0) {
                    resp.setStatus(200);
                    resp.getWriter().write("Card was deleted");
                } else {
                    resp.setStatus(400);
                    resp.getWriter().write("couldn't delete");
                }
            } catch (NumberFormatException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void setCharacterEncoding(HttpServletRequest req) {
        try {
            req.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
