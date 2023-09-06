package ru.sviridov.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.sviridov.entities.Card;
import ru.sviridov.entities.Product;
import ru.sviridov.entities.User;
import ru.sviridov.services.CardService;
import ru.sviridov.services.ProductService;
import ru.sviridov.services.UserService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

@WebServlet(urlPatterns = {"/users/*", "/users"})
public class RestUserServlet extends HttpServlet {
    private UserService userService;
    private CardService cardService;
    private ProductService productService;

    @Override
    public void init() {
        userService = new UserService();
        cardService = new CardService();
        productService = new ProductService();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String pathInfo = req.getPathInfo();
        setCharacterEncoding(req);
        resp.setContentType("application/json; charset=UTF-8");
        try {
            PrintWriter out = resp.getWriter();
            if (pathInfo == null || pathInfo.split("/").length == 0) {
                String jsonUsers = new ObjectMapper().writeValueAsString(userService.getAll());
                resp.setStatus(200);
                out.write(jsonUsers);
            } else {
                String[] parts = pathInfo.split("/");
                String firstId = parts[1];
                long userId = Long.parseLong(firstId);
                switch (parts.length) {
                    case 2: {
                        User user = userService.getById(userId);
                        if (user == null) {
                            out.write("Not found");
                            resp.setStatus(404);
                        } else {
                            final String jsonUser = new ObjectMapper().writeValueAsString(user);
                            resp.setStatus(200);
                            out.write(jsonUser);
                        }
                        break;
                    }
                    case 3: {
                        String entityName = parts[2];
                        if (entityName.equals("cards")) {
                            String jsonCards = new ObjectMapper().writeValueAsString(cardService.getUserCards(userId));
                            resp.setStatus(200);
                            out.write(jsonCards);
                        } else if (entityName.equals("products")) {
                            String jsonProducts = new ObjectMapper().writeValueAsString(productService.getUserProducts(userId));
                            resp.setStatus(200);
                            out.write(jsonProducts);
                        }
                        break;
                    }
                    case 4: {
                        String entityName = parts[2];
                        if (entityName.equals("cards")) {
                            String secondId = parts[3];
                            long cardId = Long.parseLong(secondId);
                            Card card = cardService.getUserCardByCardId(userId, cardId);
                            if (card == null) {
                                out.write("Card not found");
                                resp.setStatus(404);
                            } else {
                                final String jsonCard = new ObjectMapper().writeValueAsString(card);
                                resp.setStatus(200);
                                out.write(jsonCard);
                            }
                        } else if (entityName.equals("products")) {
                            String secondId = parts[3];
                            long productId = Long.parseLong(secondId);
                            Product product = productService.getUserProductByProductId(productId, userId);
                            if (product == null) {
                                out.write("Product not found");
                                resp.setStatus(404);
                            } else {
                                final String jsonProduct = new ObjectMapper().writeValueAsString(product);
                                resp.setStatus(200);
                                out.write(jsonProduct);
                            }
                        }
                        break;
                    }
                    default:
                        resp.setStatus(401);
                        out.write("invalid request");
                }
            }
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String pathInfo = req.getPathInfo();
        setCharacterEncoding(req);
        if (pathInfo.matches("^/$")) {
            try {
                PrintWriter out = resp.getWriter();
                boolean isInserted = userService.insert(req);
                if (isInserted) {
                    out.write("User has added");
                    resp.setStatus(200);
                } else {
                    out.write("User hasn't been added");
                    resp.setStatus(400);
                }
            } catch (IOException e) {
                e.printStackTrace();
                resp.setStatus(400);
            }
        } else if (pathInfo.matches("/purchase/$")) {
            try {
                PrintWriter out = resp.getWriter();
                int insertedRows = userService.addProductToUser(req);
                if (insertedRows == 0){
                    resp.setStatus(400);
                    out.write("couldn't add");
                } else {
                    resp.setStatus(200);
                    out.write("added");
                }
            } catch (IOException e) {
                e.printStackTrace();
                resp.setStatus(400);
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
                long userId = Long.parseLong(parts[1]);
                PrintWriter out = resp.getWriter();
                long updatedRows = userService.update(userId, req);
                if (updatedRows != 0) {
                    out.write("User was updated");
                    resp.setStatus(200);
                } else {
                    out.write("User not found");
                    resp.setStatus(400);
                }
            } catch (NumberFormatException | IOException e) {
                resp.setStatus(400);
                e.printStackTrace();

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
                PrintWriter out = resp.getWriter();
                long userId = Long.parseLong(parts[1]);
                long deletedRows = userService.deleteById(userId);
                if (deletedRows != 0) {
                    resp.setStatus(200);
                    out.write("User was deleted");
                } else {
                    resp.setStatus(400);
                    out.write("couldn't delete");
                }
            } catch (NumberFormatException | IOException e) {
                e.printStackTrace();
                resp.setStatus(400);
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
