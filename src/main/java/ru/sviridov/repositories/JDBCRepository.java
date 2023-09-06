package ru.sviridov.repositories;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;

public interface JDBCRepository<T>  {

    List<T> getAll() throws SQLException;

    T getById(long id) throws SQLException;

    long update(long id, T obj) throws SQLException;

    boolean insert(T obj) throws SQLException;

    long delete(long id) throws SQLException;

}
