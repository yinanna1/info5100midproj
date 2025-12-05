package dao;

import model.Section;
import java.sql.*;
import java.util.*;

public class SectionDAO {

    // Helper method to map a ResultSet row to a Section object
    private Section map(ResultSet rs) throws SQLException {
        return new Section(
                rs.getInt("sectionId"),
                rs.getString("sectionName"),
                rs.getInt("lessonId"),
                rs.getInt("instructorId"),
                rs.getInt("room")
        );
    }

    /**
     * Get all sections (used by MainController, SectionDetailsController, etc.)
     */
    public List<Section> getAllSections() {
        List<Section> list = new ArrayList<>();
        String sql = "SELECT * FROM section";

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

    /**
     * Get sections taught by a specific instructor
     * (used by MainController and InstructorDashboardUI)
     */
    public List<Section> getSectionsByInstructor(int instructorId) {
        List<Section> list = new ArrayList<>();
        String sql = "SELECT * FROM section WHERE instructorId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, instructorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Existing: get sections for a given lesson
     */
    public List<Section> getSectionsByLesson(int lessonId) {
        List<Section> list = new ArrayList<>();
        String sql = "SELECT * FROM section WHERE lessonId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lessonId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) list.add(map(rs));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Existing: get sections that a student is enrolled in
     */
    public List<Section> getSectionsByStudentId(int studentId) {
        String sql = """
            SELECT s.* FROM section s
            JOIN sectionstudent ss ON s.sectionId = ss.sectionId
            WHERE ss.studentId = ?
        """;

        List<Section> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) list.add(map(rs));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Create a new section and return its generated ID
     * (used by AdminDashboardUI.createSection)
     */
    public int createSection(String name, int lessonId, int instructorId, int room) {
        String sql = "INSERT INTO section (sectionName, lessonId, instructorId, room) VALUES (?, ?, ?, ?)";
        int generatedId = -1;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setInt(2, lessonId);
            ps.setInt(3, instructorId);
            ps.setInt(4, room);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return generatedId;
    }

    /**
     * Delete a section by ID; returns true if a row was deleted
     * (used by AdminDashboardUI.deleteSection)
     */
    public boolean deleteSection(int id) {
        String sql = "DELETE FROM section WHERE sectionId = ?";
        boolean success = false;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            success = (affectedRows > 0);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return success;
    }
}

