package controller;

import dao.*;
import model.*;
import view.MainUI;
import view.AdminDashboardUI;
import view.InstructorDashboardUI;
import view.StudentDashboardUI;

import javax.swing.*;
import java.util.ArrayList;
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
    // TAB 2: Instructor → Sections
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

        // Use SectionDAO.getSectionsByStudentId(...)
        List<Section> sections = sectionDAO.getSectionsByStudentId(student.getStudentId());
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
    // HELPERS FOR STUDENT DASHBOARD
    // ===============================================================

    /**
     * For the currently selected lesson in the given StudentDashboardUI,
     * recompute:
     *   - My Sections (for that lesson only)
     *   - Available Sections (sections of that lesson the student is NOT in)
     */
    private void updateStudentSectionsForCurrentLesson(StudentDashboardUI sUI, Student student) {
        Lesson currentLesson = sUI.getSelectedLesson();

        if (currentLesson == null) {
            // No lesson selected: show all student's sections in "My Sections"
            List<Section> myAll = sectionDAO.getSectionsByStudentId(student.getStudentId());
            sUI.setMySections(myAll);
            sUI.setAvailableSections(List.of());
            sUI.setSectionDetail("");
            return;
        }

        int lessonId = currentLesson.getLessonId();

        // All sections that belong to this lesson
        List<Section> allForLesson = sectionDAO.getSectionsByLesson(lessonId);

        // All sections this student is enrolled in (for any lesson)
        List<Section> myAllSections = sectionDAO.getSectionsByStudentId(student.getStudentId());

        // My sections for THIS lesson
        List<Section> myForLesson = new ArrayList<>();
        for (Section s : myAllSections) {
            if (s.getLessonId() == lessonId) {
                myForLesson.add(s);
            }
        }

        // Available sections = allForLesson - myForLesson
        List<Section> available = new ArrayList<>();
        for (Section sec : allForLesson) {
            boolean enrolled = false;
            for (Section mine : myForLesson) {
                if (mine.getSectionId() == sec.getSectionId()) {
                    enrolled = true;
                    break;
                }
            }
            if (!enrolled) {
                available.add(sec);
            }
        }

        sUI.setMySections(myForLesson);
        sUI.setAvailableSections(available);
        sUI.setSectionDetail("");
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

                InstructorDashboardUI instUI = new InstructorDashboardUI(
                        instructor,
                        lessonDAO,
                        sectionDAO,
                        studentDAO,
                        sectionStudentDAO
                );
                instUI.setVisible(true);
            }

            case "student" -> {
                Student student = studentDAO.getStudentByUserId(loggedIn.getUserId());

                // Create the Student Dashboard
                StudentDashboardUI sUI = new StudentDashboardUI(
                        student,
                        lessonDAO,
                        sectionDAO,
                        studentDAO,
                        sectionStudentDAO
                );

                // Load lessons list
                List<Lesson> lessons = lessonDAO.getAllLessons();
                sUI.setLessonList(lessons);

                // Auto-select first lesson (if any) and compute sections
                sUI.selectFirstLesson();
                updateStudentSectionsForCurrentLesson(sUI, student);

                // When the student changes the selected lesson, update lists
                sUI.onLessonSelected(e -> {
                    if (!e.getValueIsAdjusting()) {
                        updateStudentSectionsForCurrentLesson(sUI, student);
                    }
                });

                // Wire Drop Section button
                sUI.onDropSection(e -> {
                    Section selected = sUI.getSelectedMySection();
                    if (selected == null) {
                        JOptionPane.showMessageDialog(sUI,
                                "Please select a section to drop.",
                                "No Section Selected",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    int confirm = JOptionPane.showConfirmDialog(
                            sUI,
                            "Are you sure you want to drop section: " + selected.getSectionName() + "?",
                            "Confirm Drop",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (confirm != JOptionPane.YES_OPTION) {
                        return;
                    }

                    // DAO method signature is (sectionId, studentId)
                    boolean ok = sectionStudentDAO.dropStudentFromSection(
                            selected.getSectionId(),
                            student.getStudentId()
                    );

                    if (!ok) {
                        JOptionPane.showMessageDialog(sUI,
                                "Failed to drop this section (you may not be enrolled, or an error occurred).",
                                "Drop Failed",
                                JOptionPane.ERROR_MESSAGE);
                    }

                    // Recompute "My Sections" and "Available Sections"
                    updateStudentSectionsForCurrentLesson(sUI, student);
                });

                // Show the dashboard
                sUI.setVisible(true);
            }

            default -> JOptionPane.showMessageDialog(null, "Unknown role.");
        }
    }
}
