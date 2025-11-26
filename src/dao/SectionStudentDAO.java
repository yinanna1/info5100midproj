package dao;

import model.Section;
import model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SectionStudentDAO {

    // ─────────────────────────────────────────────
    // 1️⃣ GET ALL STUDENTS IN A SECTION
    // ─────────────────────────────────────────────
    public List<Student> getStudentsBySection(int sectionId) {
        List<Student> list = new ArrayList<>();

        String sql =
                "SELECT st.studentId, st.userId, u.userName " +
                        "FROM SectionStudent ss " +
                        "JOIN Student st ON ss.studentId = st.studentId " +
                        "JOIN user u ON st.userId = u.userId " +
                        "WHERE ss.sectionId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sectionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new Student(
                        rs.getInt("studentId"),
                        rs.getInt("userId"),
                        rs.getString("userName")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ─────────────────────────────────────────────
    // 2️⃣ GET ALL SECTIONS A STUDENT IS ENROLLED IN
    // ─────────────────────────────────────────────
    public List<Section> getSectionsByStudent(int studentId) {
        List<Section> list = new ArrayList<>();

        String sql =
                "SELECT s.sectionId, s.sectionName, s.lessonId, s.instructorId, s.room " +
                        "FROM SectionStudent ss " +
                        "JOIN Section s ON ss.sectionId = s.sectionId " +
                        "WHERE ss.studentId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new Section(
                        rs.getInt("sectionId"),
                        rs.getString("sectionName"),
                        rs.getInt("lessonId"),
                        rs.getInt("instructorId"),
                        rs.getInt("room")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
