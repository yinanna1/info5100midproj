package view;

import javax.swing.*;
import java.awt.*;

public class CreateCourseUI extends JDialog {

    private final JTextField titleField = new JTextField(15);
    private final JTextField instrumentField = new JTextField(15);
    private final JTextField startField = new JTextField(15);
    private final JTextField endField = new JTextField(15);
    private final JTextField descField = new JTextField(15);

    public CreateCourseUI(CreateCourseListener listener) {

        setTitle("Create Course");
        setModal(true);
        setSize(350, 250);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 2, 5, 5));

        add(new JLabel("Title:"));
        add(titleField);

        add(new JLabel("Instrument:"));
        add(instrumentField);

        add(new JLabel("Start Time:"));
        add(startField);

        add(new JLabel("End Time:"));
        add(endField);

        add(new JLabel("Description:"));
        add(descField);

        JButton createBtn = new JButton("Create");
        JButton cancelBtn = new JButton("Cancel");

        add(createBtn);
        add(cancelBtn);

        // ---- LISTENERS ----
        createBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            String instrument = instrumentField.getText().trim();
            String start = startField.getText().trim();
            String end = endField.getText().trim();
            String desc = descField.getText().trim();

            listener.onCreate(title, instrument, start, end, desc);
            dispose();
        });

        cancelBtn.addActionListener(e -> dispose());

        setVisible(true);
    }
}

