package controller;

import dao.*;
import model.*;
import view.SectionDetailsView;

import javax.swing.*;
import java.util.List;

public class SectionDetailsController {

    private final LessonDAO lessonDAO;
    private final SectionDAO sectionDAO;
    private final InstructorDAO instructorDAO;
    private final SectionStudentDAO sectionStudentDAO;
    private final SectionDetailsView ui;

    public SectionDetailsController(
            LessonDAO lessonDAO,
            SectionDAO sectionDAO,
            InstructorDAO instructorDAO,
            SectionStudentDAO sectionStudentDAO,
            SectionDetailsView ui
    ) {
        this.lessonDAO = lessonDAO;
        this.sectionDAO = sectionDAO;
        this.instructorDAO = instructorDAO;
        this.sectionStudentDAO = sectionStudentDAO;
        this.ui = ui;

        init();
    }

    private void init() {
        ui.addSelectionListener(e -> loadDetails());
        ui.setSectionList(sectionDAO.getAllSections());
    }

    private void loadDetails() {

        Section section = ui.getSelectedSection();

        if (section == null) {
            ui.showCourseInfo("No course selected.");
            ui.showSectionInfo("No section selected.");
            ui.showInstructorInfo("No instructor selected.");
            ui.showStudentsInfo("No students.");
            return;
        }

        // ============================
        // 1) LOAD LESSON
        // ============================
        Lesson lesson = lessonDAO.getLessonById(section.getLessonId());
        if (lesson != null) {
            ui.showCourseInfo(
                    "Lesson ID: " + lesson.getLessonId() + "\n" +
                            "Title: " + lesson.getTitle() + "\n" +
                            "Instrument: " + lesson.getInstrument() + "\n" +
                            "Start Time: " + lesson.getStartTime() + "\n" +
                            "End Time: " + lesson.getEndTime() + "\n" +
                            "Description: " + lesson.getDescription()
            );
        } else {
            ui.showCourseInfo("Lesson not found.");
        }

        // ============================
        // 2) LOAD SECTION INFO
        // ============================
        ui.showSectionInfo(
                "Section ID: " + section.getSectionId() + "\n" +
                        "Section Name: " + section.getSectionName() + "\n" +
                        "Lesson ID: " + section.getLessonId() + "\n" +
                        "Instructor ID: " + section.getInstructorId()
        );

        // ============================
        // 3) LOAD INSTRUCTOR
        // ============================
        Instructor inst = instructorDAO.getInstructorById(section.getInstructorId());
        if (inst != null) {
            ui.showInstructorInfo(
                    "Instructor ID: " + inst.getInstructorId() + "\n" +
                            "User ID: " + inst.getUserId() + "\n" +
                            "Name: " + inst.getUserName()
            );
        } else {
            ui.showInstructorInfo("No instructor assigned.");
        }

        // ============================
        // 4) LOAD STUDENTS
        // ============================
        List<Student> students = sectionStudentDAO.getStudentsBySection(section.getSectionId());

        if (students == null || students.isEmpty()) {
            ui.showStudentsInfo("No students enrolled.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (Student s : students) {
            sb.append("Student ID: ").append(s.getStudentId())
                    .append(" | User ID: ").append(s.getUserId())
                    .append(" | Name: ").append(s.getUserName())
                    .append("\n");
        }

        ui.showStudentsInfo(sb.toString());
    }
}
