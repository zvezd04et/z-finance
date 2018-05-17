package com.z_soft.z_finance.utils;

import android.content.Context;

import com.z_soft.z_finance.core.enums.OperationType;

public class OperationTypeUtils {

    public static LocalizedOperationType incomeType;
    public static LocalizedOperationType outcomeType;
    public static LocalizedOperationType transferType;
    public static LocalizedOperationType convertType;

    
    public static void init(Context context){
        incomeType = new LocalizedOperationType(OperationType.INCOME, context);
        outcomeType = new LocalizedOperationType(OperationType.OUTCOME, context);
        transferType = new LocalizedOperationType(OperationType.TRANSFER, context);
        convertType = new LocalizedOperationType(OperationType.CONVERT, context);
        
    }




}
