package com.z_soft.z_finance.core.interfaces;

import com.z_soft.z_finance.core.enums.OperationType;

import java.util.Calendar;

public interface Operation extends IconNode, Comparable<Operation> {

    long getId();

    void setId(long id);

    OperationType getOperationType();

    Calendar getDateTime();

    String getDescription();

}
