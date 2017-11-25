package com.biskra.ansej.v3.kick;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by ACER User on 03/07/2017.
 */
public class ConnectionClass {
    private static String ipO = "";//;
    private static String classO = "net.sourceforge.jtds.jdbc.Driver";
    private static String dbO = "";
    private static String unO = "";//;
    private static String passwordO = "";//;

    @SuppressLint("NewApi")
    public static Connection CONN(String adr, String db, String un, String pw) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {
            Class.forName(classO).newInstance();
            ConnURL = "jdbc:jtds:sqlserver://" + adr + ";"
                    + "databaseName=" + db + ";user=" + un + ";password="
                    + pw + ";";
            conn = DriverManager.getConnection(ConnURL);
        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
        return conn;
    }


}