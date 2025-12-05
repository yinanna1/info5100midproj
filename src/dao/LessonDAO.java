package dao;

import model.Lesson;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public int createLesson(
            int instructorId, String title, String instrument,
            String startTime, String endTime, String description
    ) {
        String sql = """
                INSERT INTO lesson
                (instructorId, title, instrument, startTime, endTime, description)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, instructorId);
            ps.setString(2, title);
            ps.setString(3, instrument);
            ps.setString(4, startTime);
            ps.setString(5, endTime);
            ps.setString(6, description);

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);

        } catch (SQLException e) { e.printStackTrace(); }

        return -1;
    }

    public boolean deleteLesson(int id) {
        String sql = "DELETE FROM lesson WHERE lessonId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public Lesson getLessonById(int id) {
        String sql = "SELECT * FROM lesson WHERE lessonId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);

        } catch (SQLException e) { e.printStackTrace(); }

        return null;
    }

    public Lesson getLesson(int lessonId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM lesson WHERE lesson_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, lessonId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Lesson(
                        rs.getInt("lesson_id"),
                        rs.getInt("instructor_id"),
                        rs.getString("title"),
                        rs.getString("instrument"),
                        rs.getString("start_time"),
                        rs.getString("end_time"),
                        rs.getString("description")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
