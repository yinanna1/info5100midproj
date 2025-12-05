package dao;

import model.LibraryItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibraryItemDAO {

    private LibraryItem map(ResultSet rs) throws SQLException {
        return new LibraryItem(
                rs.getInt("libraryId"),
                rs.getString("title"),
                rs.getString("url"),
                rs.getString("instrument")
        );
    }

    public List<LibraryItem> getAllItems() {
        List<LibraryItem> list = new ArrayList<>();
        String sql = "SELECT * FROM library_items";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();  // debug
        }

        return list;
    }

    // AUTO_INCREMENT version: let DB assign libraryId
    public boolean createItem(String title, String url, String instrument) throws SQLException {
        String sql = "INSERT INTO library_items (title, url, instrument) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setString(2, url);
            ps.setString(3, instrument);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteItem(int libraryId) throws SQLException {
        String sql = "DELETE FROM library_items WHERE libraryId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, libraryId);
            return ps.executeUpdate() > 0;
        }
    }
}
