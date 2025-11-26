package dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/mydb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root"; // ⚠️ change if needed
    private static final String PASSWORD = "yina0624"; // ⚠️ put your real MySQL password here

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL JDBC Driver
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connected to MySQL database successfully!");
            return conn;
        } catch (Exception e) {
            System.err.println("❌ Database connection failed!");
            e.printStackTrace();
            return null;
        }
    }

    // Quick test
    public static void main(String[] args) {
        Connection conn = getConnection();
        if (conn != null) {
            System.out.println("Connection test successful!");
        } else {
            System.out.println("Connection test failed.");
        }
    }
}
