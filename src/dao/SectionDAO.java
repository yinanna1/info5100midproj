package dao;

import model.Section;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SectionDAO {

    // Get ALL sections
    public List<Section> getAllSections() {
        List<Section> list = new ArrayList<>();
        String sql = "SELECT * FROM Section";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Section(
                        rs.getInt("sectionId"),
                        rs.getString("sectionName"),
                        rs.getInt("lessonId"),
                        rs.getInt("instructorId")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }

        return list;
    }

    // sections by lesson
    public List<Section> getSectionsByLesson(int lessonId) {
        List<Section> list = new ArrayList<>();
        String sql = "SELECT * FROM Section WHERE lessonId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, lessonId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new Section(
                        rs.getInt("sectionId"),
                        rs.getString("sectionName"),
                        rs.getInt("lessonId"),
                        rs.getInt("instructorId")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }

        return list;
    }

    // sections by instructor
    public List<Section> getSectionsByInstructor(int instructorId) {
        List<Section> list = new ArrayList<>();
        String sql = "SELECT * FROM Section WHERE instructorId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new Section(
                        rs.getInt("sectionId"),
                        rs.getString("sectionName"),
                        rs.getInt("lessonId"),
                        rs.getInt("instructorId")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }

        return list;
    }
}
