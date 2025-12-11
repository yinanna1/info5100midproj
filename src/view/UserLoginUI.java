package view;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

public class UserLoginUI extends JDialog {

    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();

    private User loggedInUser = null;
    private final CountDownLatch latch = new CountDownLatch(1);

    public UserLoginUI() {
        setTitle("User Login");
        setModal(true);
        setSize(480, 350);
        setLocationRelativeTo(null);

        // Main background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 252)); // light pastel
        setContentPane(mainPanel);

        // =========================
        // HEADER (banner with image)
        // =========================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        headerPanel.setBackground(new Color(27, 28, 70)); // deep navy

        // icon on the left
        JLabel iconLabel = new JLabel();
        URL imgUrl = getClass().getResource("/images/music.png"); // same path as before
        if (imgUrl != null) {
            ImageIcon icon = new ImageIcon(imgUrl);
            Image scaled = icon.getImage().getScaledInstance(52, 52, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaled));
        }
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12));

        // title text
        JLabel titleLabel = new JLabel("Music Learning Education System", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        headerPanel.add(iconLabel, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // =========================
        // CENTER CARD (rounded panel)
        // =========================
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false); // show main background
        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        RoundedPanel card = new RoundedPanel(22);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(18, 26, 18, 26));
        card.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // welcome text
        JLabel welcomeLabel = new JLabel("Welcome back!");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        welcomeLabel.setForeground(new Color(27, 28, 70));

        JLabel subLabel = new JLabel("Please log in to continue.");
        subLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subLabel.setForeground(new Color(110, 110, 130));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        card.add(welcomeLabel, gbc);

        gbc.gridy = 1;
        card.add(subLabel, gbc);

        // spacing
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 4, 4, 4);
        card.add(Box.createVerticalStrut(8), gbc);
        gbc.insets = new Insets(4, 4, 4, 4);

        // Username row
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));

        usernameField.setPreferredSize(new Dimension(220, 28));
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 13));

        gbc.gridwidth = 1;
        gbc.gridy = 3;
        gbc.gridx = 0;
        card.add(userLabel, gbc);

        gbc.gridx = 1;
        card.add(usernameField, gbc);

        // Password row
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));

        passwordField.setPreferredSize(new Dimension(220, 28));
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 13));

        gbc.gridy = 4;
        gbc.gridx = 0;
        card.add(passLabel, gbc);

        gbc.gridx = 1;
        card.add(passwordField, gbc);

        // Login button in card
        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        loginBtn.setFocusPainted(false);
        loginBtn.setBackground(new Color(76, 92, 197));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));
        loginBtn.addActionListener(e -> attemptLogin());

        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(14, 4, 2, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        card.add(loginBtn, gbc);

        // "Forgot my password?" pseudo-link
        JButton forgotBtn = new JButton("Forgot my password?");
        forgotBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        forgotBtn.setFocusPainted(false);
        forgotBtn.setContentAreaFilled(false);
        forgotBtn.setBorderPainted(false);
        forgotBtn.setForeground(new Color(76, 92, 197));
        forgotBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Please contact the system administrator to reset your password.",
                    "Forgot Password",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        gbc.gridy = 6;
        gbc.insets = new Insets(2, 4, 0, 4);
        card.add(forgotBtn, gbc);

        // add card to center
        GridBagConstraints wrapConstraints = new GridBagConstraints();
        wrapConstraints.anchor = GridBagConstraints.CENTER;
        centerWrapper.add(card, wrapConstraints);

        // =========================
        // EXTRA LOGIN BUTTON AT BOTTOM
        // =========================
        JButton footerLoginBtn = new JButton("Login");
        footerLoginBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        footerLoginBtn.setFocusPainted(false);
        footerLoginBtn.addActionListener(e -> attemptLogin());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        bottomPanel.add(footerLoginBtn);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Make Enter key trigger login (card button)
        getRootPane().setDefaultButton(loginBtn);
    }

    // =========================
    // LOGIN LOGIC
    // =========================
    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        UserDAO userDAO = new UserDAO();
        User user = userDAO.validateUser(username, password);

        if (user != null) {
            loggedInUser = user;
            latch.countDown();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void waitForLogin() {
        try {
            latch.await();
        } catch (InterruptedException ignored) {}
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    // =========================
    // HELPER: ROUNDED PANEL
    // =========================
    private static class RoundedPanel extends JPanel {
        private final int arc;

        public RoundedPanel(int arc) {
            this.arc = arc;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
