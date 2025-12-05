package controller;

import dao.*;
import model.*;
import view.MainUI;
import view.AdminDashboardUI;
import view.InstructorDashboardUI;
import view.StudentDashboardUI;

import javax.swing.*;
import java.util.List;

public class MainController {

    private final LessonDAO lessonDAO;
    private final SectionDAO sectionDAO;
    private final InstructorDAO instructorDAO;
    private final StudentDAO studentDAO;
    private final SectionStudentDAO sectionStudentDAO;
    private final UserDAO userDAO;

    private final MainUI ui;

    // ===============================================================
    // CONSTRUCTOR
    // ===============================================================
    public MainController(
            LessonDAO lessonDAO,
            SectionDAO sectionDAO,
            InstructorDAO instructorDAO,
            StudentDAO studentDAO,
            SectionStudentDAO sectionStudentDAO,
            UserDAO userDAO,
            MainUI ui
    ) {
        this.lessonDAO = lessonDAO;
        this.sectionDAO = sectionDAO;
        this.instructorDAO = instructorDAO;
        this.studentDAO = studentDAO;
        this.sectionStudentDAO = sectionStudentDAO;
        this.userDAO = userDAO;
        this.ui = ui;

        initMainUI();
    }

    // ===============================================================
    // INITIALIZE MAIN UI LISTENERS
    // ===============================================================
    private void initMainUI() {

        refreshAllLists();

        ui.addLessonSelectionListener(e -> loadSectionsForSelectedLesson());
        ui.addInstructorSelectionListener(e -> loadSectionsForSelectedInstructor());
        ui.addSectionSelectionListener(e -> loadStudentsForSelectedSection());
        ui.addStudentSelectionListener(e -> loadSectionsForSelectedStudent());
        ui.addSectionDetailSelectionListener(e -> loadSectionDetail());

        ui.editButton.addActionListener(e -> openAdminDashboard());
    }

    // ===============================================================
    // REFRESH ALL LISTS
    // ===============================================================
    private void refreshAllLists() {
        ui.setLessonList(lessonDAO.getAllLessons());
        ui.setInstructorList(instructorDAO.getAllInstructors());
        ui.setSectionList(sectionDAO.getAllSections());
        ui.setStudentList(studentDAO.getAllStudents());
        ui.setSectionDetailSections(sectionDAO.getAllSections());
    }

    // ===============================================================
    // TAB 1: Course → Sections
    // ===============================================================
    private void loadSectionsForSelectedLesson() {
        Lesson lesson = ui.getSelectedLesson();
        if (lesson == null) {
            ui.setDetailList(List.of(), "No course selected");
            return;
        }

        List<Section> sections = sectionDAO.getSectionsByLesson(lesson.getLessonId());
        ui.setDetailList(sections, "Sections for: " + lesson.getTitle());
    }

    // ===============================================================
    // TAB 2: Teacher → Sections
    // ===============================================================
    private void loadSectionsForSelectedInstructor() {
        Instructor inst = ui.getSelectedInstructor();
        if (inst == null) {
            ui.setDetailList(List.of(), "No instructor selected");
            return;
        }

        List<Section> sections = sectionDAO.getSectionsByInstructor(inst.getInstructorId());
        ui.setDetailList(sections, "Sections taught by: " + inst.getUserName());
    }

    // ===============================================================
    // TAB 3: Section → Students
    // ===============================================================
    private void loadStudentsForSelectedSection() {
        Section sec = ui.getSelectedSection();
        if (sec == null) {
            ui.setDetailList(List.of(), "No section selected");
            return;
        }

        List<Student> students = sectionStudentDAO.getStudentsBySection(sec.getSectionId());
        ui.setDetailList(students, "Students for section: " + sec.getSectionName());
    }

    // ===============================================================
    // TAB 4: Student → Sections
    // ===============================================================
    private void loadSectionsForSelectedStudent() {
        Student student = ui.getSelectedStudent();
        if (student == null) {
            ui.setDetailList(List.of(), "No student selected");
            return;
        }

        List<Section> sections = sectionStudentDAO.getSectionsByStudent(student.getStudentId());
        ui.setDetailList(sections, "Sections for student: " + student.getUserName());
    }

    // ===============================================================
    // TAB 5: Section Detail
    // ===============================================================
    private void loadSectionDetail() {
        Section sec = ui.getSelectedDetailSection();
        if (sec == null) return;

        Lesson lesson = lessonDAO.getLessonById(sec.getLessonId());
        Instructor teacher = instructorDAO.getInstructor(sec.getInstructorId());
        List<Student> students = sectionStudentDAO.getStudentsBySection(sec.getSectionId());

        ui.showSectionDetail(lesson, sec, teacher, students);
    }

    // ===============================================================
    // ADMIN PANEL
    // ===============================================================
    private void openAdminDashboard() {
        AdminDashboardUI adminUI = new AdminDashboardUI(
                lessonDAO,
                sectionDAO,
                this::refreshAllLists
        );
        adminUI.setVisible(true);
    }

    // ===============================================================
    // LOGIN HANDLER (NOW TAKES User OBJECT)
    // ===============================================================
    public void handleLogin(User loggedIn) {

        if (loggedIn == null) {
            JOptionPane.showMessageDialog(null, "Invalid login.");
            return;
        }

        String role = loggedIn.getRole().toLowerCase();

        switch (role) {

            case "admin" -> openAdminDashboard();

            case "instructor" -> {
                Instructor instructor =
                        instructorDAO.getInstructorByUserId(loggedIn.getUserId());

                new InstructorDashboardUI(
                        instructor,
                        lessonDAO,
                        sectionDAO,
                        studentDAO,
                        sectionStudentDAO
                );
            }

            case "student" -> {
                Student student = studentDAO.getStudentByUserId(loggedIn.getUserId());

                new StudentDashboardUI(
                        student,
                        lessonDAO,
                        sectionDAO,
                        studentDAO,
                        sectionStudentDAO
                );
            }

            default -> JOptionPane.showMessageDialog(null, "Unknown role.");
        }
    }
}
