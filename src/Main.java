import controller.MainController;
import controller.SectionDetailsController;
import controller.AdminController;

import view.MainUI;
import view.SectionDetailsView;

import dao.*;

public class Main {
    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ JDBC Driver Loaded");
        } catch (Exception e) {
            System.out.println("❌ JDBC Driver missing");
            return;
        }

        javax.swing.SwingUtilities.invokeLater(() -> {

            // ─────────────────────────────────────────────
            // 0) ADMIN LOGIN + ADMIN DASHBOARD
            // (This opens FIRST; from there admin controls the system)
            // ─────────────────────────────────────────────
            new AdminController();
            // (Admin login window shows immediately)

            // ─────────────────────────────────────────────
            // 1) MASTER–DETAIL UI (4 tabs)
            // ─────────────────────────────────────────────
            MainUI ui = new MainUI();

            LessonDAO lessonDAO = new LessonDAO();
            SectionDAO sectionDAO = new SectionDAO();
            InstructorDAO instructorDAO = new InstructorDAO();
            StudentDAO studentDAO = new StudentDAO();
            SectionStudentDAO sectionStudentDAO = new SectionStudentDAO();

            new MainController(
                    lessonDAO,
                    sectionDAO,
                    instructorDAO,
                    studentDAO,
                    sectionStudentDAO,
                    ui
            );

            // ─────────────────────────────────────────────
            // 2) SECTION DETAIL VIEW (Part 2 requirement)
            // ─────────────────────────────────────────────
            new SectionDetailsController(
                    new SectionDetailsView(),
                    sectionDAO,
                    lessonDAO,
                    instructorDAO,
                    sectionStudentDAO
            );
        });
    }
}
