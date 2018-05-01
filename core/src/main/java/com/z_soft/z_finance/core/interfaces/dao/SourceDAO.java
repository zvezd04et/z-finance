package com.z_soft.z_finance.core.interfaces.dao;

import com.z_soft.z_finance.core.enums.OperationType;
import com.z_soft.z_finance.core.interfaces.Source;

import java.util.List;

public interface SourceDAO extends CommonDAO<Source> {

    List<Source> getList(OperationType operationType);
}
