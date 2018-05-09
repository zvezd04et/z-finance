package com.z_soft.z_finance.core.interfaces;

import com.z_soft.z_finance.core.enums.OperationType;

public interface Source extends TreeNode{

    OperationType getOperationType();

    void setOperationType(OperationType type);

}
