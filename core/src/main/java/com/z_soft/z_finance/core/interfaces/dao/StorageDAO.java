package com.z_soft.z_finance.core.interfaces.dao;

import com.z_soft.z_finance.core.interfaces.Storage;

import java.math.BigDecimal;
import java.util.Currency;

public interface StorageDAO extends CommonDAO<Storage> {

    // boolean - чтобы удостовериться, что операция прошла успешно
    boolean addCurrency(Storage storage, Currency currency);
    boolean deleteCurrency(Storage storage, Currency currency);
    boolean updateAmount(Storage storage, BigDecimal amount);// сюда входит прибавить, отнять и обновить

}