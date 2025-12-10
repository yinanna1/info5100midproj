package dao;

import model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class StudentDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    /**
     * Map expects these columns to exist in ResultSet:
     * - studentId
     * - userId
     * - userName
     * - email
     */
    private Student map(ResultSet rs) throws SQLException {
        Student st = new Student();
        st.setStudentId(rs.getInt("studentId"));
        st.setUserId(rs.getInt("userId"));

        // from user table
        try {
            st.setUserName(rs.getString("userName"));
        } catch (SQLException ignored) {
            // if not selected, leave null
        }

        try {
            st.setEmail(rs.getString("email"));
        } catch (SQLException ignored) {
            // if not selected, leave null
        }

        return st;
    }

    /**
     * Get Student using userId.
     * Assumes:
     * Student(studentId, userId)
     * user(userId, userName, email, ...)
     */
    public Student getStudentByUserId(int userId) {
        String sql = """
            SELECT s.studentId,
                   s.userId,
                   u.userName AS userName,
                   u.email AS email
            FROM Student s
            JOIN user u ON s.userId = u.userId
            WHERE s.userId = ?
            LIMIT 1
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to get student by userId", e);
        }

        return null;
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();

        String sql = """
        SELECT s.studentId,
               s.userId,
               u.userName AS userName,
               u.email AS email
        FROM Student s
        JOIN user u ON s.userId = u.userId
        ORDER BY u.userName
    """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Student st = new Student();
                st.setStudentId(rs.getInt("studentId"));
                st.setUserId(rs.getInt("userId"));
                st.setUserName(rs.getString("userName"));
                st.setEmail(rs.getString("email"));
                list.add(st);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all students", e);
        }

        return list;
    }

}
