package view;

import model.*;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainUI extends JFrame {

    private final JTabbedPane tabbedPane = new JTabbedPane();

    // === Admin Button ===
    public JButton editButton = new JButton("Edit");

    // clock
    private final JLabel clockLabel = new JLabel();
    private javax.swing.Timer clockTimer;

    // ------------------------------------------------------------
    // TAB 1: Course → Sections
    // ------------------------------------------------------------
    private final JList<Lesson> courseLessonList = new JList<>(new DefaultListModel<>());
    private final JList<Object> courseSectionList = new JList<>(new DefaultListModel<>());
    private final JLabel courseDetailLabel = new JLabel("Sections for Selected Course");

    // ------------------------------------------------------------
    // TAB 2: Teacher → Sections
    // ------------------------------------------------------------
    private final JList<Instructor> teacherList = new JList<>(new DefaultListModel<>());
    private final JList<Object> teacherSectionList = new JList<>(new DefaultListModel<>());
    private final JLabel teacherDetailLabel = new JLabel("Sections for Selected Teacher");

    // ------------------------------------------------------------
    // TAB 3: Section → Students
    // ------------------------------------------------------------
    private final JList<Section> sectionList = new JList<>(new DefaultListModel<>());
    private final JList<Object> sectionStudentList = new JList<>(new DefaultListModel<>());
    private final JLabel sectionDetailLabel = new JLabel("Students for Selected Section");

    // ------------------------------------------------------------
    // TAB 4: Student → Sections
    // ------------------------------------------------------------
    private final JList<Student> studentList = new JList<>(new DefaultListModel<>());
    private final JList<Object> studentSectionList = new JList<>(new DefaultListModel<>());
    private final JLabel studentDetailLabel = new JLabel("Sections for Selected Student");

    // ------------------------------------------------------------
    // TAB 5: Section Detail
    // ------------------------------------------------------------
    private final JComboBox<Section> detailSectionCombo = new JComboBox<>();
    private final JTextArea courseInfoArea = new JTextArea();
    private final JTextArea sectionInfoArea = new JTextArea();
    private final JTextArea teacherInfoArea = new JTextArea();
    private final JTextArea studentsInfoArea = new JTextArea();

    // ------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------
    public MainUI() {
        setTitle("EMS Master–Detail Views");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        buildUI();
        startClock();

        // just in case, stop timer when closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (clockTimer != null) clockTimer.stop();
            }
        });

        setVisible(true);
    }

    // ------------------------------------------------------------
    // Build UI
    // ------------------------------------------------------------
    private void buildUI() {

        // Top bar with title, clock, Edit button
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(27, 28, 70)); // navy

        JLabel titleLabel = new JLabel("  EMS Master–Detail Views");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 15));

        clockLabel.setForeground(Color.WHITE);
        clockLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        clockLabel.setBorder(new EmptyBorder(0, 0, 0, 10));

        // right side: clock + Edit button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 3));
        rightPanel.setOpaque(false);
        rightPanel.add(clockLabel);
        rightPanel.add(editButton);

        topBar.add(titleLabel, BorderLayout.WEST);
        topBar.add(rightPanel, BorderLayout.EAST);

        buildTabs();

        JPanel container = new JPanel(new BorderLayout());
        container.add(topBar, BorderLayout.NORTH);
        container.add(tabbedPane, BorderLayout.CENTER);
        setContentPane(container);
    }

    private void startClock() {
        clockTimer = new javax.swing.Timer(1000, e -> updateClock());
        clockTimer.start();
        updateClock();
    }

    private void updateClock() {
        LocalDateTime now = LocalDateTime.now();
        clockLabel.setText(
                now.format(DateTimeFormatter.ofPattern("EEE, MMM d yyyy  HH:mm:ss"))
        );
    }

    // ------------------------------------------------------------
    // Tabs
    // ------------------------------------------------------------
    private void buildTabs() {
        tabbedPane.addTab("Course → Sections", buildCourseTab());
        tabbedPane.addTab("Teacher → Sections", buildTeacherTab());
        tabbedPane.addTab("Section → Students", buildSectionTab());
        tabbedPane.addTab("Student → Sections", buildStudentTab());
        tabbedPane.addTab("Section Detail (All Info)", buildSectionDetailTab());
    }

    private JPanel buildCourseTab() {
        return splitPanel("Courses", courseLessonList, courseDetailLabel, courseSectionList);
    }

    private JPanel buildTeacherTab() {
        return splitPanel("Teachers", teacherList, teacherDetailLabel, teacherSectionList);
    }

    private JPanel buildSectionTab() {
        return splitPanel("Sections", sectionList, sectionDetailLabel, sectionStudentList);
    }

    private JPanel buildStudentTab() {
        return splitPanel("Students", studentList, studentDetailLabel, studentSectionList);
    }

    private JPanel splitPanel(String listName, JList<?> leftList, JLabel rightLabel, JList<?> rightList) {
        JPanel panel = new JPanel(new BorderLayout());

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                wrapListWithTitle(listName, leftList),
                wrapListWithTitle(rightLabel, rightList));

        split.setResizeWeight(0.5);
        panel.add(split);
        return panel;
    }

    // ------------------------------------------------------------
    // Section Detail Tab
    // ------------------------------------------------------------
    private JPanel buildSectionDetailTab() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Select Section:"));
        detailSectionCombo.setPreferredSize(new Dimension(300, 25));
        top.add(detailSectionCombo);

        panel.add(top, BorderLayout.NORTH);

        disableEditing(courseInfoArea, sectionInfoArea, teacherInfoArea, studentsInfoArea);

        JPanel grid = new JPanel(new GridLayout(2, 2, 10, 10));
        grid.add(wrapText("Course Info", courseInfoArea));
        grid.add(wrapText("Section Info", sectionInfoArea));
        grid.add(wrapText("Instructor Info", teacherInfoArea));
        grid.add(wrapText("Students", studentsInfoArea));

        panel.add(grid, BorderLayout.CENTER);

        return panel;
    }

    private void disableEditing(JTextArea... areas) {
        for (JTextArea a : areas) {
            a.setEditable(false);
            a.setLineWrap(true);
            a.setWrapStyleWord(true);
        }
    }

    // ------------------------------------------------------------
    // Wrappers
    // ------------------------------------------------------------
    private JPanel wrapListWithTitle(String title, JList<?> list) {
        JPanel p = new JPanel(new BorderLayout());
        JLabel l = new JLabel(title);
        l.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        p.add(l, BorderLayout.NORTH);
        p.add(new JScrollPane(list), BorderLayout.CENTER);
        return p;
    }

    private JPanel wrapListWithTitle(JLabel label, JList<?> list) {
        JPanel p = new JPanel(new BorderLayout());
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        p.add(label, BorderLayout.NORTH);
        p.add(new JScrollPane(list), BorderLayout.CENTER);
        return p;
    }

    private JPanel wrapText(String title, JTextArea area) {
        JPanel p = new JPanel(new BorderLayout());
        JLabel label = new JLabel(title);
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        p.add(label, BorderLayout.NORTH);
        p.add(new JScrollPane(area), BorderLayout.CENTER);
        return p;
    }

    // ------------------------------------------------------------
    // Setters for MainController
    // ------------------------------------------------------------
    public void setLessonList(List<Lesson> lessons) { setList(courseLessonList, lessons); }
    public void setInstructorList(List<Instructor> instructors) { setList(teacherList, instructors); }
    public void setSectionList(List<Section> sections) { setList(sectionList, sections); }
    public void setStudentList(List<Student> students) { setList(studentList, students); }

    @SuppressWarnings("unchecked")
    private void setList(JList<?> list, List<?> items) {
        DefaultListModel<Object> model = (DefaultListModel<Object>) list.getModel();
        model.clear();
        if (items != null) for (Object o : items) model.addElement(o);
    }

    // ------------------------------------------------------------
    // Selection Getters
    // ------------------------------------------------------------
    public Lesson getSelectedLesson() { return courseLessonList.getSelectedValue(); }
    public Instructor getSelectedInstructor() { return teacherList.getSelectedValue(); }
    public Section getSelectedSection() { return sectionList.getSelectedValue(); }
    public Student getSelectedStudent() { return studentList.getSelectedValue(); }

    // ------------------------------------------------------------
    // Listeners
    // ------------------------------------------------------------
    public void addLessonSelectionListener(ListSelectionListener l) { courseLessonList.addListSelectionListener(l); }
    public void addInstructorSelectionListener(ListSelectionListener l) { teacherList.addListSelectionListener(l); }
    public void addSectionSelectionListener(ListSelectionListener l) { sectionList.addListSelectionListener(l); }
    public void addStudentSelectionListener(ListSelectionListener l) { studentList.addListSelectionListener(l); }

    // ============================================================
    // REQUIRED BY MAINCONTROLLER → setDetailList()
    // ============================================================
    public void setDetailList(List<?> items, String title) {
        int tab = tabbedPane.getSelectedIndex();

        switch (tab) {
            case 0 -> { courseDetailLabel.setText(title); setList(courseSectionList, items); }
            case 1 -> { teacherDetailLabel.setText(title); setList(teacherSectionList, items); }
            case 2 -> { sectionDetailLabel.setText(title); setList(sectionStudentList, items); }
            case 3 -> { studentDetailLabel.setText(title); setList(studentSectionList, items); }
        }
    }

    // ============================================================
    // Section Detail Tab Setters + Listeners
    // ============================================================
    public void setSectionDetailSections(List<Section> sections) {
        DefaultComboBoxModel<Section> model = new DefaultComboBoxModel<>();
        if (sections != null)
            for (Section s : sections) model.addElement(s);
        detailSectionCombo.setModel(model);
    }

    public Section getSelectedDetailSection() {
        return (Section) detailSectionCombo.getSelectedItem();
    }

    public void addSectionDetailSelectionListener(java.awt.event.ActionListener l) {
        detailSectionCombo.addActionListener(l);
    }

    // ============================================================
    // Show Section Detail
    // ============================================================
    public void showSectionDetail(Lesson lesson, Section section, Instructor instructor, List<Student> students) {

        // Course info
        if (lesson == null) {
            courseInfoArea.setText("No course found.");
        } else {
            courseInfoArea.setText(
                    "Lesson ID: " + lesson.getLessonId() + "\n" +
                            "Title: " + lesson.getTitle() + "\n" +
                            "Instrument: " + lesson.getInstrument() + "\n" +
                            "Start: " + lesson.getStartTime() + "\n" +
                            "End: " + lesson.getEndTime() + "\n" +
                            "Description: " + lesson.getDescription()
            );
        }

        // Section info
        if (section == null) {
            sectionInfoArea.setText("No section selected.");
        } else {
            sectionInfoArea.setText(
                    "Section ID: " + section.getSectionId() + "\n" +
                            "Name: " + section.getSectionName() + "\n" +
                            "Lesson ID: " + section.getLessonId() + "\n" +
                            "Instructor ID: " + section.getInstructorId() + "\n" +
                            "Room: " + section.getRoom()
            );
        }

        // Instructor info
        if (instructor == null) {
            teacherInfoArea.setText("No instructor found.");
        } else {
            teacherInfoArea.setText(
                    "Instructor ID: " + instructor.getInstructorId() + "\n" +
                            "User ID: " + instructor.getUserId() + "\n" +
                            "Name: " + instructor.getUserName()
            );
        }

        // Students info
        StringBuilder sb = new StringBuilder();
        if (students == null || students.isEmpty()) {
            sb.append("No students enrolled.");
        } else {
            for (Student s : students) {
                sb.append("Student ID: ").append(s.getStudentId())
                        .append(" | User ID: ").append(s.getUserId())
                        .append(" | Name: ").append(s.getUserName())
                        .append("\n");
            }
        }
        studentsInfoArea.setText(sb.toString());
    }
}
