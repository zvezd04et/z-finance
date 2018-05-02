package com.z_soft.z_finance.core.interfaces.dao;

import com.z_soft.z_finance.core.enums.OperationType;
import com.z_soft.z_finance.core.interfaces.Operation;

import java.util.List;

public interface OperationDAO extends CommonDAO<Operation> {

    List<Operation> getList(OperationType operationType);// получить список операций определенного типа

}
