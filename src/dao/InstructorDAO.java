package dao;

import model.Instructor;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstructorDAO {

    // ============================================================
    // GET ALL INSTRUCTORS
    // ============================================================
    public List<Instructor> getAllInstructors() {
        List<Instructor> list = new ArrayList<>();

        String sql = "SELECT i.instructorId, i.userId, u.userName " +
                "FROM Instructor i " +
                "LEFT JOIN user u ON i.userId = u.userId";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Instructor inst = new Instructor(
                        rs.getInt("instructorId"),
                        rs.getInt("userId"),
                        rs.getString("userName")
                );
                list.add(inst);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ============================================================
    // GET A SINGLE INSTRUCTOR BY ID
    // ============================================================
    public Instructor getInstructorById(int instructorId) {
        String sql = "SELECT i.instructorId, i.userId, u.userName " +
                "FROM Instructor i " +
                "LEFT JOIN user u ON i.userId = u.userId " +
                "WHERE i.instructorId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, instructorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Instructor(
                            rs.getInt("instructorId"),
                            rs.getInt("userId"),
                            rs.getString("userName")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Instructor getInstructorByUserId(int userId) {
        String sql = "SELECT * FROM instructor WHERE userId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Instructor(
                        rs.getInt("instructorId"),
                        rs.getInt("userId"),
                        rs.getString("userName")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    // ============================================================
    // VALIDATE ADMIN LOGIN
    // (Used by MainController)
    // ============================================================
    public User validateAdmin(String username, String password) {

        String sql =
                "SELECT * FROM user " +
                        "WHERE userName = ? AND password = ? AND role = 'admin'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Return as User since admin *is from user table*
                return new User(
                        rs.getInt("userId"),
                        rs.getString("userName"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // invalid login
    }

    public Instructor getInstructor(int instructorId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM instructor WHERE instructor_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, instructorId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Instructor(
                        rs.getInt("instructor_id"),
                        rs.getInt("user_id"),
                        rs.getString("name")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
