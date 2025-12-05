package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserModel {

    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/mydb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "yina0624";

    // =====================================================================
    // CREATE USER
    // =====================================================================
    public int createUser(String name, String email, String password, String role) {

        if (userNameExists(name)) {
            System.out.println("❌ Username already exists!");
            return -1;
        }

        String sql = "INSERT INTO user (userName, email, password, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, role);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int userId = rs.getInt(1);
                insertRoleRecord(userId, role);
                return userId;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }


    // Insert into role tables (student / instructor / admin)
    private void insertRoleRecord(int userId, String role) {
        String sql;

        switch (role.toLowerCase()) {
            case "student":
                sql = "INSERT INTO Student (userId) VALUES (?)";
                break;
            case "instructor":
                sql = "INSERT INTO Instructor (userId) VALUES (?)";
                break;
            case "admin":
                sql = "INSERT INTO admin (userId) VALUES (?)";
                break;
            default:
                System.out.println("⚠ Unknown role: " + role);
                return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("❌ Failed to insert into role table for role: " + role);
            e.printStackTrace();
        }
    }


    // =====================================================================
    // READ ALL USERS
    // =====================================================================
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("userId"),
                        rs.getString("userName"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }


    // =====================================================================
    // UPDATE USER
    // =====================================================================
    public boolean updateUser(int id, String email, String password) {

        String sql = "UPDATE user SET email=?, password=? WHERE userId=?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);
            stmt.setInt(3, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    // =====================================================================
    // DELETE USER
    // =====================================================================
    public boolean deleteUser(int id) {

        String sql = "DELETE FROM user WHERE userId=?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    // =====================================================================
    // CHECK UNIQUE USERNAME
    // =====================================================================
    public boolean userNameExists(String userName) {

        String sql = "SELECT COUNT(*) FROM user WHERE userName=?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userName);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}

