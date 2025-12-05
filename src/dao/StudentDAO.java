package dao;

import model.Student;
import java.sql.*;
import java.util.*;

public class StudentDAO {

    private Student map(ResultSet rs) throws SQLException {
        return new Student(
                rs.getInt("studentId"),
                rs.getInt("userId"),
                "Student " + rs.getInt("studentId")  // temporary name
        );
    }

    public Student getStudentByUserId(int userId) {
        String sql = "SELECT * FROM student WHERE userId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return map(rs);

        } catch (Exception e) { e.printStackTrace(); }

        return null;
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM student";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));

        } catch (Exception e) { e.printStackTrace(); }

        return list;
    }
}
