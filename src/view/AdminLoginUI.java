package view;

import model.User;
import model.UserModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminLoginUI extends JDialog {

    private JTextField usernameField = new JTextField(14);
    private JPasswordField passwordField = new JPasswordField(14);

    private Runnable onSuccess;        // callback to notify MainController
    private final UserModel userModel = new UserModel();

    // ---- FIXED: No-argument constructor allowed ----
    public AdminLoginUI() {
        this(null);
    }

    // ---- MAIN WORKING CONSTRUCTOR ----
    public AdminLoginUI(Runnable onSuccess) {
        this.onSuccess = onSuccess;

        setTitle("Admin Login");
        setModal(true);
        setSize(330, 180);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- Form inputs ---
        JPanel form = new JPanel(new GridLayout(2, 2, 5, 5));
        form.add(new JLabel("Username:"));
        form.add(usernameField);
        form.add(new JLabel("Password:"));
        form.add(passwordField);

        // --- Buttons ---
        JButton loginBtn = new JButton("Login");
        JButton cancelBtn = new JButton("Cancel");

        JPanel btnPanel = new JPanel();
        btnPanel.add(loginBtn);
        btnPanel.add(cancelBtn);

        add(form, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        // --- Events ---
        loginBtn.addActionListener(e -> attemptLogin());
        cancelBtn.addActionListener(e -> dispose());

        setVisible(true);
    }

    // validate admin credentials
    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username & password.");
            return;
        }

        List<User> users = userModel.getAllUsers();

        for (User u : users) {
            boolean matchUser = u.getUserName().equals(username);
            boolean matchPass = u.getPassword().equals(password);
            boolean isAdmin = u.getRole().equalsIgnoreCase("admin");

            if (matchUser && matchPass && isAdmin) {
                JOptionPane.showMessageDialog(this, "Admin login successful!");
                dispose();
                if (onSuccess != null) onSuccess.run();
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Invalid admin credentials.");
    }
}
