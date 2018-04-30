package com.z_soft.z_finance.core.interfaces.dao;

import java.util.List;

// описывает общие действия с БД для всех объектов
public interface CommonDAO<T> {

    List<T> getAll();
    T get(long id);
    boolean update(T storage);// boolean - чтобы удостовериться, что операция прошла успешно
    boolean delete(T storage);

}