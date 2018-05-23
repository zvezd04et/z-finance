package com.z_soft.z_finance.core.impls.dao;

import com.z_soft.z_finance.core.database.SQLiteConnection;
import com.z_soft.z_finance.core.exceptions.CurrencyException;
import com.z_soft.z_finance.core.impls.DefaultStorage;
import com.z_soft.z_finance.core.interfaces.Storage;
import com.z_soft.z_finance.core.interfaces.dao.StorageDAO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class StorageDAOImpl<T extends Storage> implements StorageDAO {

    private static final String CURRENCY_AMOUNT_TABLE = "currency_amount";
    private static final String STORAGE_TABLE = "storage";

    private List<Storage> storageList = new ArrayList<>();

    @Override
    public List<Storage> getAll() {
        storageList.clear();

        try {

            try (Statement stmt = SQLiteConnection.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("select * from " + STORAGE_TABLE + " order by parent_id")) {

                while (rs.next()) {
                    DefaultStorage storage = new DefaultStorage();
                    storage.setId(rs.getLong("id"));
                    storage.setName(rs.getString("name"));
                    storage.setIconName(rs.getString("icon_name"));
                    storage.setParentId(rs.getLong("parent_id"));
                    storage.setRefCount(rs.getInt("ref_count"));

                    storageList.add(storage);
                }

            }

            // для каждого хранилища загрузить доступны валюты и баланс
            for (Storage storage : storageList) {

                try (PreparedStatement stmt = SQLiteConnection.getConnection().prepareStatement("select * from " + CURRENCY_AMOUNT_TABLE + " where storage_id =?")) {

                    stmt.setLong(1, storage.getId());
                    try(ResultSet rs = stmt.executeQuery()){
                        while (rs.next()) {
                            storage.addCurrency(Currency.getInstance(rs.getString("currency_code")), rs.getBigDecimal("amount"));
                        }
                    }
                }

            }

            return storageList;

        } catch (SQLException e) {
            Logger.getLogger(StorageDAOImpl.class.getName()).log(Level.SEVERE, null, e);
        } catch (CurrencyException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean addCurrency(Storage storage, Currency currency, BigDecimal initAmount) {

        // для автоматического закрытия ресурсов
        try (PreparedStatement stmt = SQLiteConnection.getConnection().prepareStatement("insert into " + CURRENCY_AMOUNT_TABLE + "(currency_code, storage_id, amount) values(?,?,?)")) {

            stmt.setString(1, currency.getCurrencyCode());
            stmt.setLong(2, storage.getId());

            if (initAmount == null) {
                stmt.setDouble(3, 0);
            }else{
                stmt.setDouble(3, initAmount.doubleValue());
            }

            if (stmt.executeUpdate() == 1) { // если была добавлена 1 запись
                return true;
            }

        } catch (SQLException e) {
            Logger.getLogger(StorageDAOImpl.class.getName()).log(Level.SEVERE, null, e);
        }

        return false;
        // TODO реализовать: вместо true, false - выбрасывать исключение и перехватывать его выше, создать соотв. типы Exception
    }

    @Override
    public boolean deleteCurrency(Storage storage, Currency currency) {

        if (!operationExist(storage, currency)) {
            return false;
        }

        try (PreparedStatement stmt = SQLiteConnection.getConnection().prepareStatement("delete from " + CURRENCY_AMOUNT_TABLE + " where storage_id=? and currency_code=?")) {

            stmt.setLong(1, storage.getId());
            stmt.setString(2, currency.getCurrencyCode());


            if (stmt.executeUpdate() == 1) {  // если была обновлена 1 запись
                return true;
            }

        } catch (SQLException e) {
            Logger.getLogger(StorageDAOImpl.class.getName()).log(Level.SEVERE, null, e);
        }

        return false;
    }

    private boolean operationExist(Storage storage, Currency currency) {
        // TODO реализовать проверку, есть ли операции по этому хранилищу и валюте
        return false;
    }
    @Override
    public boolean updateAmount(Storage storage, Currency currency, BigDecimal amount) {
        try (PreparedStatement stmt = SQLiteConnection.getConnection().prepareStatement("update " + CURRENCY_AMOUNT_TABLE + " set amount=? where storage_id=? and currency_code=?")) {

            stmt.setDouble(1, amount.doubleValue());
            stmt.setLong(2, storage.getId());
            stmt.setString(3, currency.getCurrencyCode());

            if (stmt.executeUpdate() == 1) {
                return true;
            }

        } catch (SQLException e) {
            Logger.getLogger(StorageDAOImpl.class.getName()).log(Level.SEVERE, null, e);
        }

        return false;
    }

    @Override
    public Storage get(long id) {

        try (PreparedStatement stmt = SQLiteConnection.getConnection().prepareStatement("select * from " + STORAGE_TABLE + " where id=?")) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()){
                DefaultStorage storage = null;

                if (rs.next()){
                    storage = new DefaultStorage();
                    storage.setId(rs.getLong("id"));
                    storage.setName(rs.getString("name"));
                    storage.setIconName(rs.getString("icon_name"));
                    storage.setParentId(rs.getLong("parent_id"));
                    storage.setRefCount(rs.getInt("ref_count"));
                }

                return storage;
            }

        } catch (SQLException e) {
            Logger.getLogger(SourceDAOImpl.class.getName()).log(Level.SEVERE, null, e);
        }

        return null;
    }


    @Override
    public boolean update(Storage storage) {
        // для упрощения - у хранилища даем изменить только название, изменять parent_id нельзя (для этого можно удалить и заново создать)
        try (PreparedStatement stmt = SQLiteConnection.getConnection().prepareStatement("update " + STORAGE_TABLE + " set name=?, icon_name=? where id=?")) {

            stmt.setString(1, storage.getName());
            stmt.setString(2, storage.getIconName());
            stmt.setLong(3, storage.getId());

            if (stmt.executeUpdate() == 1) {

                // также требуется обновить данные по всем валютам данного хранилища (счета)
                Map<Currency, BigDecimal> map = storage.getCurrencyAmounts();

                for (Map.Entry<Currency, BigDecimal> entry : map.entrySet()) {
                    Currency c = entry.getKey();
                    BigDecimal amount = entry.getValue();

                    addCurrency(storage, c, amount); // в таблице БД - действует уникальный индекс по storage_id и currency_code - если такие данные уже есть - выполняется обновление (replace)
                }

                return true;
            }

        } catch (SQLException e) {
            Logger.getLogger(StorageDAOImpl.class.getName()).log(Level.SEVERE, null, e);
        }

        return false;
    }

    @Override
    public boolean delete(Storage storage) {

        // сначала удаляем все валюты и балансы (т.к. у них внешние ключи на storage)
        // если сразу удалять storage - выйдет ошибка foreign key constraint
        List<Currency> list = storage.getAvailableCurrencies();

        for (Currency c : list) {
            System.out.println(deleteCurrency(storage, c));
        }

        try (PreparedStatement stmt = SQLiteConnection.getConnection().prepareStatement("delete from " + STORAGE_TABLE + " where id=?")) {

            stmt.setLong(1, storage.getId());

            if (stmt.executeUpdate() == 1) {
                return true;
            }

        } catch (SQLException e) {
            Logger.getLogger(StorageDAOImpl.class.getName()).log(Level.SEVERE, null, e);
        }

        return false;
    }

    public int getRefCount(Storage storage){
        try (PreparedStatement stmt = SQLiteConnection.getConnection().prepareStatement("select ref_count from " + STORAGE_TABLE + " where id=?");) {

            stmt.setLong(1, storage.getId());

            try (ResultSet rs = stmt.executeQuery();) {

                if (rs.next()) {
                    return rs.getInt("ref_count");
                }

            }

        } catch (SQLException e) {
            Logger.getLogger(SourceDAOImpl.class.getName()).log(Level.SEVERE, null, e);
        }

        return -1;
    }

    @Override
    public boolean add(Storage storage) {

        // для хранилища нужно вставлять данные в разные таблицы, поэтому выполняем в одной транзакции
        Connection con = SQLiteConnection.getConnection();

        try {
            con.setAutoCommit(false);// включаем режим ручного подтверждения транзакции (commit)


            // само добавляем само хранилище, т.к. в таблице валют работает foreign key
            try (PreparedStatement stmt = con.prepareStatement("insert into " + STORAGE_TABLE + "(name, parent_id, icon_name) values(?,?,?)");
                 Statement stmtId = SQLiteConnection.getConnection().createStatement() ) {// возвращать id вставленной записи

                stmt.setString(1, storage.getName());

                if (storage.hasParent()) {
                    stmt.setLong(2, storage.getParent().getId());
                } else {
                    stmt.setNull(2, Types.BIGINT);
                }


                stmt.setString(3, storage.getIconName());

                if (stmt.executeUpdate() == 1) {// если объект добавился нормально
                    try (ResultSet rs = stmtId.executeQuery("SELECT last_insert_rowid()")) {// получаем id вставленной записи


                        if (rs.next()) {
                            storage.setId(rs.getLong(1));// не забываем просвоить новый id в объект, иначе дальше не добавятся валюты из-за foreign key
                        }

                        List<Currency> list = storage.getAvailableCurrencies();
                        // вставляем все валюты с суммами
                        for (Currency c : list) {
                            if (!addCurrency(storage, c, storage.getAmount(c))) {// если любой из запросов (добавление валюты) не выполнится - откатываем всю транзакцию
                                con.rollback();
                                return false;
                            }

                        }

                        con.commit();// если все выполнилось без ошибок - подтверждаем транзакцию
                        return true;


                    } catch (SQLException e) {
                        e.printStackTrace();
                        con.rollback();// если код дошел до этого места - значит что-то пошлое не так, поэтому откатываем транзакцию
                    }
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (CurrencyException e) {
            e.printStackTrace();
        } finally {
            try {
                con.setAutoCommit(true);// возвращаем настройку обратно
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}
