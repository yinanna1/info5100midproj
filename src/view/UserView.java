package view;

import javax.swing.*;
import java.awt.*;

public class UserView extends JFrame {

    // Fields for CREATE
    public JTextField nameField = new JTextField();
    public JTextField emailField = new JTextField();
    public JTextField passwordField = new JTextField();
    public JComboBox<String> roleDropdown =
            new JComboBox<>(new String[]{"student", "instructor", "admin"});
    public JButton createBtn = new JButton("Create User");

    // Fields for READ
    public JTextArea readArea = new JTextArea();
    public JButton readBtn = new JButton("Load Users");

    // Fields for UPDATE
    public JTextField updateIdField = new JTextField();
    public JTextField updateEmailField = new JTextField();
    public JTextField updatePasswordField = new JTextField();
    public JButton updateBtn = new JButton("Update User");

    // Fields for DELETE
    public JTextField deleteIdField = new JTextField();
    public JButton deleteBtn = new JButton("Delete User");

    // Record Navigator button
    public JButton navigatorBtn = new JButton("Open Record Navigator");

    public UserView() {
        setTitle("User Management (MVC)");
        setSize(650, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();

        // CREATE TAB
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

        // READ TAB
        JPanel readPanel = new JPanel(new BorderLayout());
        readArea.setEditable(false);
        readPanel.add(readBtn, BorderLayout.NORTH);
        readPanel.add(new JScrollPane(readArea), BorderLayout.CENTER);

        // UPDATE TAB
        JPanel updatePanel = new JPanel(new GridLayout(4, 2, 10, 10));
        updatePanel.add(new JLabel("User ID:"));
        updatePanel.add(updateIdField);
        updatePanel.add(new JLabel("New Email:"));
        updatePanel.add(updateEmailField);
        updatePanel.add(new JLabel("New Password:"));
        updatePanel.add(updatePasswordField);
        updatePanel.add(new JLabel(""));
        updatePanel.add(updateBtn);

        // DELETE TAB
        JPanel deletePanel = new JPanel(new GridLayout(2, 2, 10, 10));
        deletePanel.add(new JLabel("User ID:"));
        deletePanel.add(deleteIdField);
        deletePanel.add(new JLabel(""));
        deletePanel.add(deleteBtn);

        // Navigator tab
        JPanel navPanel = new JPanel();
        navPanel.add(navigatorBtn);

        // Add Tabs
        tabs.add("Create", createPanel);
        tabs.add("Read", readPanel);
        tabs.add("Update", updatePanel);
        tabs.add("Delete", deletePanel);
        tabs.add("Navigator", navPanel);

        add(tabs);
        setVisible(true);
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }
}
