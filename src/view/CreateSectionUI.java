package view;

import javax.swing.*;
import java.awt.*;

public class CreateSectionUI extends JDialog {

    public interface CreateSectionListener {
        void onCreate(String sectionName, int lessonId, int instructorId, int room);
    }

    public CreateSectionUI(CreateSectionListener listener) {

        setTitle("Create Section");
        setSize(400, 260);
        setModal(true);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JTextField nameField = new JTextField(15);
        JTextField lessonField = new JTextField(15);
        JTextField instructorField = new JTextField(15);
        JTextField roomField = new JTextField(15);

        JPanel form = new JPanel(new GridLayout(4, 2, 5, 5));
        form.add(new JLabel("Section Name:"));
        form.add(nameField);
        form.add(new JLabel("Lesson ID:"));
        form.add(lessonField);
        form.add(new JLabel("Instructor ID:"));
        form.add(instructorField);
        form.add(new JLabel("Room #:"));
        form.add(roomField);

        JButton createBtn = new JButton("Create");
        JButton cancelBtn = new JButton("Cancel");

        JPanel btnPanel = new JPanel();
        btnPanel.add(createBtn);
        btnPanel.add(cancelBtn);

        add(form, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        // Listeners
        createBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                int lessonId = Integer.parseInt(lessonField.getText().trim());
                int instructorId = Integer.parseInt(instructorField.getText().trim());
                int room = Integer.parseInt(roomField.getText().trim());

                listener.onCreate(name, lessonId, instructorId, room);
                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input.");
            }
        });

        cancelBtn.addActionListener(e -> dispose());

        setVisible(true);
    }
}
