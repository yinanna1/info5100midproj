import controller.MainController;
import controller.StudentDashboardController;
import dao.*;
import model.User;
import model.Student;
import model.Instructor;

import view.MainUI;
import view.UserLoginUI;
import view.StudentDashboardUI;
import view.InstructorDashboardUI;

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
            String role = loggedIn.getRole();
            if (role == null) {
                JOptionPane.showMessageDialog(null,
                        "Unknown role. Exiting.");
                System.exit(0);
            }

            // ADMIN → Main dashboard
            if (role.equalsIgnoreCase("admin")) {

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

            // STUDENT → Student dashboard
            if (role.equalsIgnoreCase("student")) {

                Student student = studentDAO.getStudentByUserId(loggedIn.getUserId());

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

                // ❗ now show it, after controller has populated the lists
                studentUI.setVisible(true);
                return;
            }


            // INSTRUCTOR → Instructor dashboard
            if (role.equalsIgnoreCase("instructor")) {

                Instructor instructor = instructorDAO.getInstructorByUserId(loggedIn.getUserId());

                new InstructorDashboardUI(
                        instructor,
                        lessonDAO,
                        sectionDAO,
                        studentDAO,
                        sectionStudentDAO
                );
                return;
            }

            // FALLBACK: unknown role
            JOptionPane.showMessageDialog(null,
                    "Unknown role: " + role + ". Exiting.");
            System.exit(0);
        });
    }
}

