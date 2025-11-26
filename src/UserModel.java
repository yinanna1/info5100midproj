import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserModel {
    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/mydb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "yina0624";

    // CREATE
    public int insertUser(User user) {
        String sql = "INSERT INTO user (userName, email, password, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRole());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);   // <-- RETURN INTEGER ID ONLY
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // failure
    }
    public String insertRoleTable(int userId, String role) {
        String sql = "";

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
                return null;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return String.valueOf(rs.getInt(1));   // return role ID
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // READ
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

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

    // UPDATE
    public boolean updateUser(int id, String newEmail, String newPassword) {
        String sql = "UPDATE user SET email = ?, password = ? WHERE userId = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newEmail);
            stmt.setString(2, newPassword);
            stmt.setInt(3, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // DELETE
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM user WHERE userId = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    //smth
    // --- INSERT STUDENT RECORD WHEN ROLE = student ---
    private Integer insertStudentRecord(int userId) {
        String sql = "INSERT INTO Student (userId) VALUES (?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);  // studentId
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // --- INSERT INSTRUCTOR RECORD ---
    private Integer insertInstructorRecord(int userId) {
        String sql = "INSERT INTO Instructor (userId) VALUES (?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // --- INSERT ADMIN RECORD ---
    private Integer insertAdminRecord(int userId) {
        String sql = "INSERT INTO admin (userId) VALUES (?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean userNameExists(String name) {
        String sql = "SELECT COUNT(*) FROM user WHERE userName = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;  // true if name already used
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

}
