package controller;

import dao.*;
import model.*;
import view.StudentDashboardUI;
import view.LibraryStudentUI;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StudentDashboardController {

    private final Student student;
    private final StudentDashboardUI ui;
    private final LessonDAO lessonDAO;
    private final SectionDAO sectionDAO;
    private final SectionStudentDAO sectionStudentDAO;

    public StudentDashboardController(
            Student student,
            StudentDashboardUI ui,
            LessonDAO lessonDAO,
            SectionDAO sectionDAO,
            StudentDAO studentDAO,          // not stored, just for signature
            SectionStudentDAO sectionStudentDAO
    ) {
        this.student = student;
        this.ui = ui;
        this.lessonDAO = lessonDAO;
        this.sectionDAO = sectionDAO;
        this.sectionStudentDAO = sectionStudentDAO;

        // Initial data load
        initData();

        // When lesson changes, reload available sections
        ui.onLessonSelected(e -> {
            if (!e.getValueIsAdjusting()) {
                loadAvailableSections();
            }
        });

        // When My Sections selection changes, reload detail panel
        ui.onMySectionSelected(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSectionDetail();
            }
        });

        ui.onAddSection(e -> addSection());
        ui.onDropSection(e -> dropSection());

        // NEW: open library viewer
        ui.onViewLibrary(e -> openLibrary());
    }

    // -----------------------------------------------------
    // INITIAL DATA
    // -----------------------------------------------------
    private void initData() {
        // Load all lessons
        List<Lesson> lessons = lessonDAO.getAllLessons();
        ui.setLessonList(lessons);

        // Load student's sections
        loadMySections();

        // Auto-select first lesson and load available sections
        if (!lessons.isEmpty()) {
            ui.selectFirstLesson();
            loadAvailableSections();
        } else {
            ui.setAvailableSections(List.of());
        }

        // Auto-select first "My Section" and show details
        ui.selectFirstMySection();
        loadSectionDetail();
    }

    // -----------------------------------------------------
    // LOAD AVAILABLE SECTIONS FOR SELECTED LESSON
    // -----------------------------------------------------
    private void loadAvailableSections() {
        Lesson selected = ui.getSelectedLesson();
        if (selected == null) {
            ui.setAvailableSections(List.of());
            return;
        }

        // All sections for this lesson
        List<Section> allForLesson =
                sectionDAO.getSectionsByLesson(selected.getLessonId());

        // Sections the student is already enrolled in
        List<Section> mySections =
                sectionDAO.getSectionsByStudentId(student.getStudentId());

        // Build a set of section IDs the student already has
        Set<Integer> mySectionIds = new HashSet<>();
        for (Section s : mySections) {
            mySectionIds.add(s.getSectionId());
        }

        // Filter out sections the student is already in
        List<Section> available = new ArrayList<>();
        for (Section s : allForLesson) {
            if (!mySectionIds.contains(s.getSectionId())) {
                available.add(s);
            }
        }

        ui.setAvailableSections(available);
    }

    // -----------------------------------------------------
    // LOAD MY SECTIONS
    // -----------------------------------------------------
    private void loadMySections() {
        ui.setMySections(
                sectionDAO.getSectionsByStudentId(student.getStudentId())
        );
    }

    // -----------------------------------------------------
    // SHOW SECTION DETAIL (RIGHT SIDE)
    // -----------------------------------------------------
    private void loadSectionDetail() {
        Section s = ui.getSelectedMySection();
        if (s == null) {
            ui.setSectionDetail("No section selected.");
            return;
        }

        Lesson lesson = lessonDAO.getLessonById(s.getLessonId());

        StringBuilder sb = new StringBuilder();
        sb.append("Section Info\n");
        sb.append("------------\n");
        sb.append("Section ID: ").append(s.getSectionId()).append("\n");
        sb.append("Name:       ").append(s.getSectionName()).append("\n");
        sb.append("Room:       ").append(s.getRoom()).append("\n");
        sb.append("Lesson ID:  ").append(s.getLessonId()).append("\n\n");

        if (lesson != null) {
            sb.append("Lesson Info\n");
            sb.append("-----------\n");
            sb.append("Title:      ").append(lesson.getTitle()).append("\n");
            sb.append("Instrument: ").append(lesson.getInstrument()).append("\n");
            sb.append("Start:      ").append(lesson.getStartTime()).append("\n");
            sb.append("End:        ").append(lesson.getEndTime()).append("\n");
            sb.append("Description:\n").append(lesson.getDescription()).append("\n");
        } else {
            sb.append("No lesson details found for this section.\n");
        }

        ui.setSectionDetail(sb.toString());
    }

    // -----------------------------------------------------
    // ADD SECTION
    // -----------------------------------------------------
    private void addSection() {
        Section s = ui.getSelectedAvailableSection();
        if (s == null) {
            JOptionPane.showMessageDialog(ui,
                    "Please select a section to add.",
                    "No Section Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // ✅ CORRECT ORDER: (sectionId, studentId)
            boolean ok = sectionStudentDAO.addStudentToSection(
                    s.getSectionId(),
                    student.getStudentId()
            );

            if (!ok) {
                JOptionPane.showMessageDialog(ui,
                        "Could not enroll in this section.",
                        "Add Failed",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            loadMySections();
            loadAvailableSections();
            ui.selectFirstMySection();
            loadSectionDetail();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(ui,
                    "Error adding section: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // -----------------------------------------------------
    // DROP SECTION
    // -----------------------------------------------------
    private void dropSection() {
        Section s = ui.getSelectedMySection();
        if (s == null) {
            JOptionPane.showMessageDialog(ui,
                    "Please select a section to drop.",
                    "No Section Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                ui,
                "Are you sure you want to drop section: " + s.getSectionName() + "?",
                "Confirm Drop",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            // ✅ CORRECT ORDER: (sectionId, studentId)
            boolean ok = sectionStudentDAO.dropStudentFromSection(
                    s.getSectionId(),
                    student.getStudentId()
            );

            if (!ok) {
                JOptionPane.showMessageDialog(ui,
                        "Could not drop this section.",
                        "Drop Failed",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            loadMySections();
            loadAvailableSections();
            ui.selectFirstMySection();
            loadSectionDetail();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(ui,
                    "Error dropping section: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // -----------------------------------------------------
    // OPEN LIBRARY VIEWER (READ-ONLY)
    // -----------------------------------------------------
    private void openLibrary() {
        LibraryItemDAO libraryDAO = new LibraryItemDAO();
        LibraryStudentUI libUI = new LibraryStudentUI(libraryDAO);
        libUI.setVisible(true);
    }
}
