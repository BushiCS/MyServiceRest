package ru.sviridov.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.sviridov.entities.Product;
import ru.sviridov.mappers.RequestMapper;
import ru.sviridov.services.ProductService;
import ru.sviridov.services.UserService;
import ru.sviridov.servlets.RestProductServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static org.mockito.Mockito.*;

public class ProductServletTest {

    HttpServletRequest request;
    HttpServletResponse response;
    UserService userService;
    ProductService productService;
    PrintWriter writer;
    RestProductServlet productServlet;
    BufferedReader bufferedReader;
    ObjectMapper objectMapper;
    RequestMapper mapper;

    @BeforeEach
    void initMocks() throws IOException {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        userService = mock(UserService.class);

        productService = mock(ProductService.class);
        writer = mock(PrintWriter.class);
        objectMapper = mock(ObjectMapper.class);
        Mockito.when(response.getWriter()).thenReturn(writer);
        Mockito.when(request.getPathInfo()).thenReturn("/");

        bufferedReader = Mockito.mock(BufferedReader.class);
        Mockito.when(request.getReader()).thenReturn(bufferedReader);

        productServlet = new RestProductServlet(userService, productService, request, response, objectMapper);
        mapper = new RequestMapper(objectMapper, bufferedReader);
    }

    @Test
    @DisplayName("doGet test")
    void doGet() {

        long userId = 2;
        long productId = 1;
        List<Product> expectedProducts = List.of(
                new Product(1, "Milk", 80),
                new Product(2, "Cheese", 150
                ));
        Product expectedProduct = new Product(1, "Milk", 80);
        when(productService.getAll()).thenReturn(expectedProducts);
        Assertions.assertEquals(expectedProducts, productService.getAll());
        productServlet.doGet(request, response);
        when(request.getPathInfo()).thenReturn("/1");
        when(productService.getById(productId)).thenReturn(expectedProduct);
        productServlet.doGet(request, response);
        Assertions.assertEquals(expectedProduct,productService.getById(productId));
    }

    @Test
    @DisplayName("doPost test")
    void doPost() throws IOException {
        Product product = new Product();
        product.setTitle("Cheese");
        product.setPrice(200);
        when(objectMapper.readValue(bufferedReader, Product.class)).thenReturn(product);
        when(productService.insert(product)).thenReturn(true);
        mapper = new RequestMapper(objectMapper, bufferedReader);
        productServlet.doPost(request,response);
        Assertions.assertTrue(productService.insert(product));
    }

    @Test
    @DisplayName("doPut test")
    void doPut() throws IOException {
        long id = 1;
        long updatedRows = 1;
        Product product = new Product();
        product.setTitle("Coconut Milk");

        Mockito.when(request.getPathInfo()).thenReturn("/1");
        when(objectMapper.readValue(bufferedReader, Product.class)).thenReturn(product);
        mapper = new RequestMapper(objectMapper, bufferedReader);
        when(mapper.mapJsonToProduct(request)).thenReturn(product);
        when(productService.update(id,product)).thenReturn(updatedRows);
        productServlet.doPut(request,response);
        Assertions.assertEquals(updatedRows,productService.update(id, product));
    }

    @Test
    @DisplayName("doDelete test")
    void doDelete(){
        long id = 1;
        long deletedRows = 1;
        when(request.getPathInfo()).thenReturn("/1");
        when(productService.deleteById(id)).thenReturn(deletedRows);
        productServlet.doDelete(request, response);
        Assertions.assertEquals(deletedRows, productService.deleteById(id));
    }
}
