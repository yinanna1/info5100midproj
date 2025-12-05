package dao;

import model.Section;
import model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SectionStudentDAO {

    // ADD STUDENT TO SECTION
    public boolean addStudentToSection(int studentId, int sectionId) {
        String sql = "INSERT INTO SectionStudent (studentId, sectionId) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // REMOVE STUDENT FROM SECTION
    public boolean removeStudentFromSection(int studentId, int sectionId) {
        String sql = "DELETE FROM SectionStudent WHERE studentId = ? AND sectionId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // CHECK IF STUDENT ALREADY ENROLLED
    public boolean isStudentInSection(int studentId, int sectionId) {
        String sql = "SELECT 1 FROM SectionStudent WHERE studentId = ? AND sectionId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ❗ REQUIRED BY CONTROLLERS
    // GET ALL STUDENTS IN A SECTION
    public List<Student> getStudentsBySection(int sectionId) {
        List<Student> list = new ArrayList<>();

        String sql =
                "SELECT s.studentId, s.userId, u.userName " +
                        "FROM SectionStudent ss " +
                        "JOIN Student s ON ss.studentId = s.studentId " +
                        "LEFT JOIN user u ON s.userId = u.userId " +
                        "WHERE ss.sectionId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();

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

    // ❗ REQUIRED BY MainController
    // GET SECTIONS A STUDENT IS ENROLLED IN
    public List<Section> getSectionsByStudent(int studentId) {
        List<Section> list = new ArrayList<>();

        String sql =
                "SELECT s.sectionId, s.sectionName, s.lessonId, s.instructorId, s.room " +
                        "FROM SectionStudent ss " +
                        "JOIN Section s ON ss.sectionId = s.sectionId " +
                        "WHERE ss.studentId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

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
