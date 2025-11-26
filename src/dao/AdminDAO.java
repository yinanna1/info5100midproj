package dao;

import java.sql.*;

public class AdminDAO {

    public boolean validateAdmin(String username, String password) {

        String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            return rs.next(); // admin exists

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
