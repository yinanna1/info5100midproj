import dao.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteUser {

    public static void deleteUser(int userId) {
        String sql = "DELETE FROM user WHERE userId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            int rows = stmt.executeUpdate();
            System.out.println("🗑️ " + rows + " user deleted successfully.");

        } catch (SQLException e) {
            System.err.println("❌ Error deleting user: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
