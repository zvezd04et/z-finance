package com.z_soft.z_finance.core.impls.dao;

import com.z_soft.z_finance.core.database.SQLiteConnection;
import com.z_soft.z_finance.core.enums.OperationType;
import com.z_soft.z_finance.core.impls.DefaultSource;
import com.z_soft.z_finance.core.interfaces.Source;
import com.z_soft.z_finance.core.interfaces.dao.SourceDAO;
import com.z_soft.z_finance.core.utils.TreeConstructor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

//TODO можно реализовать общий абстрактный класс и вынести туда общие методы (getAll, delete и пр.
public class SourceDAOImpl implements SourceDAO {

    private static final String SOURCE_TABLE = "source";

    private List<Source> sourceList = new ArrayList<>();
    private Map<OperationType, List<Source>> sourceMap = new EnumMap<>(OperationType.class);// для каждого ключа (типа операции) - своя коллекция источников (корневых элементов дерева)

    private TreeConstructor<Source> treeConstructor = new TreeConstructor<>();// для каждого объекта создаем свой экземпляр TreeConstructor - т.к. передается тип Generics


    @Override
    public List<Source> getAll() {
        sourceList.clear();

        try (Statement stmt = SQLiteConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("select * from " + SOURCE_TABLE)) {

            while (rs.next()) {
                DefaultSource source = new DefaultSource();
                source.setId(rs.getLong("id"));
                source.setName(rs.getString("name"));

                Integer operationTypeId = rs.getInt("operation_type_id"); // можно использовать тип Integer
                source.setOperationType(OperationType.getType(operationTypeId));// operationType устанавливаем только для корневых элементов, т.к. для дочерних автоматически устанавливается тип от родителя

                Long parentId = rs.getLong("parent_id");// тип Long, чтобы можно было проверять на null
                treeConstructor.addToTree(parentId, source, sourceList);

            }

            fillSourceMap();

            return sourceList;// должен содержать только корневые элементы

        } catch (SQLException e) {
            Logger.getLogger(SourceDAOImpl.class.getName()).log(Level.SEVERE, null, e);
        }

        return null;
    }

    private void fillSourceMap() {
        // в sourceMap и sourceList находятся одни и те же объекты!!

        for (OperationType type: OperationType.values()) {
            // используем lambda выражение для фильтрации
            sourceMap.put(type, sourceList.stream().filter(s -> s.getOperationType() == type).collect(Collectors.toList()));
        }

    }

    @Override
    public Source get(long id) {
        return null;
    }

    @Override
    public boolean update(Source source) {
        // для упрощения - у хранилища даем изменить только название, изменять parent_id нельзя (для этого можно удалить и заново создать)
        try (PreparedStatement stmt = SQLiteConnection.getConnection().prepareStatement("update " + SOURCE_TABLE + " set name=? where id=?")) {

            stmt.setString(1, source.getName());// у созданного элемента - разрешаем менять только название
            stmt.setLong(2, source.getId());

            // не даем обновлять operationType - тип устанавливается только один раз при создании корневеого элемента

            if (stmt.executeUpdate() == 1) {
                return true;
            }

        } catch (SQLException e) {
            Logger.getLogger(SourceDAOImpl.class.getName()).log(Level.SEVERE, null, e);
        }

        return false;
    }

    @Override
    public boolean delete(Source source) {
        // TODO реализовать - если есть ли операции по данному хранилищу - запрещать удаление
        try (PreparedStatement stmt = SQLiteConnection.getConnection().prepareStatement("delete from " + SOURCE_TABLE + " where id=?")) {

            stmt.setLong(1, source.getId());

            if (stmt.executeUpdate() == 1) {
                return true;
            }

        } catch (SQLException e) {
            Logger.getLogger(SourceDAOImpl.class.getName()).log(Level.SEVERE, null, e);
        }

        return false;
    }

    @Override
    public List<Source> getList(OperationType operationType) {
        return sourceMap.get(operationType);
    }
}
