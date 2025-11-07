package org.gamelog.repository;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private final Connection connection;
    private static DatabaseConnection instance;

    //dotenv file to load environmental variables
    private static final Dotenv dotenv = Dotenv.load();

    private static final String driverClassName = "org.postgresql.Driver";
    private static final String URL = dotenv.get("DB_URL");
    private static final String USERNAME = dotenv.get("DB_USERNAME");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    public DatabaseConnection() throws SQLException {
        try{
            Class.forName(driverClassName);
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

        }catch(ClassNotFoundException e){
            e.printStackTrace();
            throw new SQLException(e);
        } catch (SQLException e) {
            System.out.println("Failed to connect to PostgreSQL: " + e.getMessage());
            throw e;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
}
