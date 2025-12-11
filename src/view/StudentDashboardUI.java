package view;

import dao.*;
import model.Lesson;
import model.Section;
import model.Student;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StudentDashboardUI extends JDialog {

    private final Student student;
    private final LessonDAO lessonDAO;
    private final SectionDAO sectionDAO;
    private final StudentDAO studentDAO;
    private final SectionStudentDAO sectionStudentDAO;

    // UI components
    private final JList<Lesson> lessonList = new JList<>(new DefaultListModel<>());
    private final JList<Section> availableSectionList = new JList<>(new DefaultListModel<>());
    private final JList<Section> mySectionList = new JList<>(new DefaultListModel<>());

    private final JButton addSectionBtn = new JButton("Add Section");
    private final JButton dropSectionBtn = new JButton("Drop Section");
    private final JButton viewLibraryBtn = new JButton("View Library");

    private final JTextArea sectionInfoArea = new JTextArea();

    // clock
    private final JLabel clockLabel = new JLabel();
    private final javax.swing.Timer clockTimer;

    public StudentDashboardUI(
            Student student,
            LessonDAO lessonDAO,
            SectionDAO sectionDAO,
            StudentDAO studentDAO,
            SectionStudentDAO sectionStudentDAO
    ) {
        super((Frame) null, "Student Dashboard", false);

        this.student = student;
        this.lessonDAO = lessonDAO;
        this.sectionDAO = sectionDAO;
        this.studentDAO = studentDAO;
        this.sectionStudentDAO = sectionStudentDAO;

        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        buildUI();

        // start clock
        clockTimer = new javax.swing.Timer(1000, e -> updateClock());
        clockTimer.start();
        updateClock();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                clockTimer.stop();
                System.exit(0);
            }
        });
    }

    // -----------------------------------------------------
    // BUILD UI
    // -----------------------------------------------------
    private void buildUI() {
        // Top bar: logged in + clock
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(27, 28, 70)); // navy bar

        JLabel loggedIn = new JLabel("  Logged in as: " + student.getUserName());
        loggedIn.setForeground(Color.WHITE);

        clockLabel.setForeground(Color.WHITE);
        clockLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        clockLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));

        top.add(loggedIn, BorderLayout.WEST);
        top.add(clockLabel, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        // main area split panes
        sectionInfoArea.setEditable(false);
        sectionInfoArea.setLineWrap(true);
        sectionInfoArea.setWrapStyleWord(true);

        JSplitPane topSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                wrap("Lessons", lessonList),
                wrap("Available Sections", availableSectionList)
        );
        topSplit.setResizeWeight(0.5);

        JSplitPane bottomSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                wrap("My Sections", mySectionList),
                wrap("Section Details", new JScrollPane(sectionInfoArea))
        );
        bottomSplit.setResizeWeight(0.5);

        JSplitPane mainSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                topSplit,
                bottomSplit
        );
        mainSplit.setResizeWeight(0.6);

        add(mainSplit, BorderLayout.CENTER);

        // bottom button bar – leave buttons with default look
        JPanel bottom = new JPanel();
        bottom.add(addSectionBtn);
        bottom.add(dropSectionBtn);
        bottom.add(viewLibraryBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel wrap(String title, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(title), BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private void updateClock() {
        LocalDateTime now = LocalDateTime.now();
        clockLabel.setText(
                now.format(DateTimeFormatter.ofPattern("EEE, MMM d yyyy  HH:mm:ss"))
        );
    }

    // -----------------------------------------------------
    // METHODS USED BY CONTROLLER
    // -----------------------------------------------------

    public void setLessonList(List<Lesson> lessons) {
        DefaultListModel<Lesson> m = (DefaultListModel<Lesson>) lessonList.getModel();
        m.clear();
        if (lessons != null) lessons.forEach(m::addElement);
    }

    public void setAvailableSections(List<Section> sections) {
        DefaultListModel<Section> m = (DefaultListModel<Section>) availableSectionList.getModel();
        m.clear();
        if (sections != null) sections.forEach(m::addElement);
    }

    public void setMySections(List<Section> sections) {
        DefaultListModel<Section> m = (DefaultListModel<Section>) mySectionList.getModel();
        m.clear();
        if (sections != null) sections.forEach(m::addElement);
    }

    public Lesson getSelectedLesson() {
        return lessonList.getSelectedValue();
    }

    public Section getSelectedAvailableSection() {
        return availableSectionList.getSelectedValue();
    }

    public Section getSelectedMySection() {
        return mySectionList.getSelectedValue();
    }

    public void onLessonSelected(ListSelectionListener l) {
        lessonList.addListSelectionListener(l);
    }

    public void onMySectionSelected(ListSelectionListener l) {
        mySectionList.addListSelectionListener(l);
    }

    public void onAddSection(java.awt.event.ActionListener l) {
        addSectionBtn.addActionListener(l);
    }

    public void onDropSection(java.awt.event.ActionListener l) {
        dropSectionBtn.addActionListener(l);
    }

    public void onViewLibrary(java.awt.event.ActionListener l) {
        viewLibraryBtn.addActionListener(l);
    }

    public void refresh(
            List<Lesson> lessons,
            List<Section> available,
            List<Section> mine
    ) {
        setLessonList(lessons);
        setAvailableSections(available);
        setMySections(mine);
    }

    public void selectFirstLesson() {
        ListModel<Lesson> model = lessonList.getModel();
        if (model.getSize() > 0) {
            lessonList.setSelectedIndex(0);
        }
    }

    public void selectFirstMySection() {
        ListModel<Section> model = mySectionList.getModel();
        if (model.getSize() > 0) {
            mySectionList.setSelectedIndex(0);
        }
    }

    public void setSectionDetail(String text) {
        sectionInfoArea.setText(text);
    }
}
