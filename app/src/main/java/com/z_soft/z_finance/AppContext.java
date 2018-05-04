package com.z_soft.z_finance;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class AppContext extends Application{

    private static final String TAG = AppContext.class.getName();

    private static final String DB_NAME = "z_base.db";
    private static String dbFolder;
    private static String dbPath;

    @Override
    public void onCreate() {
        super.onCreate();
        checkDbExist(this);
    }

    // если нет файла БД - скопировать его из папки assets
    private static void checkDbExist(Context context) {

        // определить папку с данными приложения
        dbFolder = context.getApplicationInfo().dataDir + "/" + "databases/";

        dbPath = dbFolder + DB_NAME;

        if (!checkDataBaseExists()) {
            copyDataBase(context);
        }
    }

    private static void copyDataBase(Context context) {


        // создаем папку databases
        File databaseFolder = new File(dbFolder);
        databaseFolder.mkdir();

        try (InputStream sourceFile = context.getAssets().open(DB_NAME); // что копируем
             OutputStream destinationFolder = new FileOutputStream(dbPath) // куда копируем dbPath
        ){

            // копируем по байтам весь файл стандартным способом Java I/O
            byte[] buffer = new byte[1024];
            int length;
            while ((length = sourceFile.read(buffer)) > 0) {
                destinationFolder.write(buffer, 0, length);
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }

    private static boolean checkDataBaseExists() {
        File dbFile = new File(dbPath);
        return dbFile.exists();
    }
}
