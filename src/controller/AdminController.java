package controller;

import dao.AdminDAO;
import dao.*;
import view.*;

import javax.swing.*;

public class AdminController {

    private final AdminLoginUI loginUI;
    private final AdminDashboardUI dashboardUI;
    private final AdminDAO adminDAO;

    private final LessonDAO lessonDAO;
    private final SectionDAO sectionDAO;
    private final InstructorDAO instructorDAO;
    private final StudentDAO studentDAO;

    public AdminController() {

        loginUI = new AdminLoginUI();
        dashboardUI = new AdminDashboardUI();

        adminDAO = new AdminDAO();
        lessonDAO = new LessonDAO();
        sectionDAO = new SectionDAO();
        instructorDAO = new InstructorDAO();
        studentDAO = new StudentDAO();

        initLoginListener();
        loginUI.setVisible(true);
    }

    private void initLoginListener() {
        loginUI.getLoginButton().addActionListener(e -> doLogin());
    }

    private void doLogin() {
        String user = loginUI.getUsername();
        String pass = loginUI.getPassword();

        if (adminDAO.validateAdmin(user, pass)) {
            JOptionPane.showMessageDialog(loginUI, "Login Successful!");
            loginUI.dispose();
            loadDashboard();
            dashboardUI.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(loginUI, "Invalid Credentials!");
        }
    }

    private void loadDashboard() {
        dashboardUI.lessonList.setListData(lessonDAO.getAllLessons().toArray());
        dashboardUI.sectionList.setListData(sectionDAO.getAllSections().toArray());
        dashboardUI.instructorList.setListData(instructorDAO.getAllInstructors().toArray());
        dashboardUI.studentList.setListData(studentDAO.getAllStudents().toArray());
    }
}

