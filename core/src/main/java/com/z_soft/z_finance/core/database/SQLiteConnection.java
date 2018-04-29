package com.z_soft.z_finance.core.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLiteConnection {

    private static Connection con;

    public static Connection getConnection(String url){
        try {

            Class.forName("org.sqlite.JDBC").newInstance();// можно эту строчку удалить - драйвер автоматически будет найден

            if (con==null) con = DriverManager.getConnection(url);

            return con;

        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(SQLiteConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}
