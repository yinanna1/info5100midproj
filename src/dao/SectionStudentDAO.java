package dao;

import model.Student;
import model.Section;

import java.sql.*;
import java.util.*;

public class SectionStudentDAO {

    // ===============================================================
    // MAPPERS
    // ===============================================================

    // Match StudentDAO: studentId, userId, and a placeholder name
    private Student mapStudent(ResultSet rs) throws SQLException {
        return new Student(
                rs.getInt("studentId"),
                rs.getInt("userId"),
                "Student " + rs.getInt("studentId")  // same pattern as StudentDAO
        );
    }

    // Section mapper matches your SectionDAO.map(...)
    private Section mapSection(ResultSet rs) throws SQLException {
        return new Section(
                rs.getInt("sectionId"),
                rs.getString("sectionName"),
                rs.getInt("lessonId"),
                rs.getInt("instructorId"),
                rs.getInt("room")
        );
    }

    // ===============================================================
    // ENROLL / DROP
    // ===============================================================

    public boolean addStudentToSection(int studentId, int sectionId) {

        String sql = "INSERT INTO sectionstudent (sectionId, studentId) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            ps.setInt(2, studentId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean dropStudentFromSection(int studentId, int sectionId) {

        String sql = "DELETE FROM sectionstudent WHERE studentId = ? AND sectionId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===============================================================
    // QUERIES USED BY CONTROLLERS / UI
    // ===============================================================

    /**
     * Get all students in a given section.
     * Used by MainController, SectionDetailsController, InstructorDashboardUI, etc.
     */
    public List<Student> getStudentsBySection(int sectionId) {
        List<Student> list = new ArrayList<>();

        String sql = """
            SELECT st.*
            FROM student st
            JOIN sectionstudent ss ON st.studentId = ss.studentId
            WHERE ss.sectionId = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapStudent(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * (Optional, but useful) Get all sections a given student is enrolled in.
     * This matches the method name that was originally used: getSectionsByStudent(int)
     * in some controllers.
     */
    public List<Section> getSectionsByStudent(int studentId) {
        List<Section> list = new ArrayList<>();

        String sql = """
            SELECT s.*
            FROM section s
            JOIN sectionstudent ss ON s.sectionId = ss.sectionId
            WHERE ss.studentId = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapSection(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
