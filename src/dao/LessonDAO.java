package dao;

import model.Lesson;
import java.sql.*;
import java.util.*;

public class LessonDAO {

    private Lesson map(ResultSet rs) throws SQLException {
        return new Lesson(
                rs.getInt("lessonId"),
                rs.getInt("instructorId"),
                rs.getString("title"),
                rs.getString("instrument"),
                rs.getString("startTime"),
                rs.getString("endTime"),
                rs.getString("description")
        );
    }

    public List<Lesson> getAllLessons() {
        List<Lesson> list = new ArrayList<>();
        String sql = "SELECT * FROM lesson";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));

        } catch (Exception e) { e.printStackTrace(); }

        return list;
    }

    public Lesson getLessonById(int id) {
        String sql = "SELECT * FROM lesson WHERE lessonId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);

        } catch (Exception e) { e.printStackTrace(); }

        return null;
    }

    // Create a new lesson and return its generated ID
    public int createLesson(int instructorId,
                            String title,
                            String instrument,
                            String start,
                            String end,
                            String desc) {

        String sql = "INSERT INTO lesson " +
                "(instructorId, title, instrument, startTime, endTime, description) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        int generatedId = -1;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, instructorId);
            ps.setString(2, title);
            ps.setString(3, instrument);

            // assumes start/end are in format "yyyy-MM-dd HH:mm:ss"
            ps.setTimestamp(4, Timestamp.valueOf(start));
            ps.setTimestamp(5, Timestamp.valueOf(end));

            ps.setString(6, desc);

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

    // Delete a lesson by ID; returns true if something was deleted
    public boolean deleteLesson(int id) {
        String sql = "DELETE FROM lesson WHERE lessonId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public Integer getLatestLessonIdBySection(int sectionId) {
        String sql = """
        SELECT lessonId
        FROM Lesson
        WHERE sectionId = ?
        ORDER BY lessonId DESC
        LIMIT 1
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("lessonId");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to get latest lesson for section", e);
        }

        return null;
    }


}
