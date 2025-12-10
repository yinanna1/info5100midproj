package dao;

import model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SectionStudentDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    /**
     * Returns all students enrolled in a section,
     * including userName + email from user table.
     *
     * Assumes:
     * - SectionStudent(sectionId, studentId)
     * - Student(studentId, userId)
     * - user(userId, userName, email)
     */
    public List<Student> getStudentsBySection(int sectionId) {
        List<Student> list = new ArrayList<>();

        String sql = """
            SELECT s.studentId, s.userId, u.userName, u.email
            FROM SectionStudent ss
            JOIN Student s ON ss.studentId = s.studentId
            JOIN user u ON s.userId = u.userId
            WHERE ss.sectionId = ?
            ORDER BY u.userName
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Student st = new Student();
                    st.setStudentId(rs.getInt("studentId"));
                    st.setUserId(rs.getInt("userId"));
                    st.setUserName(rs.getString("userName"));
                    st.setEmail(rs.getString("email"));
                    list.add(st);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to get students by section", e);
        }

        return list;
    }

    /**
     * Add student enrollment to section.
     * Returns true if insert succeeded.
     */
    public boolean addStudentToSection(int sectionId, int studentId) {
        String sql = "INSERT INTO SectionStudent (sectionId, studentId) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            ps.setInt(2, studentId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            // Optional: if you want duplicate enrollments to return false instead of crashing
            // You can check SQLState or error code here.
            throw new RuntimeException("Failed to add student to section", e);
        }
    }

    /**
     * Controller expects this name.
     */
    public boolean dropStudentFromSection(int sectionId, int studentId) {
        String sql = "DELETE FROM SectionStudent WHERE sectionId = ? AND studentId = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            ps.setInt(2, studentId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to drop student from section", e);
        }
    }

    /**
     * Keep this alias if other parts of your code use it.
     */
    public boolean removeStudentFromSection(int sectionId, int studentId) {
        return dropStudentFromSection(sectionId, studentId);
    }
}
