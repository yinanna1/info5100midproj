package controller;

import dao.*;
import model.*;
import view.StudentDashboardUI;

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
            StudentDAO studentDAO,          // not stored, but kept for signature consistency
            SectionStudentDAO sectionStudentDAO
    ) {
        this.student = student;
        this.ui = ui;
        this.lessonDAO = lessonDAO;
        this.sectionDAO = sectionDAO;
        this.sectionStudentDAO = sectionStudentDAO;

        // Initial data load
        initData();

        // Wire listeners
        ui.onLessonSelected(e -> {
            if (!e.getValueIsAdjusting()) {
                loadAvailableSections();
            }
        });
        ui.onAddSection(e -> addSection());
        ui.onDropSection(e -> dropSection());
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

        // Auto-select the first lesson (if any) and load available sections
        if (!lessons.isEmpty()) {
            ui.selectFirstLesson();
            loadAvailableSections();
        } else {
            ui.setAvailableSections(List.of());
        }
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
    // ADD SECTION
    // -----------------------------------------------------
    private void addSection() {
        Section s = ui.getSelectedAvailableSection();
        if (s == null) return;

        boolean ok = sectionStudentDAO.addStudentToSection(
                student.getStudentId(),
                s.getSectionId()
        );

        if (ok) {
            // refresh both lists
            loadMySections();
            loadAvailableSections();
        }
    }

    // -----------------------------------------------------
    // DROP SECTION
    // -----------------------------------------------------
    private void dropSection() {
        Section s = ui.getSelectedMySection();
        if (s == null) return;

        boolean ok = sectionStudentDAO.dropStudentFromSection(
                student.getStudentId(),
                s.getSectionId()
        );

        if (ok) {
            // refresh both lists
            loadMySections();
            loadAvailableSections();
        }
    }
}
