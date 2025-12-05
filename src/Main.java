import controller.MainController;
import controller.StudentDashboardController;
import dao.*;
import model.User;
import model.Student;

import view.MainUI;
import view.UserLoginUI;
import view.StudentDashboardUI;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ JDBC Driver Loaded");
        } catch (Exception e) {
            System.out.println("❌ JDBC Driver missing");
            return;
        }

        SwingUtilities.invokeLater(() -> {

            // -----------------------------------
            // LOGIN FIRST
            // -----------------------------------
            UserLoginUI loginUI = new UserLoginUI();
            loginUI.setVisible(true);

            loginUI.waitForLogin();

            User loggedIn = loginUI.getLoggedInUser();

            if (loggedIn == null) {
                JOptionPane.showMessageDialog(null,
                        "Login cancelled. Exiting.");
                System.exit(0);
            }

            // -----------------------------------
            // DAO INITIALIZATION
            // -----------------------------------
            LessonDAO lessonDAO = new LessonDAO();
            SectionDAO sectionDAO = new SectionDAO();
            InstructorDAO instructorDAO = new InstructorDAO();
            StudentDAO studentDAO = new StudentDAO();
            SectionStudentDAO sectionStudentDAO = new SectionStudentDAO();
            UserDAO userDAO = new UserDAO();

            // -----------------------------------
            // ROLE ROUTING
            // -----------------------------------

            if (loggedIn.getRole().equalsIgnoreCase("admin")) {

                MainUI ui = new MainUI();

                new MainController(
                        lessonDAO,
                        sectionDAO,
                        instructorDAO,
                        studentDAO,
                        sectionStudentDAO,
                        userDAO,
                        ui
                );

                ui.setVisible(true);
                return;
            }

            if (loggedIn.getRole().equalsIgnoreCase("student")) {

                Student student = studentDAO.getStudentByUserId(loggedIn.getUserId());

                if (student == null) {
                    JOptionPane.showMessageDialog(null, "No student record found.");
                    return;
                }

                StudentDashboardUI studentUI = new StudentDashboardUI(
                        student,
                        lessonDAO,
                        sectionDAO,
                        studentDAO,
                        sectionStudentDAO
                );

                new StudentDashboardController(
                        student,
                        studentUI,
                        lessonDAO,
                        sectionDAO,
                        studentDAO,
                        sectionStudentDAO
                );

                return;
            }

            if (loggedIn.getRole().equalsIgnoreCase("instructor")) {

                JOptionPane.showMessageDialog(
                        null,
                        "Instructor Dashboard not implemented yet."
                );

                return;
            }

            JOptionPane.showMessageDialog(null,
                    "Unknown role: " + loggedIn.getRole());
            System.exit(0);
        });
    }
}
