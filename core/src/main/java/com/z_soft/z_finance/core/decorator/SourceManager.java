package com.z_soft.z_finance.core.decorator;

import com.z_soft.z_finance.core.enums.OperationType;
import com.z_soft.z_finance.core.interfaces.Source;
import com.z_soft.z_finance.core.interfaces.dao.SourceDAO;

import java.util.List;

public class SourceManager implements SourceDAO {

    private SourceDAO sourceDAO;
    private List<Source> sourceList;

    public SourceManager(SourceDAO sourceDAO) {
        this.sourceDAO = sourceDAO;
    }

    @Override
    public List<Source> getAll() {
        if (sourceList==null){
            sourceList = sourceDAO.getAll();
        }
        return sourceList;
    }

    @Override
    public Source get(long id) {
        return null;
    }

    @Override
    public boolean update(Source source) {

        return sourceDAO.update(source);

    }

    @Override
    public boolean delete(Source source) {
        // TODO добавить нужные Exceptions
        if (sourceDAO.delete(source)){
            sourceList.remove(source);
            sourceDAO.getList(source.getOperationType()).remove(source);
            return true;
        }
        return false;
    }

    @Override
    public List<Source> getList(OperationType operationType) {
        return sourceDAO.getList(operationType);
    }
}
