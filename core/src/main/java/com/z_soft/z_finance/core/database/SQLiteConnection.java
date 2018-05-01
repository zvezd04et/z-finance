package com.z_soft.z_finance.core.database;

import com.z_soft.z_finance.core.checking.Check;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLiteConnection {

    private static Connection con;

    public static Connection getConnection(){
        try {

            Class.forName("org.sqlite.JDBC").newInstance();// можно эту строчку удалить - драйвер автоматически будет найден

            // создание подключение к базе данных по пути, указанному в урле
            String url = "jdbc:sqlite:D:\\Work\\Projects\\Android\\debug\\z.db";

            if (con==null) con = DriverManager.getConnection(url);

            return con;

        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(SQLiteConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}
