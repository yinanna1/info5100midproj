package dao;

import model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // NEW JOIN map()
    private Student mapJoin(ResultSet rs) throws SQLException {
        return new Student(
                rs.getInt("studentId"),
                rs.getInt("userId"),
                rs.getString("userName")
        );
    }

    // OLD map() for the old methods (no userName)
    private Student map(ResultSet rs) throws SQLException {
        return new Student(
                rs.getInt("studentId"),
                rs.getInt("userId")
        );
    }

    // ============================================================
    // NEW JOIN VERSION (recommended for UI)
    // ============================================================
    public Student getStudentByUserId_Join(int userId) {
        String sql = """
            SELECT s.studentId, s.userId, u.userName
            FROM student s
            JOIN users u ON s.userId = u.userId
            WHERE s.userId = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapJoin(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ============================================================
    // 🔥 YOUR ORIGINAL METHOD (kept exactly as requested)
    // ============================================================
    public Student getStudentByUserId(int userId) {
        String sql = "SELECT * FROM student WHERE userId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return map(rs);      // old map() → no userName
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ============================================================
    // Get all students (JOIN so names show in UI)
    // ============================================================
    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();

        String sql = """
            SELECT s.studentId, s.userId, u.userName
            FROM student s
            JOIN users u ON s.userId = u.userId
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapJoin(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

}
