import dao.DatabaseConnection;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReadUser {

    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("userId"),
                        rs.getString("userName"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error reading users: " + e.getMessage());
        }

        return users;
    }

    public static User getUserById(int userId) {
        String sql = "SELECT * FROM user WHERE userId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("userId"),
                        rs.getString("userName"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching user by ID: " + e.getMessage());
        }
        return null;
    }
}
