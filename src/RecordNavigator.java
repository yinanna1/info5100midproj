import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class RecordNavigator extends JFrame {
    private JTextField idField, nameField, emailField, roleField;
    private JButton prevButton, nextButton;
    private ArrayList<UserRecord> records = new ArrayList<>();
    private int currentIndex = 0;

    // ✅ JDBC connection details (update password)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";           // your MySQL username
    private static final String DB_PASS = "yina0624";  // your MySQL password

    public RecordNavigator() {
        setTitle("EMS model.User Record Viewer");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 2, 10, 10));

        // === UI Components ===
        add(new JLabel("model.User ID:"));
        idField = new JTextField();
        idField.setEditable(false);
        add(idField);

        add(new JLabel("Name:"));
        nameField = new JTextField();
        nameField.setEditable(false);
        add(nameField);

        add(new JLabel("Email:"));
        emailField = new JTextField();
        emailField.setEditable(false);
        add(emailField);

        add(new JLabel("Role:"));
        roleField = new JTextField();
        roleField.setEditable(false);
        add(roleField);

        prevButton = new JButton("Previous");
        nextButton = new JButton("Next");
        add(prevButton);
        add(nextButton);

        // === Load records from MySQL ===
        loadRecords();

        // === Show first record if available ===
        if (!records.isEmpty()) {
            displayRecord(currentIndex);
        } else {
            JOptionPane.showMessageDialog(this, "No user records found in the database.");
        }

        // === Button listeners ===
        prevButton.addActionListener(e -> showPrevious());
        nextButton.addActionListener(e -> showNext());

        setVisible(true);
    }

    // === Load user data from database ===
    private void loadRecords() {
        String query = "SELECT userId, userName, email, role FROM user";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("userId");
                String name = rs.getString("userName");
                String email = rs.getString("email");
                String role = rs.getString("role");

                records.add(new UserRecord(id, name, email, role));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Database Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // === Display one record ===
    private void displayRecord(int index) {
        if (index >= 0 && index < records.size()) {
            UserRecord r = records.get(index);
            idField.setText(String.valueOf(r.userId));
            nameField.setText(r.userName);
            emailField.setText(r.email);
            roleField.setText(r.role);
        }
    }

    // === Show previous record ===
    private void showPrevious() {
        if (currentIndex > 0) {
            currentIndex--;
            displayRecord(currentIndex);
        } else {
            JOptionPane.showMessageDialog(this, "Already at the first record.");
        }
    }

    // === Show next record ===
    private void showNext() {
        if (currentIndex < records.size() - 1) {
            currentIndex++;
            displayRecord(currentIndex);
        } else {
            JOptionPane.showMessageDialog(this, "Already at the last record.");
        }
    }

    public static void main(String[] args) {
        // Load MySQL JDBC Driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found.");
            return;
        }

        SwingUtilities.invokeLater(RecordNavigator::new);
    }

    // === Simple record class for users ===
    static class UserRecord {
        int userId;
        String userName;
        String email;
        String role;

        UserRecord(int userId, String userName, String email, String role) {
            this.userId = userId;
            this.userName = userName;
            this.email = email;
            this.role = role;
        }
    }
}
