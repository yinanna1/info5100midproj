package view;

import dao.*;
import model.Instructor;
import model.Section;
import model.Student;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InstructorDashboardUI extends JDialog {

    private final Instructor instructor;
    private final LessonDAO lessonDAO;
    private final SectionDAO sectionDAO;
    private final StudentDAO studentDAO;
    private final SectionStudentDAO sectionStudentDAO;

    private final DefaultListModel<Section> sectionsModel = new DefaultListModel<>();
    private final DefaultListModel<Student> studentsModel = new DefaultListModel<>();

    private final JList<Section> sectionsList = new JList<>(sectionsModel);
    private final JList<Student> studentsList = new JList<>(studentsModel);

    public InstructorDashboardUI(
            Instructor instructor,
            LessonDAO lessonDAO,
            SectionDAO sectionDAO,
            StudentDAO studentDAO,
            SectionStudentDAO sectionStudentDAO
    ) {
        super((Frame) null, "Instructor Dashboard", true);

        this.instructor = instructor;
        this.lessonDAO = lessonDAO;
        this.sectionDAO = sectionDAO;
        this.studentDAO = studentDAO;
        this.sectionStudentDAO = sectionStudentDAO;

        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(1, 2));

        JPanel left = new JPanel(new BorderLayout());
        left.add(new JLabel("Your Sections", SwingConstants.CENTER), BorderLayout.NORTH);
        left.add(new JScrollPane(sectionsList), BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout());
        right.add(new JLabel("Students in Selected Section", SwingConstants.CENTER), BorderLayout.NORTH);
        right.add(new JScrollPane(studentsList), BorderLayout.CENTER);

        panel.add(left);
        panel.add(right);

        add(panel, BorderLayout.CENTER);

        // Update students when a section is selected
        sectionsList.addListSelectionListener(e -> loadStudents());

        // Initial load
        loadSections();
        loadStudents();

        setVisible(true);
    }

    // ============================
    // LOAD SECTIONS FOR INSTRUCTOR
    // ============================
    private void loadSections() {
        sectionsModel.clear();
        List<Section> data =
                sectionDAO.getSectionsByInstructor(instructor.getInstructorId());

        data.forEach(sectionsModel::addElement);
    }

    // ============================
    // LOAD STUDENTS FOR SECTION
    // ============================
    private void loadStudents() {
        studentsModel.clear();

        Section selected = getSelectedSection();
        if (selected == null) return;

        List<Student> sList =
                sectionStudentDAO.getStudentsBySection(selected.getSectionId());

        sList.forEach(studentsModel::addElement);
    }

    public Section getSelectedSection() {
        return sectionsList.getSelectedValue();
    }
}
