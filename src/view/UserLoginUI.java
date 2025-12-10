package view;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

public class UserLoginUI extends JDialog {

    private JTextField usernameField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();

    private User loggedInUser = null;

    // Used to block Main until login is done
    private final CountDownLatch latch = new CountDownLatch(1);

    public UserLoginUI() {
        setTitle("User Login");
        setModal(true);
        setSize(450, 300); // a bit bigger for banner + image
        setLocationRelativeTo(null);

        // --- MAIN LAYOUT ---
        JPanel mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        // =========================
        // 1. HEADER (banner + image)
        // =========================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.setBackground(new Color(30, 30, 60)); // dark blue tone

        // Title text
        JLabel titleLabel = new JLabel("Music Learning Education System", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        // Music image (left side)
        JLabel iconLabel = new JLabel();
        URL imgUrl = getClass().getResource("/music.jpg"); // put music.png under src/images or resources/images
        if (imgUrl != null) {
            ImageIcon icon = new ImageIcon(imgUrl);
            // Optional: scale image a bit smaller
            Image scaled = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaled));
        }
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);

        headerPanel.add(iconLabel, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // =========================
        // 2. FORM (username + password)
        // =========================
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username label + field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        formPanel.add(usernameField, gbc);

        // Password label + field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        formPanel.add(passwordField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // =========================
        // 3. LOGIN BUTTON (bottom)
        // =========================
        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> attemptLogin());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        buttonPanel.add(loginBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        UserDAO userDAO = new UserDAO();
        User user = userDAO.validateUser(username, password);

        if (user != null) {
            loggedInUser = user;
            latch.countDown();   // release Main thread
            dispose();           // close popup
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /** BLOCK until the user logs in */
    public void waitForLogin() {
        try {
            latch.await();
        } catch (InterruptedException ignored) {}
    }

    /** Return the logged-in user */
    public User getLoggedInUser() {
        return loggedInUser;
    }
}
