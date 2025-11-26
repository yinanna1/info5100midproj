import javax.swing.*;
import java.awt.*;

public class UserView extends JFrame {

    // Create fields
    JTextField nameField = new JTextField();
    JTextField emailField = new JTextField();
    JTextField passwordField = new JTextField();
    JComboBox<String> roleDropdown = new JComboBox<>(new String[]{"student", "instructor", "admin"});
    JButton createBtn = new JButton("Add model.User");

    // Read fields
    JTextArea readArea = new JTextArea();
    JButton readBtn = new JButton("Load Users");

    // Update fields
    JTextField updateIdField = new JTextField();
    JTextField updateEmailField = new JTextField();
    JTextField updatePasswordField = new JTextField();
    JButton updateBtn = new JButton("Update model.User");

    // Delete fields
    JTextField deleteIdField = new JTextField();
    JButton deleteBtn = new JButton("Delete model.User");

    public UserView() {
        setTitle("model.User Management System (MVC)");
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
        createPanel.add(new JLabel(""));
        createPanel.add(createBtn);

        // ------------------- READ TAB -------------------
        JPanel readPanel = new JPanel(new BorderLayout());
        readArea.setEditable(false);
        readPanel.add(readBtn, BorderLayout.NORTH);
        readPanel.add(new JScrollPane(readArea), BorderLayout.CENTER);

        // ------------------- UPDATE TAB -------------------
        JPanel updatePanel = new JPanel(new GridLayout(4, 2, 10, 10));
        updatePanel.add(new JLabel("model.User ID:"));
        updatePanel.add(updateIdField);
        updatePanel.add(new JLabel("New Email:"));
        updatePanel.add(updateEmailField);
        updatePanel.add(new JLabel("New Password:"));
        updatePanel.add(updatePasswordField);
        updatePanel.add(new JLabel(""));
        updatePanel.add(updateBtn);

        // ------------------- DELETE TAB -------------------
        JPanel deletePanel = new JPanel(new GridLayout(2, 2, 10, 10));
        deletePanel.add(new JLabel("model.User ID:"));
        deletePanel.add(deleteIdField);
        deletePanel.add(new JLabel(""));
        deletePanel.add(deleteBtn);

        tabs.add("Create", createPanel);
        tabs.add("Read", readPanel);
        tabs.add("Update", updatePanel);
        tabs.add("Delete", deletePanel);

        add(tabs);
        setVisible(true);
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }
}
