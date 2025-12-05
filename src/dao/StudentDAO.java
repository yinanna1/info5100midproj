package dao;

import model.Student;
import java.sql.*;
import java.util.*;

public class StudentDAO {

    // Map a row to Student, using real userName from user table
    private Student map(ResultSet rs) throws SQLException {
        return new Student(
                rs.getInt("studentId"),
                rs.getInt("userId"),
                rs.getString("userName")   // real username from joined user table
        );
    }

    // Get a single student by userId (used at login)
    public Student getStudentByUserId(int userId) {
        String sql = """
            SELECT s.studentId, s.userId, u.userName
            FROM student s
            JOIN user u ON s.userId = u.userId
            WHERE s.userId = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return map(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Get all students (for admin/main views)
    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();

        String sql = """
            SELECT s.studentId, s.userId, u.userName
            FROM student s
            LEFT JOIN user u ON s.userId = u.userId
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
