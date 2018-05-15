package com.z_soft.z_finance.utils;

import android.app.Application;

public class AppContext extends Application{

    private static final String TAG = AppContext.class.getName();

    private static final String DB_NAME = "z_base.db";
    private static String dbFolder;
    private static String dbPath;

    public final static String NODE_OBJECT = "com.z_soft.z_finance.activities.EditSourceActivity.NodeObject";

    public final static int REQUEST_NODE_EDIT = 101;
    public final static int REQUEST_NODE_ADD = 102;
    public final static int REQUEST_NODE_ADD_CHILD = 103;

    // в этом режиме при выборе значения, у которого нет дочерних элементов, будет выполнять возврат этого объекта в активити (нужно при создании или редактировании операции)
    public static final int SELECT_MODE = 100;
    // в этом режиме при выборе значения, у которого нет дочерних элементов, будет выполнять редактирование
    public static final int EDIT_MODE = 101;

    @Override
    public void onCreate() {
        super.onCreate();
        IconUtils.fillIcons(this); // один раз при загрузке приложения загружаем иконки
    }

}
