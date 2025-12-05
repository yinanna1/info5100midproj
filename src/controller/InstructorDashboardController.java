package controller;

import dao.*;
import model.Section;
import model.Student;
import model.Instructor;
import view.InstructorDashboardUI;

import java.util.List;

public class InstructorDashboardController {

    private final LessonDAO lessonDAO;
    private final SectionDAO sectionDAO;
    private final StudentDAO studentDAO;
    private final SectionStudentDAO sectionStudentDAO;
    private final Instructor instructor;
    private final InstructorDashboardUI ui;

    public InstructorDashboardController(
            Instructor instructor,
            LessonDAO lessonDAO,
            SectionDAO sectionDAO,
            StudentDAO studentDAO,
            SectionStudentDAO sectionStudentDAO
    ) {
        this.instructor = instructor;
        this.lessonDAO = lessonDAO;
        this.sectionDAO = sectionDAO;
        this.studentDAO = studentDAO;
        this.sectionStudentDAO = sectionStudentDAO;

        // Constructor now requires all arguments
        this.ui = new InstructorDashboardUI(
                instructor,
                lessonDAO,
                sectionDAO,
                studentDAO,
                sectionStudentDAO
        );

        // UI automatically loads sections + students
    }
}
