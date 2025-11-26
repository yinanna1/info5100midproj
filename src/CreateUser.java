import dao.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CreateUser {
    public static int insertUser(String userName, String email, String password, String role) {
        String sql = "INSERT INTO user (userName, email, password, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, userName);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, role);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int userId = rs.getInt(1);
                    System.out.println("✅ New user added with ID: " + userId);
                    return userId; // return the generated user ID
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // if failed
    }
}
