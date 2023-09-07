import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;
import ru.sviridov.entities.Card;
import ru.sviridov.entities.User;
import ru.sviridov.mappers.RequestMapper;
import ru.sviridov.services.CardService;
import ru.sviridov.services.ProductService;
import ru.sviridov.services.UserService;
import ru.sviridov.servlets.RestUserServlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;

public class UserServletTest {

    


    @Test
    @DisplayName("doGet test")
    void doGet() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        UserService userService = mock(UserService.class);
        PrintWriter writer = mock(PrintWriter.class);
        Mockito.when(response.getWriter()).thenReturn(writer);
        RestUserServlet restUserServlet = new RestUserServlet(userService,response);
        restUserServlet.doGet(request, response);
        long userId = 1;
        List<User> getAllUsers = List.of(
                new User(1, "Bill"),
                new User(2, "Jack"),
                new User(3, "Kevin"),
                new User(4, "Michael"),
                new User(5, "Ann"));
        User getByIdUser = new User(1, "Bill");
        Mockito.when(userService.getAll()).thenReturn(getAllUsers);

        Mockito.when(userService.getById(userId)).thenReturn(getByIdUser);
        List<User> expectedGetAll = userService.getAll();
        User expectedGetById = userService.getById(userId);

        Assertions.assertEquals(expectedGetAll, getAllUsers);
        Assertions.assertEquals(expectedGetById, getByIdUser);


    }
}
