package dao;

import model.Section;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SectionDAO {

    // ===========================================================
    // Helper: Convert ResultSet → Section object
    // ===========================================================
    private Section map(ResultSet rs) throws SQLException {
        return new Section(
                rs.getInt("sectionId"),
                rs.getString("sectionName"),
                rs.getInt("lessonId"),
                rs.getInt("instructorId"),
                rs.getInt("room")
        );
    }

    // ===========================================================
    // Get ALL sections
    // ===========================================================
    public List<Section> getAllSections() {
        List<Section> list = new ArrayList<>();
        String sql = "SELECT * FROM Section";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ===========================================================
    // Create a new section
    // ===========================================================
    public int createSection(String name, int lessonId, int instructorId, int room) {
        String sql = """
            INSERT INTO Section (sectionName, lessonId, instructorId, room)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setInt(2, lessonId);
            ps.setInt(3, instructorId);
            ps.setInt(4, room);

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // failed
    }

    // ===========================================================
    // Delete a section
    // ===========================================================
    public boolean deleteSection(int id) {
        String sql = "DELETE FROM Section WHERE sectionId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // ===========================================================
    // NEW: Get sections belonging to a specific lesson
    // Used in Course → Sections, StudentDashboard, etc.
    // ===========================================================
    public List<Section> getSectionsByLesson(int lessonId) {
        List<Section> list = new ArrayList<>();
        String sql = "SELECT * FROM Section WHERE lessonId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lessonId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) list.add(map(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ===========================================================
    // NEW: Get sections by instructorId (optional but useful)
    // ===========================================================
    public List<Section> getSectionsByInstructor(int instructorId) {
        List<Section> list = new ArrayList<>();
        String sql = "SELECT * FROM Section WHERE instructorId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, instructorId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) list.add(map(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ===========================================================
    // NEW: Get a single section by ID (needed for detail views)
    // ===========================================================
    public Section getSectionById(int id) {
        String sql = "SELECT * FROM Section WHERE sectionId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return map(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
