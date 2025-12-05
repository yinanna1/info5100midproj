package view;

import dao.*;
import model.Lesson;
import model.Section;
import model.Student;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
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

    public StudentDashboardUI(
            Student student,
            LessonDAO lessonDAO,
            SectionDAO sectionDAO,
            StudentDAO studentDAO,
            SectionStudentDAO sectionStudentDAO
    ) {
        // ❗ make dialog NON-modal
        super((Frame) null, "Student Dashboard", false);

        this.student = student;
        this.lessonDAO = lessonDAO;
        this.sectionDAO = sectionDAO;
        this.studentDAO = studentDAO;
        this.sectionStudentDAO = sectionStudentDAO;

        setSize(800, 600);
        setLocationRelativeTo(null);

        buildUI();
        // ❗ DO NOT call setVisible(true) here – Main will do it after controller is set up
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

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                wrap("Lessons", lessonList),
                wrap("Available Sections", availableSectionList)
        );
        split.setResizeWeight(0.5);

        JSplitPane split2 = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                split,
                wrap("My Sections", mySectionList)
        );
        split2.setResizeWeight(0.7);

        add(split2, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.add(addSectionBtn);
        bottom.add(dropSectionBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel wrap(String title, JList<?> list) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(title), BorderLayout.NORTH);
        p.add(new JScrollPane(list), BorderLayout.CENTER);
        return p;
    }

    // -----------------------------------------------------
    // REQUIRED METHODS FOR CONTROLLER
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

    public void onAddSection(java.awt.event.ActionListener l) {
        addSectionBtn.addActionListener(l);
    }

    public void onDropSection(java.awt.event.ActionListener l) {
        dropSectionBtn.addActionListener(l);
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

    // Helper so controller can auto-select a lesson
    public void selectFirstLesson() {
        ListModel<Lesson> model = lessonList.getModel();
        if (model.getSize() > 0) {
            lessonList.setSelectedIndex(0);
        }
    }
}
