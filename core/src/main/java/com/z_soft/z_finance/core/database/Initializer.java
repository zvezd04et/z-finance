package com.z_soft.z_finance.core.database;

import com.z_soft.z_finance.core.decorator.OperationManager;
import com.z_soft.z_finance.core.decorator.SourceManager;
import com.z_soft.z_finance.core.decorator.StorageManager;
import com.z_soft.z_finance.core.impls.dao.OperationDAOImpl;
import com.z_soft.z_finance.core.impls.dao.SourceDAOImpl;
import com.z_soft.z_finance.core.impls.dao.StorageDAOImpl;

public class Initializer {

    private static OperationManager operationManager;
    private static SourceManager sourceManager;
    private static StorageManager storageManager;


    public static OperationManager getOperationManager() {
        return operationManager;
    }


    public static SourceManager getSourceManager() {
        return sourceManager;
    }


    public static StorageManager getStorageManager() {
        return storageManager;
    }


    public static void load(String driverName, String url){

        SQLiteConnection.init(driverName, url);

        // последовательность создания объектов - важна (сначала справочные значения, потом операции)
        sourceManager = new SourceManager(new SourceDAOImpl());
        storageManager = new StorageManager(new StorageDAOImpl());
        operationManager = new OperationManager(new OperationDAOImpl(sourceManager.getIdentityMap(), storageManager.getIdentityMap()), sourceManager, storageManager);
    }
}
