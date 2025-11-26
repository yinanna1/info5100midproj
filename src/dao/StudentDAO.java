package dao;

import model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // All students with their names
    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();

        String sql = "SELECT s.studentId, s.userId, u.userName " +
                "FROM Student s " +
                "LEFT JOIN user u ON s.userId = u.userId";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Student s = new Student(
                        rs.getInt("studentId"),
                        rs.getInt("userId"),
                        rs.getString("userName")
                );
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Students registered in a given section (Section–Students)
    public List<Student> getStudentsBySection(int sectionId) {
        List<Student> list = new ArrayList<>();

        String sql = "SELECT s.studentId, s.userId, u.userName " +
                "FROM SectionStudent ss " +
                "JOIN Student s ON ss.studentId = s.studentId " +
                "LEFT JOIN user u ON s.userId = u.userId " +
                "WHERE ss.sectionId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Student s = new Student(
                            rs.getInt("studentId"),
                            rs.getInt("userId"),
                            rs.getString("userName")
                    );
                    list.add(s);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Helper: all sections for a particular student is handled in SectionDAO
}
