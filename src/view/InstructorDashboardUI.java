package view;

import dao.*;
import model.Instructor;
import model.Section;
import model.Student;
import model.Lesson;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    // details area for section + lesson info
    private final JTextArea infoArea = new JTextArea();

    // clock
    private final JLabel clockLabel = new JLabel();
    private final javax.swing.Timer clockTimer;

    public InstructorDashboardUI(
            Instructor instructor,
            LessonDAO lessonDAO,
            SectionDAO sectionDAO,
            StudentDAO studentDAO,
            SectionStudentDAO sectionStudentDAO
    ) {
        super((Frame) null, "Instructor Dashboard", false);

        this.instructor = instructor;
        this.lessonDAO = lessonDAO;
        this.sectionDAO = sectionDAO;
        this.studentDAO = studentDAO;
        this.sectionStudentDAO = sectionStudentDAO;

        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // when user closes this window, exit whole program
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                clockTimer.stop();
                System.exit(0);
            }
        });

        // ============================
        // TOP: INSTRUCTOR INFO + CLOCK (navy bar)
        // ============================
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(27, 28, 70));

        String name = instructor.getUserName() != null
                ? instructor.getUserName()
                : ("Instructor " + instructor.getInstructorId());

        JLabel header = new JLabel(
                "  Instructor: " + name + " (ID: " + instructor.getInstructorId() + ")"
        );
        header.setForeground(Color.WHITE);

        clockLabel.setForeground(Color.WHITE);
        clockLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        clockLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        topBar.add(header, BorderLayout.WEST);
        topBar.add(clockLabel, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

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
        // BOTTOM: SECTION + LESSON INFO + VIEW LIBRARY
        // ============================
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);

        JPanel bottom = new JPanel(new BorderLayout());

        // Top row: label + "View Library" button
        JPanel bottomTop = new JPanel(new BorderLayout());
        bottomTop.add(new JLabel("Section & Lesson Info", SwingConstants.CENTER), BorderLayout.CENTER);

        JButton viewLibraryBtn = new JButton("View Library");
        bottomTop.add(viewLibraryBtn, BorderLayout.EAST);

        bottom.add(bottomTop, BorderLayout.NORTH);
        bottom.add(new JScrollPane(infoArea), BorderLayout.CENTER);

        add(bottom, BorderLayout.SOUTH);

        // Library popup: modal and centered over this dashboard
        viewLibraryBtn.addActionListener(e -> {
            LibraryItemDAO libraryDAO = new LibraryItemDAO();
            LibraryStudentUI libUI = new LibraryStudentUI(libraryDAO);
            libUI.setModal(true);
            libUI.setLocationRelativeTo(InstructorDashboardUI.this);
            libUI.toFront();
            libUI.requestFocus();
            libUI.setVisible(true);
        });

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

        // Auto-select the first section if available
        if (!sectionsModel.isEmpty()) {
            sectionsList.setSelectedIndex(0);
        }

        // start clock (after fields are initialized)
        clockTimer = new javax.swing.Timer(1000, e -> updateClock());
        clockTimer.start();
        updateClock();

        setVisible(true);
    }

    // ============================
    // CLOCK
    // ============================
    private void updateClock() {
        LocalDateTime now = LocalDateTime.now();
        String text = now.format(DateTimeFormatter.ofPattern("EEE, MMM d yyyy  HH:mm:ss"));
        clockLabel.setText(text);
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
