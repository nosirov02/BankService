package uz.isystem.BankService.service;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class JdbcConnection {
    private String URL = "jdbc:postgresql://localhost:5432/isystem_db";
    private String username = "postgres";
    private String password = "root";

    Connection connection;


    public JdbcConnection() {
        try {
            connection = DriverManager.getConnection(URL, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection(){
        return connection;
    }
    public Statement getStatement(){
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
