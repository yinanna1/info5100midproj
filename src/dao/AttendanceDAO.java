package dao;

import java.sql.*;
import javax.sql.DataSource;

/**
 * Attendance DAO
 * Assumes you already have a DBConnection or DataSource pattern in your project.
 * Replace getConnection() with your project's actual connection method.
 */
public class AttendanceDAO {

    // If you already have a DB util, use that instead.
    private Connection getConnection() throws SQLException {
        // Example placeholder:
        return DatabaseConnection.getConnection();
    }

    /**
     * Records attendance for a student in a lesson.
     * Recommended: add a UNIQUE index on (lessonId, studentId)
     * so ON DUPLICATE KEY UPDATE works cleanly.
     */
    public void upsertAttendance(int lessonId, int studentId, String status) {
        String sql = """
            INSERT INTO Attendance (lessonId, studentId, status, timestamp)
            VALUES (?, ?, ?, NOW())
            ON DUPLICATE KEY UPDATE
                status = VALUES(status),
                timestamp = NOW()
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lessonId);
            ps.setInt(2, studentId);
            ps.setString(3, status);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to upsert attendance", e);
        }
    }
}

