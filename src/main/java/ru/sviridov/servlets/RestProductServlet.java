package ru.sviridov.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.sviridov.entities.Product;
import ru.sviridov.entities.User;
import ru.sviridov.services.ProductService;
import ru.sviridov.services.UserService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

@WebServlet({"/products/*", "/products"})
public class RestProductServlet extends HttpServlet {

    UserService userService;
    private ProductService productService;

    @Override
    public void init() {
        userService = new UserService();
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
                String jsonProducts = new ObjectMapper().writeValueAsString(productService.getAll());
                resp.setStatus(200);
                out.write(jsonProducts);
            } else {
                String[] parts = pathInfo.split("/");
                String firstId = parts[1];
                long productId = Long.parseLong(firstId);
                switch (parts.length) {
                    case 2: {
                        Product product = productService.getById(productId);
                        if (product == null) {
                            out.write("Not found");
                            resp.setStatus(404);
                        } else {
                            final String jsonProduct = new ObjectMapper().writeValueAsString(product);
                            resp.setStatus(200);
                            out.write(jsonProduct);
                        }
                        break;
                    }
                    case 3: {
                        String entityName = parts[2];
                        if (entityName.equals("users")) {
                            String jsonCards = new ObjectMapper().writeValueAsString(userService.getProductUsers(productId));
                            resp.setStatus(200);
                            out.write(jsonCards);
                        }
                        break;
                    }
                    case 4: {
                        String entityName = parts[2];
                        if (entityName.equals("users")) {
                            String secondId = parts[3];
                            long userId = Long.parseLong(secondId);
                            User user = userService.getProductUserByUserId(userId, productId);
                            if (user == null) {
                                out.write("User not found");
                                resp.setStatus(404);
                            } else {
                                final String jsonCard = new ObjectMapper().writeValueAsString(user);
                                resp.setStatus(200);
                                out.write(jsonCard);
                            }
                        }
                        break;
                    }
                    default:
                        resp.setStatus(401);
                        out.write("invalid request");
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String pathInfo = req.getPathInfo();
        setCharacterEncoding(req);
        if (pathInfo.matches("^/$")) {
            try {
                boolean isInserted = productService.insert(req);
                if (isInserted) {
                    resp.getWriter().write("Product has added");
                    resp.setStatus(200);
                } else {
                    resp.getWriter().write("Product hasn't been added");
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
                long productId = Long.parseLong(parts[1]);
                long updatedRows = productService.update(productId, req);
                if (updatedRows != 0) {
                    resp.getWriter().write("Product was updated");
                    resp.setStatus(200);
                } else {
                    resp.getWriter().write("Product not found");
                    resp.setStatus(400);
                }
            } catch (IOException | NumberFormatException e) {
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
                long productId = Long.parseLong(parts[1]);
                long deletedRows = productService.deleteById(productId);
                if (deletedRows != 0) {
                    resp.setStatus(200);
                    resp.getWriter().write("Product has deleted");
                } else {
                    resp.setStatus(400);
                    resp.getWriter().write("couldn't delete");
                }
            } catch (IOException | NumberFormatException e) {
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
