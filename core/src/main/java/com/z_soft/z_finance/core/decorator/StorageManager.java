package com.z_soft.z_finance.core.decorator;

import com.z_soft.z_finance.core.exceptions.CurrencyException;
import com.z_soft.z_finance.core.interfaces.Storage;
import com.z_soft.z_finance.core.interfaces.dao.StorageDAO;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

public class StorageManager implements StorageDAO {

    private StorageDAO storageDAO;
    private List<Storage> storageList;

    public StorageManager(StorageDAO storageDAO) {
        this.storageDAO = storageDAO;
        init();
    }

    private void init() {
        storageList = storageDAO.getAll();
    }


    @Override
    public boolean addCurrency(Storage storage, Currency currency) throws CurrencyException {
        if (storageDAO.addCurrency(storage, currency)){
            storage.addCurrency(currency);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteCurrency(Storage storage, Currency currency) throws CurrencyException {
        if (storageDAO.deleteCurrency(storage, currency)){
            storage.deleteCurrency(currency);
            return true;
        }
        return false;
    }

    // TODO при обновлении происходит наоборот - сначала обновляется объект в коллекции, потом уже в БД - продумать, как сделать, чтобы можно было откатить изменения в случае ошибки при запросе к БД
    @Override
    public boolean updateAmount(Storage storage, Currency currency, BigDecimal amount) {

        return storageDAO.updateAmount(storage, currency, amount);

    }

    @Override
    public boolean update(Storage storage) {

        return storageDAO.update(storage);

    }

    @Override
    public boolean delete(Storage storage) {
        // TODO добавить нужные Exceptions
        if (storageDAO.delete(storage)){
            storageList.remove(storage);
            return true;
        }
        return false;
    }


    @Override
    public List<Storage> getAll() {
        if (storageList==null){
            storageList = storageDAO.getAll();
        }
        return storageList;
    }

    @Override
    public Storage get(long id) {
        return storageDAO.get(id);
    }
}
