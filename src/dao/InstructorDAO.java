package dao;

import model.Instructor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstructorDAO {

    // All instructors with their names (from user table)
    public List<Instructor> getAllInstructors() {
        List<Instructor> list = new ArrayList<>();

        String sql = "SELECT i.instructorId, i.userId, u.userName " +
                "FROM Instructor i " +
                "LEFT JOIN user u ON i.userId = u.userId";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Instructor inst = new Instructor(
                        rs.getInt("instructorId"),
                        rs.getInt("userId"),
                        rs.getString("userName")
                );
                list.add(inst);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Instructor getInstructorById(int instructorId) {
        String sql = "SELECT i.instructorId, i.userId, u.userName " +
                "FROM Instructor i " +
                "LEFT JOIN user u ON i.userId = u.userId " +
                "WHERE i.instructorId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, instructorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Instructor(
                            rs.getInt("instructorId"),
                            rs.getInt("userId"),
                            rs.getString("userName")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

