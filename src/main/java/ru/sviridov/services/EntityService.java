package ru.sviridov.services;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface EntityService<T> {

    List<T> getAll() throws SQLException;

    T getById(long id) throws SQLException;

    boolean insert(T obj) throws SQLException, IOException;

    long update(long id, T obj) throws SQLException, IOException;

    long deleteById(long id) throws SQLException;

}
