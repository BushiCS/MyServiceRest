import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.sviridov.entities.Card;
import ru.sviridov.entities.Product;
import ru.sviridov.entities.User;
import ru.sviridov.mappers.RequestMapper;
import ru.sviridov.services.CardService;
import ru.sviridov.services.ProductService;
import ru.sviridov.services.UserService;
import ru.sviridov.servlets.RestUserServlet;

import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

public class UserServletTest {
    HttpServletRequest request;
    HttpServletResponse response;
    UserService userService;
    CardService cardService;
    ProductService productService;
    PrintWriter writer;
    RestUserServlet userServlet;
    BufferedReader bufferedReader;
    ObjectMapper objectMapper;
    RequestMapper mapper;

    @BeforeEach
    void initMocks() throws IOException {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        userService = mock(UserService.class);
        cardService = mock(CardService.class);
        productService = mock(ProductService.class);
        writer = mock(PrintWriter.class);
        objectMapper = mock(ObjectMapper.class);
        Mockito.when(response.getWriter()).thenReturn(writer);
        Mockito.when(request.getPathInfo()).thenReturn("/");

        bufferedReader = Mockito.mock(BufferedReader.class);
        Mockito.when(request.getReader()).thenReturn(bufferedReader);

        userServlet = new RestUserServlet(userService, cardService, productService, request, response, objectMapper);
        mapper = new RequestMapper(objectMapper, bufferedReader);

    }

    @Test
    @DisplayName("doGet test")
    void doGet() {

        long userId = 2;
        long productId = 1;
        long cardId = 3;

        List<User> expectedUsers = List.of(
                new User(1, "Bill"),
                new User(2, "Jack"),
                new User(3, "Kevin"),
                new User(4, "Michael"),
                new User(5, "Ann"));
        User expectedUserById = new User(2, "Jack");

        List<Card> expectedUserCards = List.of(
                new Card(3, "TINKOFF", "542 243", 2));
        Card expectedUserCardById = new Card(3, "TINKOFF", "542 243", 2);

        List<Product> expectedUserProducts = List.of(
                new Product(1, "Milk", 80),
                new Product(3, "Bread", 60));
        Product expectedUserProductById = new Product(1, "Milk", 80);

        Mockito.when(userService.getAll()).thenReturn(expectedUsers);
        Mockito.when(userService.getById(userId)).thenReturn(expectedUserById);
        Mockito.when(cardService.getUserCards(userId)).thenReturn(expectedUserCards);
        Mockito.when(cardService.getUserCardByCardId(cardId, userId)).thenReturn(expectedUserCardById);
        Mockito.when(productService.getUserProducts(userId)).thenReturn(expectedUserProducts);
        Mockito.when(productService.getUserProductByProductId(productId, userId)).thenReturn(expectedUserProductById);
        userServlet.doGet(request, response);
        Assertions.assertEquals(expectedUsers, userService.getAll());
        Assertions.assertEquals(expectedUserById, userService.getById(userId));
        Assertions.assertEquals(expectedUserCards, cardService.getUserCards(userId));
        Assertions.assertEquals(expectedUserCardById, cardService.getUserCardByCardId(cardId, userId));
        Assertions.assertEquals(expectedUserProducts, productService.getUserProducts(userId));
        Assertions.assertEquals(expectedUserProductById, productService.getUserProductByProductId(productId, userId));
    }

    @Test
    @DisplayName("doPost test")
    void doPost() throws IOException {
        boolean expectedInserted = true;
        User user = new User(10, "Alex");
        Product product = new Product();
        product.setTitle("Cheese");
        product.setPrice(200);
        int expected = 1;
        when(objectMapper.readValue(bufferedReader, User.class)).thenReturn(user);
        when(userService.insert(user)).thenReturn(expectedInserted);
        mapper = new RequestMapper(objectMapper, bufferedReader);
        when(request.getPathInfo()).thenReturn("/purchases/");
        String[] strForCut = new String[]{"[{\"name\":\"Bill\"},{\"title\":\"Cheese\"}]"};
        String[] params = new String[]{"{\"name\":\"Bill\"}", "{\"title\":\"Cheese\"}"};
        when(bufferedReader.lines()).thenReturn(Stream.of(strForCut));
        when(objectMapper.readValue("{\"name\":\"Bill\"}", User.class)).thenReturn(user);
        when(objectMapper.readValue("{\"title\":\"Cheese\"}", Product.class)).thenReturn(product);


        when(mapper.mapRequestForProductAndUser(request)).thenReturn(params);
        when(userService.addProductToUser(params)).thenReturn(expected);
        userServlet.doPost(request, response);
        Assertions.assertTrue(userService.insert(user));
        Assertions.assertEquals(expected, userService.addProductToUser(params));
    }

    @Test
    @DisplayName("doPut test")
    void doPut() throws IOException {
        long id = 1;
        long updatedRows = 1;
        User user = new User(id, "Antonio");
        Mockito.when(request.getPathInfo()).thenReturn("/1");
        when(objectMapper.readValue(bufferedReader, User.class)).thenReturn(user);
        mapper = new RequestMapper(objectMapper, bufferedReader);
        when(mapper.mapJsonToUser(request)).thenReturn(user);
        when(userService.update(id, user)).thenReturn(updatedRows);
        userServlet.doPut(request, response);
        Assertions.assertEquals(updatedRows,userService.update(id, user));
    }

    @Test
    @DisplayName("doDelete test")
    void doDelete(){
        long id = 1;
        long deletedRows = 1;
        when(request.getPathInfo()).thenReturn("/1");
        when(userService.deleteById(id)).thenReturn(deletedRows);
        userServlet.doDelete(request, response);
        Assertions.assertEquals(deletedRows, userService.deleteById(id));
    }
}
