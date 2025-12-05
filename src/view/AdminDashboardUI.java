package view;

import dao.LessonDAO;
import dao.SectionDAO;

import javax.swing.*;
import java.awt.*;

public class AdminDashboardUI extends JDialog {

    private final LessonDAO lessonDAO;
    private final SectionDAO sectionDAO;
    private final Runnable refreshCallback;

    public AdminDashboardUI(LessonDAO lessonDAO, SectionDAO sectionDAO, Runnable refreshCallback) {
        super((Frame) null, "Admin Dashboard", true);
        this.lessonDAO = lessonDAO;
        this.sectionDAO = sectionDAO;
        this.refreshCallback = refreshCallback;

        setSize(450, 350);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1, 10, 10));

        JButton createCourseBtn = new JButton("Create Course");
        JButton deleteCourseBtn = new JButton("Delete Course");
        JButton createSectionBtn = new JButton("Create Section");
        JButton deleteSectionBtn = new JButton("Delete Section");

        add(createCourseBtn);
        add(deleteCourseBtn);
        add(createSectionBtn);
        add(deleteSectionBtn);

        // =====================================================
        // CREATE COURSE (ONE POPUP WITH ALL FIELDS)
        // =====================================================
        createCourseBtn.addActionListener(e -> {
            JTextField instructorF = new JTextField();
            JTextField titleF = new JTextField();
            JTextField instrumentF = new JTextField();
            JTextField startF = new JTextField();
            JTextField endF = new JTextField();
            JTextField descF = new JTextField();

            JPanel panel = new JPanel(new GridLayout(6, 2));
            panel.add(new JLabel("Instructor ID:"));
            panel.add(instructorF);

            panel.add(new JLabel("Title:"));
            panel.add(titleF);

            panel.add(new JLabel("Instrument:"));
            panel.add(instrumentF);

            panel.add(new JLabel("Start Time (yyyy-mm-dd HH:MM:SS):"));
            panel.add(startF);

            panel.add(new JLabel("End Time (yyyy-mm-dd HH:MM:SS):"));
            panel.add(endF);

            panel.add(new JLabel("Description:"));
            panel.add(descF);

            int ok = JOptionPane.showConfirmDialog(this, panel, "Create Course", JOptionPane.OK_CANCEL_OPTION);

            if (ok == JOptionPane.OK_OPTION) {
                try {
                    int instructorId = Integer.parseInt(instructorF.getText());
                    String title = titleF.getText();
                    String instrument = instrumentF.getText();
                    String start = startF.getText();
                    String end = endF.getText();
                    String desc = descF.getText();

                    int id = lessonDAO.createLesson(instructorId, title, instrument, start, end, desc);

                    if (id > 0) {
                        JOptionPane.showMessageDialog(this, "Course created!");
                        refreshCallback.run();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to create course.");
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input.");
                }
            }
        });

        // =====================================================
        // DELETE COURSE
        // =====================================================
        deleteCourseBtn.addActionListener(e -> {
            try {
                String input = JOptionPane.showInputDialog(this, "Enter Course (Lesson) ID to delete:");
                if (input == null) return;

                int id = Integer.parseInt(input);

                if (lessonDAO.deleteLesson(id)) {
                    JOptionPane.showMessageDialog(this, "Course deleted.");
                    refreshCallback.run();
                } else {
                    JOptionPane.showMessageDialog(this, "Delete failed.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID.");
            }
        });

        // =====================================================
        // CREATE SECTION
        // =====================================================
        createSectionBtn.addActionListener(e -> {
            JTextField nameF = new JTextField();
            JTextField lessonF = new JTextField();
            JTextField instructorF2 = new JTextField();
            JTextField roomF = new JTextField();

            JPanel panel = new JPanel(new GridLayout(4, 2));
            panel.add(new JLabel("Section Name:"));
            panel.add(nameF);

            panel.add(new JLabel("Lesson ID:"));
            panel.add(lessonF);

            panel.add(new JLabel("Instructor ID:"));
            panel.add(instructorF2);

            panel.add(new JLabel("Room:"));
            panel.add(roomF);

            int ok = JOptionPane.showConfirmDialog(this, panel, "Create Section", JOptionPane.OK_CANCEL_OPTION);

            if (ok == JOptionPane.OK_OPTION) {
                try {
                    String name = nameF.getText();
                    int lessonId = Integer.parseInt(lessonF.getText());
                    int instructorId = Integer.parseInt(instructorF2.getText());
                    int room = Integer.parseInt(roomF.getText());

                    int id = sectionDAO.createSection(name, lessonId, instructorId, room);

                    if (id > 0) {
                        JOptionPane.showMessageDialog(this, "Section created!");
                        refreshCallback.run();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to create section.");
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input.");
                }
            }
        });

        // =====================================================
        // DELETE SECTION
        // =====================================================
        deleteSectionBtn.addActionListener(e -> {
            try {
                String input = JOptionPane.showInputDialog(this, "Enter Section ID to delete:");
                if (input == null) return;

                int id = Integer.parseInt(input);

                if (sectionDAO.deleteSection(id)) {
                    JOptionPane.showMessageDialog(this, "Section deleted.");
                    refreshCallback.run();
                } else {
                    JOptionPane.showMessageDialog(this, "Delete failed.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID.");
            }
        });

        setVisible(true);
    }
}
