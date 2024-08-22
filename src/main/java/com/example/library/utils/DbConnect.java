package com.example.library.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbConnect { // singleton pattern
    private static DbConnect instance;
    private final static String JDBC_URL = "jdbc:mysql://127.0.0.1:3306/library";
    private final static String USERNAME = "root";
    private final static String PASSWORD = "duy732003";

    private Connection connection;

    private DbConnect() {
        System.out.println("Create connection....");
        try {
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DbConnect getInstance() { // khoi tao doi tuong singleton
        if (instance == null) {
            instance = new DbConnect();
        }
        return instance;
    }

    public ResultSet executeQuery(String sql) { // nhung cau query co tra ve gia tri select
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            return resultSet;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void executeUpdate(String sql) { // nhung cau query khong tra ve gia tri // update, insert, delete
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}