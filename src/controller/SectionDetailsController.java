package controller;

import dao.*;
import model.*;
import view.SectionDetailsView;
import java.util.List;

public class SectionDetailsController {

    private final SectionDetailsView view;
    private final SectionDAO sectionDAO;
    private final LessonDAO lessonDAO;
    private final InstructorDAO instructorDAO;
    private final SectionStudentDAO sectionStudentDAO;

    public SectionDetailsController(
            SectionDetailsView view,
            SectionDAO sectionDAO,
            LessonDAO lessonDAO,
            InstructorDAO instructorDAO,
            SectionStudentDAO sectionStudentDAO
    ) {
        this.view = view;
        this.sectionDAO = sectionDAO;
        this.lessonDAO = lessonDAO;
        this.instructorDAO = instructorDAO;
        this.sectionStudentDAO = sectionStudentDAO;

        view.setSectionList(sectionDAO.getAllSections());

        view.addSectionSelectListener(() -> updateDetails());
    }

    private void updateDetails() {
        Section section = view.getSelectedSection();
        if (section == null) return;

        // Section info
        view.showSectionInfo(section);

        // Course info
        Lesson lesson = lessonDAO.getLessonBySection(section.getSectionId());
        view.showCourseInfo(lesson);

        // Teacher info
        Instructor teacher = instructorDAO.getInstructorById(section.getInstructorId());
        view.showTeacherInfo(teacher);

        // Student list
        List<Student> students = sectionStudentDAO.getStudentsBySection(section.getSectionId());
        view.showStudentList(students);
    }
}

