package com.z_soft.z_finance.core.interfaces;

import com.z_soft.z_finance.core.objects.OperationType;

public interface Source {

    String getName();

    long getId();

    OperationType getOperationType();

}
