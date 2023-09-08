package ru.sviridov.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.sviridov.entities.Card;
import ru.sviridov.entities.Product;
import ru.sviridov.entities.User;
import ru.sviridov.services.UserService;

import java.io.IOException;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ServiceWithMockitoTest {

    @Mock
    private UserService userService;

    @Test
    @DisplayName("get all mocks")
    void userServiceGetAll() {
        User bill = new User();
        bill.setId(1);
        bill.setName("Bill");
        User michael = new User();
        michael.setId(2);
        michael.setName("Michael");
        Mockito.when(userService.getAll()).thenReturn(List.of(bill, michael));
        List<User> expected = userService.getAll();
        List<User> actual = List.of(bill, michael);
        Mockito.verify(userService).getAll();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("get mock by id")
    void getById() {
        long id = 1;
        User expectedUser = new User();
        expectedUser.setId(id);
        expectedUser.setName("Jack");
        expectedUser.setCards(List.of(
                new Card(1, "VTB", "321 123", 4),
                new Card(2, "SBER", "235 173", 1)));
        expectedUser.setProducts(List.of(
                new Product(1, "Milk", 80),
                new Product(2, "Cheese", 150)));
        Mockito.when(userService.getById(id)).thenReturn(expectedUser);
        User actual = userService.getById(id);
        Assertions.assertEquals(expectedUser, actual);
        Mockito.verify(userService).getById(id);
    }

    @Test
    @DisplayName("mock insert")
    void insert() throws IOException {
        User user = new User(10, "Camilla");
        Mockito.when(userService.insert(user)).thenReturn(true);
        Assertions.assertTrue(userService.insert(user));
        Mockito.verify(userService).insert(user);
    }

    @Test
    @DisplayName("mock update")
    void update() throws IOException {
        long id = 5;
        User user = new User(5, "Ann");
        long updatedRows = 1;
        Mockito.when(userService.update(id, user)).thenReturn(updatedRows);
        Assertions.assertEquals(updatedRows, userService.update(id, user));
        Mockito.verify(userService).update(id, user);
    }

    @Test
    @DisplayName("mock delete")
    void delete() {
        long id = 5;
        long deletedRows = 1;
        Mockito.when(userService.deleteById(id)).thenReturn(deletedRows);
        long deletedById = userService.deleteById(id);
        Assertions.assertEquals(deletedRows, deletedById);
        Mockito.verify(userService).deleteById(id);
    }
}