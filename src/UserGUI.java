import model.User;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.List;

public class UserGUI extends JFrame {
    private JTextField nameField, emailField, passwordField, roleField;
    private JTextField deleteUserIdField, updateUserIdField, updateEmailField, updatePasswordField;
    private JButton registerButton, deleteButton, updateButton, readButton;
    private JTextArea readTextArea;

    // Database connection info
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "yina0624";

    public UserGUI() {
        setTitle("EMS model.User Management");
        setSize(650, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ---------- CREATE USER PANEL ----------
        JPanel createPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        createPanel.setBorder(BorderFactory.createTitledBorder("Create model.User"));

        createPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        createPanel.add(nameField);

        createPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        createPanel.add(emailField);

        createPanel.add(new JLabel("Password:"));
        passwordField = new JTextField();
        createPanel.add(passwordField);

        createPanel.add(new JLabel("Role:"));
        roleField = new JTextField("student");
        createPanel.add(roleField);

        registerButton = new JButton("Register model.User");
        createPanel.add(new JLabel(""));
        createPanel.add(registerButton);

        // ---------- DELETE USER PANEL ----------
        JPanel deletePanel = new JPanel(new GridLayout(2, 2, 10, 10));
        deletePanel.setBorder(BorderFactory.createTitledBorder("Delete model.User"));
        deletePanel.add(new JLabel("model.User ID:"));
        deleteUserIdField = new JTextField();
        deletePanel.add(deleteUserIdField);
        deleteButton = new JButton("Delete model.User");
        deletePanel.add(new JLabel(""));
        deletePanel.add(deleteButton);

        // ---------- UPDATE USER PANEL ----------
        JPanel updatePanel = new JPanel(new GridLayout(4, 2, 10, 10));
        updatePanel.setBorder(BorderFactory.createTitledBorder("Update model.User"));
        updatePanel.add(new JLabel("model.User ID:"));
        updateUserIdField = new JTextField();
        updatePanel.add(updateUserIdField);
        updatePanel.add(new JLabel("New Email:"));
        updateEmailField = new JTextField();
        updatePanel.add(updateEmailField);
        updatePanel.add(new JLabel("New Password:"));
        updatePasswordField = new JTextField();
        updatePanel.add(updatePasswordField);
        updateButton = new JButton("Update model.User");
        updatePanel.add(new JLabel(""));
        updatePanel.add(updateButton);

        // ---------- READ USERS PANEL ----------
        JPanel readPanel = new JPanel(new BorderLayout());
        readPanel.setBorder(BorderFactory.createTitledBorder("All Users"));
        readButton = new JButton("Load All Users");
        readTextArea = new JTextArea(10, 40);
        readTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(readTextArea);
        readPanel.add(readButton, BorderLayout.NORTH);
        readPanel.add(scrollPane, BorderLayout.CENTER);

        // ---------- COMBINE EVERYTHING ----------
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(createPanel);
        mainPanel.add(deletePanel);
        mainPanel.add(updatePanel);
        mainPanel.add(readPanel);
        add(mainPanel, BorderLayout.CENTER);

        // ---------- BUTTON ACTIONS ----------
        registerButton.addActionListener(e -> createUser());
        deleteButton.addActionListener(e -> deleteUser());
        updateButton.addActionListener(e -> updateUser());
        readButton.addActionListener(e -> readAllUsers());

        setVisible(true);
    }

    // ---------------- CREATE USER ----------------
    private void createUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }

        String sql = "INSERT INTO user (userName, email, password, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, role);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int userId = rs.getInt(1);
                    JOptionPane.showMessageDialog(this, "✅ model.User created successfully!\nmodel.User ID: " + userId);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Failed to register user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ---------------- DELETE USER ----------------
    private void deleteUser() {
        String userIdText = deleteUserIdField.getText().trim();
        if (userIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a model.User ID to delete.");
            return;
        }
        try {
            int userId = Integer.parseInt(userIdText);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete user ID " + userId + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                DeleteUser.deleteUser(userId);
                JOptionPane.showMessageDialog(this, "🗑️ model.User deleted (if existed).");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "model.User ID must be a number.");
        }
    }

    // ---------------- UPDATE USER ----------------
    private void updateUser() {
        String userIdText = updateUserIdField.getText().trim();
        String newEmail = updateEmailField.getText().trim();
        String newPassword = updatePasswordField.getText().trim();

        if (userIdText.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdText);
            UpdateUser.updateUser(userId, newEmail, newPassword);
            JOptionPane.showMessageDialog(this, "✏️ model.User updated successfully!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "model.User ID must be a number.");
        }
    }

    // ---------------- READ USERS ----------------
    private void readAllUsers() {
        List<User> users = ReadUser.getAllUsers();
        readTextArea.setText("");
        if (users.isEmpty()) {
            readTextArea.append("No users found.\n");
        } else {
            for (User user : users) {
                readTextArea.append(
                        "ID: " + user.getUserId() +
                                " | Name: " + user.getUserName() +
                                " | Email: " + user.getEmail() +
                                " | Role: " + user.getRole() + "\n"
                );
            }
        }
    }

    // ---------------- MAIN ----------------
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ MySQL driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found!");
            return;
        }
        SwingUtilities.invokeLater(UserGUI::new);
    }
}


