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
    private final JButton viewLibraryBtn = new JButton("View Library");   // NEW

    // details area for selected section
    private final JTextArea sectionInfoArea = new JTextArea();

    public StudentDashboardUI(
            Student student,
            LessonDAO lessonDAO,
            SectionDAO sectionDAO,
            StudentDAO studentDAO,
            SectionStudentDAO sectionStudentDAO
    ) {
        // Make dialog non-modal so controller can run after construction
        super((Frame) null, "Student Dashboard", false);

        this.student = student;
        this.lessonDAO = lessonDAO;
        this.sectionDAO = sectionDAO;
        this.studentDAO = studentDAO;
        this.sectionStudentDAO = sectionStudentDAO;

        setSize(900, 600);
        setLocationRelativeTo(null);

        buildUI();

        // When user clicks the window close button, exit the program
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    // -----------------------------------------------------
    // BUILD UI LAYOUT
    // -----------------------------------------------------
    private void buildUI() {
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new BorderLayout());
        top.add(new JLabel("Logged in as: " + student.getUserName(), SwingConstants.CENTER),
                BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        // Top split: Lessons | Available Sections
        JSplitPane topSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                wrap("Lessons", lessonList),
                wrap("Available Sections", availableSectionList)
        );
        topSplit.setResizeWeight(0.5);

        // Bottom split: My Sections | Section Details
        sectionInfoArea.setEditable(false);
        sectionInfoArea.setLineWrap(true);
        sectionInfoArea.setWrapStyleWord(true);

        JSplitPane bottomSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                wrap("My Sections", mySectionList),
                wrap("Section Details", new JScrollPane(sectionInfoArea))
        );
        bottomSplit.setResizeWeight(0.5);

        // Vertical split: [topSplit] over [bottomSplit]
        JSplitPane mainSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                topSplit,
                bottomSplit
        );
        mainSplit.setResizeWeight(0.6);

        add(mainSplit, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.add(addSectionBtn);
        bottom.add(dropSectionBtn);
        bottom.add(viewLibraryBtn);      // NEW
        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel wrap(String title, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(title), BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
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

    // NEW: hook for "View Library" button
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

    // Helper so controller can auto-select first lesson
    public void selectFirstLesson() {
        ListModel<Lesson> model = lessonList.getModel();
        if (model.getSize() > 0) {
            lessonList.setSelectedIndex(0);
        }
    }

    // Helper so controller can auto-select first "My Section"
    public void selectFirstMySection() {
        ListModel<Section> model = mySectionList.getModel();
        if (model.getSize() > 0) {
            mySectionList.setSelectedIndex(0);
        }
    }

    // Controller uses this to show section details
    public void setSectionDetail(String text) {
        sectionInfoArea.setText(text);
    }
}
