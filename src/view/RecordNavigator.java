package view;

import model.User;
import model.UserModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RecordNavigator extends JFrame {

    private List<User> users;
    private int index = 0;

    private JLabel idLabel = new JLabel();
    private JLabel nameLabel = new JLabel();
    private JLabel emailLabel = new JLabel();
    private JLabel roleLabel = new JLabel();

    public RecordNavigator() {
        setTitle("User Record Viewer");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Load users from database
        UserModel model = new UserModel();
        users = model.getAllUsers();

        // MAIN PANEL
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("User Detail", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(header);

        idLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        panel.add(idLabel);
        panel.add(nameLabel);
        panel.add(emailLabel);
        panel.add(roleLabel);

        // BUTTONS
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton prevBtn = new JButton("◀ Prev");
        JButton nextBtn = new JButton("Next ▶");
        buttonPanel.add(prevBtn);
        buttonPanel.add(nextBtn);

        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Show first record
        showRecord();

        // LISTENERS
        prevBtn.addActionListener(e -> {
            if (index > 0) {
                index--;
                showRecord();
            }
        });

        nextBtn.addActionListener(e -> {
            if (index < users.size() - 1) {
                index++;
                showRecord();
            }
        });

        setVisible(true);
    }

    private void showRecord() {
        if (users.isEmpty()) {
            idLabel.setText("No users found.");
            nameLabel.setText("");
            emailLabel.setText("");
            roleLabel.setText("");
            return;
        }

        User u = users.get(index);
        idLabel.setText("User ID: " + u.getUserId());
        nameLabel.setText("Name: " + u.getUserName());
        emailLabel.setText("Email: " + u.getEmail());
        roleLabel.setText("Role: " + u.getRole());
    }

    // MAIN for direct launch
    public static void main(String[] args) {
        SwingUtilities.invokeLater(RecordNavigator::new);
    }
}
