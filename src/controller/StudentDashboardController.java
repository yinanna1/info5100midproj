package controller;

import dao.LessonDAO;
import dao.SectionDAO;
import dao.StudentDAO;
import dao.SectionStudentDAO;

import model.Student;
import model.Lesson;
import model.Section;

import view.StudentDashboardUI;

import java.util.List;

public class StudentDashboardController {

    private final Student student;
    private final StudentDashboardUI ui;

    private final LessonDAO lessonDAO;
    private final SectionDAO sectionDAO;
    private final StudentDAO studentDAO;
    private final SectionStudentDAO sectionStudentDAO;

    // ======================================================
    // CONSTRUCTOR — MUST MATCH 6 ARGUMENTS FROM MainController
    // ======================================================
    public StudentDashboardController(
            Student student,
            StudentDashboardUI ui,
            LessonDAO lessonDAO,
            SectionDAO sectionDAO,
            StudentDAO studentDAO,
            SectionStudentDAO sectionStudentDAO
    ) {
        this.student = student;
        this.ui = ui;
        this.lessonDAO = lessonDAO;
        this.sectionDAO = sectionDAO;
        this.studentDAO = studentDAO;
        this.sectionStudentDAO = sectionStudentDAO;

        loadLessons();
        loadMySections();

        ui.onLessonSelected(e -> loadSectionsForSelectedLesson());
        ui.onAddSection(e -> addSection());
        ui.onDropSection(e -> dropSection());
    }

    // ======================================================
    // LOAD ALL LESSONS TO CHOOSE FROM
    // ======================================================
    private void loadLessons() {
        List<Lesson> lessons = lessonDAO.getAllLessons();
        ui.setLessonList(lessons);
    }

    // ======================================================
    // LOAD SECTIONS FOR A SELECTED LESSON
    // ======================================================
    private void loadSectionsForSelectedLesson() {
        Lesson lesson = ui.getSelectedLesson();
        if (lesson == null) {
            ui.setAvailableSections(List.of());
            return;
        }

        List<Section> sections = sectionDAO.getSectionsByLesson(lesson.getLessonId());
        ui.setAvailableSections(sections);
    }

    // ======================================================
    // ADD A SECTION (ENROLL STUDENT)
    // ======================================================
    private void addSection() {
        Section section = ui.getSelectedAvailableSection();
        if (section == null) return;

        sectionStudentDAO.addStudentToSection(student.getStudentId(), section.getSectionId());
        loadMySections();
    }

    // ======================================================
    // REMOVE A SECTION (DROP STUDENT)
    // ======================================================
    private void dropSection() {
        Section section = ui.getSelectedMySection();
        if (section == null) return;

        sectionStudentDAO.removeStudentFromSection(student.getStudentId(), section.getSectionId());
        loadMySections();
    }

    // ======================================================
    // LOAD CURRENTLY ENROLLED SECTIONS
    // ======================================================
    private void loadMySections() {
        List<Section> mySections =
                sectionStudentDAO.getSectionsByStudent(student.getStudentId());

        ui.setMySections(mySections);
    }
}
