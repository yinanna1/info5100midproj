package view;

import javax.swing.*;
import java.awt.*;

public class DeleteCourseUI extends JDialog {

    public interface DeleteCourseListener {
        void onDelete(int lessonId);
    }

    public DeleteCourseUI(DeleteCourseListener listener) {
        setTitle("Delete Course");
        setSize(350, 150);
        setModal(true);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JTextField idField = new JTextField(10);

        JPanel form = new JPanel(new GridLayout(1, 2, 5, 5));
        form.add(new JLabel("Lesson ID:"));
        form.add(idField);

        JButton deleteBtn = new JButton("Delete");
        JButton cancelBtn = new JButton("Cancel");

        JPanel btnPanel = new JPanel();
        btnPanel.add(deleteBtn);
        btnPanel.add(cancelBtn);

        add(form, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        // Listeners
        deleteBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                listener.onDelete(id);
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid lesson ID.");
            }
        });

        cancelBtn.addActionListener(e -> dispose());

        setVisible(true);
    }
}
