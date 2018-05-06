package com.z_soft.z_finance.utils;

import android.app.Application;

public class AppContext extends Application{

    private static final String TAG = AppContext.class.getName();

    private static final String DB_NAME = "z_base.db";
    private static String dbFolder;
    private static String dbPath;

    @Override
    public void onCreate() {
        super.onCreate();
    }

}
