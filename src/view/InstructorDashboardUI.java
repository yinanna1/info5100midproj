package view;

import dao.*;
import model.Instructor;
import model.Section;
import model.Student;
import model.Lesson;

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

    // New: details area for section + lesson info
    private final JTextArea infoArea = new JTextArea();

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

        // ============================
        // TOP: INSTRUCTOR INFO
        // ============================
        String name = instructor.getUserName() != null ? instructor.getUserName() : ("Instructor " + instructor.getInstructorId());
        JLabel header = new JLabel(
                "Instructor: " + name + " (ID: " + instructor.getInstructorId() + ")",
                SwingConstants.CENTER
        );
        add(header, BorderLayout.NORTH);

        // ============================
        // CENTER: SECTIONS & STUDENTS
        // ============================
        JPanel centerPanel = new JPanel(new GridLayout(1, 2));

        JPanel left = new JPanel(new BorderLayout());
        left.add(new JLabel("Your Sections", SwingConstants.CENTER), BorderLayout.NORTH);
        left.add(new JScrollPane(sectionsList), BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout());
        right.add(new JLabel("Students in Selected Section", SwingConstants.CENTER), BorderLayout.NORTH);
        right.add(new JScrollPane(studentsList), BorderLayout.CENTER);

        centerPanel.add(left);
        centerPanel.add(right);

        add(centerPanel, BorderLayout.CENTER);

        // ============================
        // BOTTOM: SECTION + LESSON INFO
        // ============================
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(new JLabel("Section & Lesson Info", SwingConstants.CENTER), BorderLayout.NORTH);
        bottom.add(new JScrollPane(infoArea), BorderLayout.CENTER);

        add(bottom, BorderLayout.SOUTH);

        // Update students + details when a section is selected
        sectionsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadStudents();
                loadSectionAndLessonInfo();
            }
        });

        // Initial load
        loadSections();
        loadStudents();
        loadSectionAndLessonInfo();

        // Optionally auto-select the first section if available
        if (!sectionsModel.isEmpty()) {
            sectionsList.setSelectedIndex(0);
        }

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

    // ============================
    // LOAD SECTION + LESSON INFO
    // ============================
    private void loadSectionAndLessonInfo() {
        Section selected = getSelectedSection();

        if (selected == null) {
            infoArea.setText("No section selected.");
            return;
        }

        Lesson lesson = lessonDAO.getLessonById(selected.getLessonId());

        StringBuilder sb = new StringBuilder();

        sb.append("Section Info\n");
        sb.append("------------\n");
        sb.append("Section ID: ").append(selected.getSectionId()).append("\n");
        sb.append("Name:      ").append(selected.getSectionName()).append("\n");
        sb.append("Room:      ").append(selected.getRoom()).append("\n");
        sb.append("Lesson ID: ").append(selected.getLessonId()).append("\n\n");

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

        sb.append("\nStudents enrolled: ").append(studentsModel.getSize());

        infoArea.setText(sb.toString());
    }

    public Section getSelectedSection() {
        return sectionsList.getSelectedValue();
    }
}
