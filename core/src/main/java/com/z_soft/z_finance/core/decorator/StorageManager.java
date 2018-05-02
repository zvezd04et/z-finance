package com.z_soft.z_finance.core.decorator;

import com.z_soft.z_finance.core.exceptions.AmountException;
import com.z_soft.z_finance.core.exceptions.CurrencyException;
import com.z_soft.z_finance.core.interfaces.Source;
import com.z_soft.z_finance.core.interfaces.Storage;
import com.z_soft.z_finance.core.interfaces.dao.StorageDAO;
import com.z_soft.z_finance.core.utils.ValueTree;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StorageManager implements StorageDAO {

    private ValueTree<Storage> treeUtils = new ValueTree<>();// построитель дерева

    // Все коллекции хранят ссылки на одни и те же объекты, но в разных "срезах"
    // при удалении - удалять нужно из всех коллекций
    private List<Storage> treeList = new ArrayList<>(); // хранит деревья объектов без разделения по типам операции
    private Map<Long, Storage> identityMap = new HashMap<>(); // нет деревьев, каждый объект хранится отдельно, нужно для быстрого доступа к любому объекту по id (чтобы каждый раз не использовать перебор по всей коллекции List и не обращаться к бд)

    private StorageDAO storageDAO;


    public StorageManager(StorageDAO storageDAO) {
        this.storageDAO = storageDAO;
        init();
    }

    private void init() {
        List<Storage> storageList = storageDAO.getAll();// запрос в БД происходит только один раз, чтобы заполнить коллекцию storageList

        for (Storage s : storageList) {
            identityMap.put(s.getId(), s);
            treeUtils.addToTree(s.getParentId(), s, treeList);
        }
    }

    @Override
    public List<Storage> getAll() {// возвращает объекты уже в виде деревьев
        return treeList;
    }

    @Override
    public Storage get(long id) {// не делаем запрос в БД, а получаем ранее загруженный объект из коллекции
        return identityMap.get(id);
    }

    @Override
    // TODO подумать как сделать - сначала обновлять в базе, а потом уже в коллекции (либо - если в базе не обновилось - откатить изменения в объекте коллекции)
    public boolean update(Storage storage) {

        return storageDAO.update(storage);

    }

    @Override
    public boolean delete(Storage storage) {
        // TODO добавить нужные Exceptions
        if (storageDAO.delete(storage)) {
            removeFromCollections(storage);

            return true;
        }
        return false;
    }

    // добавляет объект во все коллекции
    private void addToCollections(Storage storage) {
        identityMap.put(storage.getId(), storage);

        if (storage.hasParent()) {
            if (!storage.getParent().getChilds().contains(storage)) {// если ранее не был добавлен уже
                storage.getParent().add(storage);
            }
        } else {// если добавляем элемент, у которого нет родителей (корневой)
            treeList.add(storage);
        }
    }

    // удаляет объект из всех коллекций
    private void removeFromCollections(Storage storage) {
        identityMap.remove(storage.getId());
        if (storage.getParent() != null) {// если удаляем дочерний элемент
            storage.getParent().remove(storage);// т.к. у каждого дочернего элемента есть ссылка на родительский - можно быстро удалять элемент из дерева без поиска по всему дереву
        } else {// если удаляем элемент, у которого нет родителей
            treeList.remove(storage);
        }
    }

    @Override
    public boolean add(Storage storage) {

        if (storageDAO.add(storage)) {// если в БД добавилось нормально
            addToCollections(storage);
            return true;
        }else{// откатываем добавление
            // для отката можно использовать паттерн Command (для функции Undo)
        }

        return false;
    }

    @Override
    public boolean addCurrency(Storage storage, Currency currency, BigDecimal initAmount) throws CurrencyException {
        if (storageDAO.addCurrency(storage, currency, initAmount)) {// если в БД добавилось нормально
            storage.addCurrency(currency, initAmount);
            return true;
        }

        return false;
    }

    @Override
    public boolean deleteCurrency(Storage storage, Currency currency) throws CurrencyException {
        if (storageDAO.deleteCurrency(storage, currency)) {// если в БД удалилось нормально
            storage.deleteCurrency(currency);
            return true;
        }
        return false;
    }


    @Override
    public boolean updateAmount(Storage storage, Currency currency, BigDecimal amount) {

        if (storageDAO.updateAmount(storage, currency, amount)) {
            try {
                storage.updateAmount(amount, currency);
            } catch (CurrencyException e) {
                e.printStackTrace();
            } catch (AmountException e) {
                e.printStackTrace();
            }
            return true;
        }

        return false;

    }

    // если понадобится напрямую получить объекты из БД - можно использовать storageDAO
    public StorageDAO getStorageDAO() {
        return storageDAO;
    }

    public Map<Long, Storage> getIdentityMap() {
        return identityMap;
    }
}
