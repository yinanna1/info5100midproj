package controller;

import dao.*;
import model.*;
import view.StudentDashboardUI;
import view.LibraryStudentUI;

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

    // NEW: for instructor name display
    private final InstructorDAO instructorDAO = new InstructorDAO();

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

        initData();

        // Lesson selection: reload available sections + detail
        ui.onLessonSelected(e -> {
            if (!e.getValueIsAdjusting()) {
                loadAvailableSections();
                ui.selectFirstAvailableSection();
                loadAvailableSectionDetail();
            }
        });

        // Available section selection: show details
        ui.onAvailableSectionSelected(e -> {
            if (!e.getValueIsAdjusting()) {
                loadAvailableSectionDetail();
            }
        });

        // My section selection: update bottom details
        ui.onMySectionSelected(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSectionDetail();
            }
        });

        ui.onAddSection(e -> addSection());
        ui.onDropSection(e -> dropSection());
        ui.onViewLibrary(e -> openLibrary());
    }

    private void initData() {
        List<Lesson> lessons = lessonDAO.getAllLessons();
        ui.setLessonList(lessons);

        loadMySections();

        if (!lessons.isEmpty()) {
            ui.selectFirstLesson();
            loadAvailableSections();
            ui.selectFirstAvailableSection();
            loadAvailableSectionDetail();
        } else {
            ui.setAvailableSections(List.of());
            ui.setAvailableSectionDetail("No lessons found → no available sections.");
        }

        ui.selectFirstMySection();
        loadSectionDetail();
    }

    private void loadMySections() {
        ui.setMySections(sectionDAO.getSectionsByStudentId(student.getStudentId()));
    }

    private void loadAvailableSections() {
        Lesson selected = ui.getSelectedLesson();
        if (selected == null) {
            ui.setAvailableSections(List.of());
            ui.setAvailableSectionDetail("No lesson selected.");
            return;
        }

        List<Section> allForLesson = sectionDAO.getSectionsByLesson(selected.getLessonId());
        List<Section> mySections = sectionDAO.getSectionsByStudentId(student.getStudentId());

        Set<Integer> myIds = new HashSet<>();
        for (Section s : mySections) myIds.add(s.getSectionId());

        List<Section> available = new ArrayList<>();
        for (Section s : allForLesson) {
            if (!myIds.contains(s.getSectionId())) available.add(s);
        }

        ui.setAvailableSections(available);

        if (available.isEmpty()) {
            ui.setAvailableSectionDetail("No available sections for this lesson.");
        }
    }

    private void loadAvailableSectionDetail() {
        Section s = ui.getSelectedAvailableSection();
        if (s == null) {
            ui.setAvailableSectionDetail("Select an available section to see details.");
            return;
        }

        Lesson lesson = lessonDAO.getLessonById(s.getLessonId());

        // NEW: instructor name
        Instructor inst = instructorDAO.getInstructor(s.getInstructorId());
        String instName = (inst != null && inst.getUserName() != null)
                ? inst.getUserName()
                : "Unknown";

        StringBuilder sb = new StringBuilder();
        sb.append("Section Info\n");
        sb.append("------------\n");
        sb.append("Section ID:    ").append(s.getSectionId()).append("\n");
        sb.append("Name:          ").append(s.getSectionName()).append("\n");
        sb.append("Room:          ").append(s.getRoom()).append("\n");
        sb.append("Lesson ID:     ").append(s.getLessonId()).append("\n");
        sb.append("Instructor ID: ").append(s.getInstructorId()).append("\n");
        sb.append("Instructor:    ").append(instName).append("\n\n");

        if (lesson != null) {
            sb.append("Lesson Info\n");
            sb.append("-----------\n");
            sb.append("Title:      ").append(lesson.getTitle()).append("\n");
            sb.append("Instrument: ").append(lesson.getInstrument()).append("\n");
            sb.append("Start:      ").append(lesson.getStartTime()).append("\n");
            sb.append("End:        ").append(lesson.getEndTime()).append("\n");
        }

        ui.setAvailableSectionDetail(sb.toString());
    }

    private void loadSectionDetail() {
        Section s = ui.getSelectedMySection();
        if (s == null) {
            ui.setSectionDetail("No section selected.");
            return;
        }

        Lesson lesson = lessonDAO.getLessonById(s.getLessonId());

        // NEW: instructor name
        Instructor inst = instructorDAO.getInstructor(s.getInstructorId());
        String instName = (inst != null && inst.getUserName() != null)
                ? inst.getUserName()
                : "Unknown";

        StringBuilder sb = new StringBuilder();
        sb.append("Section Info\n");
        sb.append("------------\n");
        sb.append("Section ID:    ").append(s.getSectionId()).append("\n");
        sb.append("Name:          ").append(s.getSectionName()).append("\n");
        sb.append("Room:          ").append(s.getRoom()).append("\n");
        sb.append("Lesson ID:     ").append(s.getLessonId()).append("\n");
        sb.append("Instructor ID: ").append(s.getInstructorId()).append("\n");
        sb.append("Instructor:    ").append(instName).append("\n\n");

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

    private void addSection() {
        Section s = ui.getSelectedAvailableSection();
        if (s == null) return;

        boolean ok = sectionStudentDAO.addStudentToSection(
                s.getSectionId(),
                student.getStudentId()
        );

        if (ok) {
            loadMySections();
            loadAvailableSections();
            ui.selectFirstMySection();
            loadSectionDetail();
            ui.selectFirstAvailableSection();
            loadAvailableSectionDetail();
        }
    }

    private void dropSection() {
        Section s = ui.getSelectedMySection();
        if (s == null) return;

        boolean ok = sectionStudentDAO.dropStudentFromSection(
                s.getSectionId(),
                student.getStudentId()
        );

        if (ok) {
            loadMySections();
            loadAvailableSections();
            ui.selectFirstMySection();
            loadSectionDetail();
            ui.selectFirstAvailableSection();
            loadAvailableSectionDetail();
        }
    }

    private void openLibrary() {
        LibraryItemDAO libraryDAO = new LibraryItemDAO();
        LibraryStudentUI libUI = new LibraryStudentUI(libraryDAO);
        libUI.setVisible(true);
    }
}
