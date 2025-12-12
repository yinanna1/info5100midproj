package view;

import dao.*;
import model.Lesson;
import model.Section;
import model.Student;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.border.EmptyBorder;
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

    // lists
    private final JList<Lesson> lessonList = new JList<>(new DefaultListModel<>());
    private final JList<Section> availableSectionList = new JList<>(new DefaultListModel<>());
    private final JList<Section> mySectionList = new JList<>(new DefaultListModel<>());

    // buttons
    private final JButton addSectionBtn = new JButton("Add Section");
    private final JButton dropSectionBtn = new JButton("Drop Section");
    private final JButton viewLibraryBtn = new JButton("View Library");

    // details
    private final JTextArea sectionInfoArea = new JTextArea();              // bottom-right (My Section)
    private final JTextArea availableSectionDetailArea = new JTextArea();   // top-right (Available Section Details)

    // clock
    private final JLabel clockLabel = new JLabel();
    private javax.swing.Timer clockTimer;

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

        setSize(1000, 600); // a bit wider to fit 3 columns nicely
        setLocationRelativeTo(null);

        buildUI();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (clockTimer != null) clockTimer.stop();
                System.exit(0);
            }
        });

        clockTimer = new javax.swing.Timer(1000, e -> updateClock());
        clockTimer.start();
        updateClock();

        setVisible(true);
    }

    // -----------------------------------------------------
    // BUILD UI LAYOUT
    // -----------------------------------------------------
    private void buildUI() {
        setLayout(new BorderLayout());

        // -------- top bar with name + clock -----------
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(27, 28, 70)); // navy

        JLabel nameLabel = new JLabel("  Logged in as: " + student.getUserName());
        nameLabel.setForeground(Color.WHITE);

        clockLabel.setForeground(Color.WHITE);
        clockLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        clockLabel.setBorder(new EmptyBorder(0, 0, 0, 10));

        topBar.add(nameLabel, BorderLayout.WEST);
        topBar.add(clockLabel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // style text areas
        configureArea(sectionInfoArea);
        configureArea(availableSectionDetailArea);
        availableSectionDetailArea.setText("Select an available section to see details.");

        // ---------- TOP: 3 columns ----------
        JPanel lessonsPanel = wrap("Lessons", new JScrollPane(lessonList));
        JPanel availablePanel = wrap("Available Sections", new JScrollPane(availableSectionList));
        JPanel availableDetailPanel = wrap("Available Section Details", new JScrollPane(availableSectionDetailArea));

        JSplitPane split12 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, lessonsPanel, availablePanel);
        split12.setResizeWeight(0.33);

        JSplitPane topThree = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, split12, availableDetailPanel);
        topThree.setResizeWeight(0.66); // first 2 columns together take ~2/3, details is ~1/3

        // ---------- BOTTOM: My Sections | Section Details ----------
        JSplitPane bottomSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                wrap("My Sections", new JScrollPane(mySectionList)),
                wrap("Section Details", new JScrollPane(sectionInfoArea))
        );
        bottomSplit.setResizeWeight(0.4);

        // ---------- Vertical main split ----------
        JSplitPane mainSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                topThree,
                bottomSplit
        );
        mainSplit.setResizeWeight(0.55);

        add(mainSplit, BorderLayout.CENTER);

        // ---------- bottom buttons ----------
        JPanel bottom = new JPanel();
        bottom.add(addSectionBtn);
        bottom.add(dropSectionBtn);
        bottom.add(viewLibraryBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    private void configureArea(JTextArea area) {
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
    }

    private JPanel wrap(String title, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout());
        JLabel label = new JLabel(title);
        label.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        p.add(label, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    // -----------------------------------------------------
    // CLOCK
    // -----------------------------------------------------
    private void updateClock() {
        LocalDateTime now = LocalDateTime.now();
        String text = now.format(DateTimeFormatter.ofPattern("EEE, MMM d yyyy  HH:mm:ss"));
        clockLabel.setText(text);
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

    public void onAvailableSectionSelected(ListSelectionListener l) {
        availableSectionList.addListSelectionListener(l);
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

    public void selectFirstLesson() {
        if (lessonList.getModel().getSize() > 0) lessonList.setSelectedIndex(0);
    }

    public void selectFirstAvailableSection() {
        if (availableSectionList.getModel().getSize() > 0) availableSectionList.setSelectedIndex(0);
    }

    public void selectFirstMySection() {
        if (mySectionList.getModel().getSize() > 0) mySectionList.setSelectedIndex(0);
    }

    public void setSectionDetail(String text) {
        sectionInfoArea.setText(text);
    }

    public void setAvailableSectionDetail(String text) {
        availableSectionDetailArea.setText(text);
    }
}
