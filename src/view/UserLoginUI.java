package view;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;
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
        setSize(350, 200);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        form.add(new JLabel("Username:"));
        form.add(usernameField);

        form.add(new JLabel("Password:"));
        form.add(passwordField);

        JButton loginBtn = new JButton("Login");

        loginBtn.addActionListener(e -> attemptLogin());

        add(form, BorderLayout.CENTER);
        add(loginBtn, BorderLayout.SOUTH);
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
