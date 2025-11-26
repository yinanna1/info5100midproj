package dao;

import model.Lesson;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LessonDAO {

    // All lessons (courses)
    public List<Lesson> getAllLessons() {
        List<Lesson> list = new ArrayList<>();
        String sql = "SELECT lessonId, instructorId, title, instrument, " +
                "       startTime, endTime, room, description " +
                "FROM lesson";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Lesson l = new Lesson(
                        rs.getInt("lessonId"),
                        rs.getInt("instructorId"),
                        rs.getString("title"),
                        rs.getString("instrument"),
                        rs.getString("startTime"),
                        rs.getString("endTime"),
                        rs.getInt("room"),
                        rs.getString("description")
                );
                list.add(l);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Lesson getLessonById(int lessonId) {
        String sql = "SELECT * FROM lesson WHERE lessonId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lessonId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Lesson(
                        rs.getInt("lessonId"),
                        rs.getInt("instructorId"),
                        rs.getString("title"),
                        rs.getString("instrument"),
                        rs.getString("startTime"),
                        rs.getString("endTime"),
                        rs.getInt("room"),
                        rs.getString("description")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get lesson linked to a specific section
    public Lesson getLessonBySection(int sectionId) {

        String sql =
                "SELECT l.* " +
                        "FROM lessons l " +
                        "JOIN lesson_students ls ON l.lessonId = ls.lessonId " +
                        "WHERE ls.sectionId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sectionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Lesson(
                        rs.getInt("lessonId"),
                        rs.getInt("instructorId"),
                        rs.getString("title"),
                        rs.getString("instrument"),
                        rs.getString("startTime"),
                        rs.getString("endTime"),
                        rs.getInt("room"),
                        rs.getString("description")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
