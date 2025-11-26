import dao.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateUser {

    public static void updateUser(int userId, String newEmail, String newPassword) {
        String sql = "UPDATE user SET email = ?, password = ? WHERE userId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newEmail);
            stmt.setString(2, newPassword);
            stmt.setInt(3, userId);

            int rows = stmt.executeUpdate();
            System.out.println("✏️ " + rows + " user updated successfully.");

        } catch (SQLException e) {
            System.err.println("❌ Error updating user: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
