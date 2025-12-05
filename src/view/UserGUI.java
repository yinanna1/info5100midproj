package view;

import model.User;
import model.UserModel;
import controller.UserController;

import javax.swing.*;
import java.awt.*;

public class UserGUI extends JFrame {

    private final UserController controller;

    // Create fields
    private JTextField nameField = new JTextField();
    private JTextField emailField = new JTextField();
    private JTextField passwordField = new JTextField();
    private JComboBox<String> roleDropdown = new JComboBox<>(new String[]{"student", "instructor", "admin"});

    // Read fields
    private JTextArea readArea = new JTextArea();
    private JButton readBtn = new JButton("Load Users");

    // Update fields
    private JTextField updateIdField = new JTextField();
    private JTextField updateEmailField = new JTextField();
    private JTextField updatePasswordField = new JTextField();

    // Delete fields
    private JTextField deleteIdField = new JTextField();

    public UserGUI(UserController controller) {
        this.controller = controller;

        setTitle("User Management System");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        // ------------------- CREATE TAB -------------------
        JPanel createPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        createPanel.add(new JLabel("Name:"));
        createPanel.add(nameField);
        createPanel.add(new JLabel("Email:"));
        createPanel.add(emailField);
        createPanel.add(new JLabel("Password:"));
        createPanel.add(passwordField);
        createPanel.add(new JLabel("Role:"));
        createPanel.add(roleDropdown);
        JButton createBtn = new JButton("Create User");
        createPanel.add(new JLabel(""));
        createPanel.add(createBtn);

        // ------------------- READ TAB -------------------
        JPanel readPanel = new JPanel(new BorderLayout());
        readArea.setEditable(false);
        readPanel.add(readBtn, BorderLayout.NORTH);
        readPanel.add(new JScrollPane(readArea), BorderLayout.CENTER);

        // ------------------- UPDATE TAB -------------------
        JPanel updatePanel = new JPanel(new GridLayout(4, 2, 10, 10));
        updatePanel.add(new JLabel("User ID:"));
        updatePanel.add(updateIdField);
        updatePanel.add(new JLabel("New Email:"));
        updatePanel.add(updateEmailField);
        updatePanel.add(new JLabel("New Password:"));
        updatePanel.add(updatePasswordField);
        JButton updateBtn = new JButton("Update User");
        updatePanel.add(new JLabel(""));
        updatePanel.add(updateBtn);

        // ------------------- DELETE TAB -------------------
        JPanel deletePanel = new JPanel(new GridLayout(2, 2, 10, 10));
        deletePanel.add(new JLabel("User ID:"));
        deletePanel.add(deleteIdField);
        JButton deleteBtn = new JButton("Delete User");
        deletePanel.add(new JLabel(""));
        deletePanel.add(deleteBtn);

        tabs.add("Create", createPanel);
        tabs.add("Read", readPanel);
        tabs.add("Update", updatePanel);
        tabs.add("Delete", deletePanel);

        add(tabs);
        setVisible(true);

        // ------------------- ACTION LISTENERS -------------------

        // CREATE
        createBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();
            String role = (String) roleDropdown.getSelectedItem();

            int newId = controller.createUser(name, email, password, role);
            if (newId > 0) {
                JOptionPane.showMessageDialog(this,
                        "User created!\nID: " + newId + "\nRole: " + role);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create user (Duplicate name?)");
            }
        });

        // READ
        readBtn.addActionListener(e -> {
            readArea.setText("");
            for (User user : controller.getAllUsers()) {
                readArea.append(user.toString() + "\n");
            }
        });

        // UPDATE
        updateBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(updateIdField.getText().trim());
                String newEmail = updateEmailField.getText().trim();
                String newPass = updatePasswordField.getText().trim();

                boolean ok = controller.updateUser(id, newEmail, newPass);
                JOptionPane.showMessageDialog(this, ok ? "Updated!" : "Update failed");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID");
            }
        });

        // DELETE
        deleteBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(deleteIdField.getText().trim());
                boolean ok = controller.deleteUser(id);
                JOptionPane.showMessageDialog(this, ok ? "Deleted!" : "Delete failed");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID");
            }
        });

    }

    // ---------------------------------------------------
    // MAIN FOR RUNNING USER GUI DIRECTLY
    // ---------------------------------------------------
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL driver loaded.");
        } catch (Exception e) {
            System.out.println("Driver missing!");
            return;
        }

        UserModel model = new UserModel();
        UserController controller = new UserController(model);

        SwingUtilities.invokeLater(() -> new UserGUI(controller));
    }

}
