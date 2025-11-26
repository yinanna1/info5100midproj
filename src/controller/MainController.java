package controller;

import dao.*;
import model.*;
import view.MainUI;

import java.util.List;

public class MainController {

    private final LessonDAO lessonDAO;
    private final SectionDAO sectionDAO;
    private final InstructorDAO instructorDAO;
    private final StudentDAO studentDAO;
    private final SectionStudentDAO sectionStudentDAO;

    private final MainUI ui;

    public MainController(
            LessonDAO lessonDAO,
            SectionDAO sectionDAO,
            InstructorDAO instructorDAO,
            StudentDAO studentDAO,
            SectionStudentDAO sectionStudentDAO,
            MainUI ui
    ) {
        this.lessonDAO = lessonDAO;
        this.sectionDAO = sectionDAO;
        this.instructorDAO = instructorDAO;
        this.studentDAO = studentDAO;
        this.sectionStudentDAO = sectionStudentDAO;
        this.ui = ui;

        initListeners();
        loadInitialLists();
    }

    // ------------------------------------------------------------
    // Load initial data into all lists / combo
    // ------------------------------------------------------------
    private void loadInitialLists() {
        ui.setLessonList(lessonDAO.getAllLessons());
        ui.setInstructorList(instructorDAO.getAllInstructors());
        ui.setSectionList(sectionDAO.getAllSections());
        ui.setStudentList(studentDAO.getAllStudents());

        // For Tab 5: Section Detail
        ui.setSectionDetailSections(sectionDAO.getAllSections());
    }

    private void initListeners() {
        // Tabs 1-4
        ui.addLessonSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSectionsForLesson();
            }
        });
        ui.addInstructorSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSectionsForInstructor();
            }
        });
        ui.addSectionSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showStudentsForSection();
            }
        });
        ui.addStudentSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSectionsForStudent();
            }
        });

        // Tab 5: Section Detail
        ui.addSectionDetailSelectionListener(e -> showSectionFullDetail());
    }

    // ────────────────────────────────────────────────────────────────
    //  MASTER DETAIL 1:  Course (Lesson) → Sections
    // ────────────────────────────────────────────────────────────────
    private void showSectionsForLesson() {
        Lesson selected = ui.getSelectedLesson();
        if (selected == null) return;

        List<Section> sections = sectionDAO.getSectionsByLesson(selected.getLessonId());
        ui.setDetailList(sections, "Sections for Course: " + selected.getTitle());
    }

    // ────────────────────────────────────────────────────────────────
    //  MASTER DETAIL 2:  Teacher (Instructor) → Sections
    // ────────────────────────────────────────────────────────────────
    private void showSectionsForInstructor() {
        Instructor selected = ui.getSelectedInstructor();
        if (selected == null) return;

        List<Section> sections = sectionDAO.getSectionsByInstructor(selected.getInstructorId());
        ui.setDetailList(sections, "Sections taught by: " + selected.getUserName());
    }

    // ────────────────────────────────────────────────────────────────
    //  MASTER DETAIL 3:  Section → Students
    // ────────────────────────────────────────────────────────────────
    private void showStudentsForSection() {
        Section section = ui.getSelectedSection();
        if (section == null) return;

        List<Student> students = sectionStudentDAO.getStudentsBySection(section.getSectionId());
        ui.setDetailList(students, "Students in Section: " + section.getSectionName());
    }

    // ────────────────────────────────────────────────────────────────
    //  MASTER DETAIL 4:  Student → Sections
    // ────────────────────────────────────────────────────────────────
    private void showSectionsForStudent() {
        Student student = ui.getSelectedStudent();
        if (student == null) return;

        List<Section> sections = sectionStudentDAO.getSectionsByStudent(student.getStudentId());
        ui.setDetailList(sections, "Sections for Student: " + student.getUserName());
    }

    // ────────────────────────────────────────────────────────────────
    //  NEW: One view showing a+b+c+d for the selected Section
    // ────────────────────────────────────────────────────────────────
    private void showSectionFullDetail() {
        Section section = ui.getSelectedDetailSection();
        if (section == null) return;

        // a. Course (lesson) information of the section
        Lesson lesson = lessonDAO.getLessonById(section.getLessonId());

        // c. Teacher information
        Instructor instructor = instructorDAO.getInstructorById(section.getInstructorId());

        // d. Students registered in this section
        List<Student> students = sectionStudentDAO.getStudentsBySection(section.getSectionId());

        // b. Section info is already in the Section object
        ui.showSectionDetail(lesson, section, instructor, students);
    }
}
