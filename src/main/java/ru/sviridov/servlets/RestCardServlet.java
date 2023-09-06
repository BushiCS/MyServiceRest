package ru.sviridov.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.sviridov.entities.Card;
import ru.sviridov.services.CardService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

@WebServlet({"/cards/*", "/cards"})
public class RestCardServlet extends HttpServlet {

    private CardService cardService;

    @Override
    public void init() {
        cardService = new CardService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String pathInfo = req.getPathInfo();
        setCharacterEncoding(req);
        resp.setContentType("application/json; charset=UTF-8");
        try {
            PrintWriter out = resp.getWriter();
            if (pathInfo == null || pathInfo.split("/").length == 0) {
                String jsonCards = new ObjectMapper().writeValueAsString(cardService.getAll());
                resp.setStatus(200);
                out.write(jsonCards);
            } else {
                String[] parts = pathInfo.split("/");
                String firstId = parts[1];
                long cardId = Long.parseLong(firstId);
                Card card = cardService.getById(cardId);
                if (card == null) {
                    out.write("Not found");
                    resp.setStatus(404);
                } else {
                    final String jsonCard = new ObjectMapper().writeValueAsString(card);
                    resp.setStatus(200);
                    out.write(jsonCard);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String pathInfo = req.getPathInfo();
        setCharacterEncoding(req);
        if (pathInfo.matches("^/$")) {
            try {
                boolean isInserted = cardService.insert(req);
                if (isInserted) {
                    resp.getWriter().write("Card has added");
                    resp.setStatus(200);
                } else {
                    resp.getWriter().write("Card hasn't been added");
                    resp.setStatus(400);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        String pathInfo = req.getPathInfo();
        setCharacterEncoding(req);
        if (pathInfo.matches("^/\\d+$")) {
            String[] parts = pathInfo.split("/");
            try {
                long cardId = Long.parseLong(parts[1]);
                long updatedRows = cardService.update(cardId, req);
                if (updatedRows != 0) {
                    resp.getWriter().write("Card was updated");
                    resp.setStatus(200);
                } else {
                    resp.getWriter().write("Card not found");
                    resp.setStatus(400);
                    throw new SQLException();
                }
            } catch (IOException | SQLException e) {
                e.printStackTrace();
                resp.setStatus(400);
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        String pathInfo = req.getPathInfo();
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
