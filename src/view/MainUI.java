package view;

import model.*;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.List;

public class MainUI extends JFrame {

    private final JTabbedPane tabbedPane = new JTabbedPane();

    // -------- Tab 1: Course -> Sections --------
    private final JList<Lesson> courseLessonList = new JList<>(new DefaultListModel<>());
    private final JList<Object> courseSectionList = new JList<>(new DefaultListModel<>());
    private final JLabel courseDetailLabel = new JLabel("Sections for Selected Course");

    // -------- Tab 2: Teacher -> Sections --------
    private final JList<Instructor> teacherList = new JList<>(new DefaultListModel<>());
    private final JList<Object> teacherSectionList = new JList<>(new DefaultListModel<>());
    private final JLabel teacherDetailLabel = new JLabel("Sections for Selected Teacher");

    // -------- Tab 3: Section -> Students --------
    private final JList<Section> sectionList = new JList<>(new DefaultListModel<>());
    private final JList<Object> sectionStudentList = new JList<>(new DefaultListModel<>());
    private final JLabel sectionDetailLabel = new JLabel("Students for Selected Section");

    // -------- Tab 4: Student -> Sections --------
    private final JList<Student> studentList = new JList<>(new DefaultListModel<>());
    private final JList<Object> studentSectionList = new JList<>(new DefaultListModel<>());
    private final JLabel studentDetailLabel = new JLabel("Sections for Selected Student");

    // -------- Tab 5: Section Detail (one view: course + section + teacher + students) --------
    private final JComboBox<Section> detailSectionCombo = new JComboBox<>();
    private final JTextArea courseInfoArea = new JTextArea();
    private final JTextArea sectionInfoArea = new JTextArea();
    private final JTextArea teacherInfoArea = new JTextArea();
    private final JTextArea studentsInfoArea = new JTextArea();

    public MainUI() {
        setTitle("EMS Master–Detail Views");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        buildTabs();
        setContentPane(tabbedPane);
        setVisible(true);
    }

    // ----------------------------------------------------------------------
    //  Build all 5 tabs
    // ----------------------------------------------------------------------
    private void buildTabs() {
        tabbedPane.addTab("Course → Sections", buildCourseTab());
        tabbedPane.addTab("Teacher → Sections", buildTeacherTab());
        tabbedPane.addTab("Section → Students", buildSectionTab());
        tabbedPane.addTab("Student → Sections", buildStudentTab());
        tabbedPane.addTab("Section Detail (All Info)", buildSectionDetailTab());
    }

    private JPanel buildCourseTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                wrapListWithTitle("Courses", courseLessonList),
                wrapListWithTitle(courseDetailLabel, courseSectionList)
        );
        split.setResizeWeight(0.5);
        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildTeacherTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                wrapListWithTitle("Teachers", teacherList),
                wrapListWithTitle(teacherDetailLabel, teacherSectionList)
        );
        split.setResizeWeight(0.5);
        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildSectionTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                wrapListWithTitle("Sections", sectionList),
                wrapListWithTitle(sectionDetailLabel, sectionStudentList)
        );
        split.setResizeWeight(0.5);
        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildStudentTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                wrapListWithTitle("Students", studentList),
                wrapListWithTitle(studentDetailLabel, studentSectionList)
        );
        split.setResizeWeight(0.5);
        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    // -------- New Tab 5: Section Detail --------
    private JPanel buildSectionDetailTab() {
        JPanel panel = new JPanel(new BorderLayout());

        // Top: section selector
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Select Section: "));
        detailSectionCombo.setPreferredSize(new Dimension(300, 25));
        top.add(detailSectionCombo);
        panel.add(top, BorderLayout.NORTH);

        // Center: 2x2 grid of text areas
        courseInfoArea.setEditable(false);
        sectionInfoArea.setEditable(false);
        teacherInfoArea.setEditable(false);
        studentsInfoArea.setEditable(false);

        JPanel grid = new JPanel(new GridLayout(2, 2, 8, 8));
        grid.add(wrapTextArea("Course Information", courseInfoArea));
        grid.add(wrapTextArea("Section Information", sectionInfoArea));
        grid.add(wrapTextArea("Teacher Information", teacherInfoArea));
        grid.add(wrapTextArea("Students Registered", studentsInfoArea));

        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    // ----------------------------------------------------------------------
    //  Helpers
    // ----------------------------------------------------------------------
    private JPanel wrapListWithTitle(String title, JList<?> list) {
        JLabel label = new JLabel(title);
        label.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        JPanel p = new JPanel(new BorderLayout());
        p.add(label, BorderLayout.NORTH);
        p.add(new JScrollPane(list), BorderLayout.CENTER);
        return p;
    }

    private JPanel wrapListWithTitle(JLabel label, JList<?> list) {
        label.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        JPanel p = new JPanel(new BorderLayout());
        p.add(label, BorderLayout.NORTH);
        p.add(new JScrollPane(list), BorderLayout.CENTER);
        return p;
    }

    private JPanel wrapTextArea(String title, JTextArea area) {
        JPanel p = new JPanel(new BorderLayout());
        JLabel label = new JLabel(title);
        label.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        p.add(label, BorderLayout.NORTH);
        p.add(new JScrollPane(area), BorderLayout.CENTER);
        return p;
    }

    // ==============================================================
    //  Methods used by controller  (existing 4 master–detail tabs)
    // ==============================================================

    // ----- Left lists -----
    public void setLessonList(List<Lesson> lessons) {
        setListData(courseLessonList, lessons);
    }

    public void setInstructorList(List<Instructor> instructors) {
        setListData(teacherList, instructors);
    }

    public void setSectionList(List<Section> sections) {
        setListData(sectionList, sections);
    }

    public void setStudentList(List<Student> students) {
        setListData(studentList, students);
    }

    @SuppressWarnings("unchecked")
    private void setListData(JList<?> list, List<?> data) {
        DefaultListModel<Object> model = (DefaultListModel<Object>) list.getModel();
        model.clear();
        if (data != null) {
            for (Object o : data) {
                model.addElement(o);
            }
        }
    }

    // ----- Get selected items -----
    public Lesson getSelectedLesson() {
        return courseLessonList.getSelectedValue();
    }

    public Instructor getSelectedInstructor() {
        return teacherList.getSelectedValue();
    }

    public Section getSelectedSection() {
        return sectionList.getSelectedValue();
    }

    public Student getSelectedStudent() {
        return studentList.getSelectedValue();
    }

    // ----- Listen to selections -----
    public void addLessonSelectionListener(ListSelectionListener l) {
        courseLessonList.addListSelectionListener(l);
    }

    public void addInstructorSelectionListener(ListSelectionListener l) {
        teacherList.addListSelectionListener(l);
    }

    public void addSectionSelectionListener(ListSelectionListener l) {
        sectionList.addListSelectionListener(l);
    }

    public void addStudentSelectionListener(ListSelectionListener l) {
        studentList.addListSelectionListener(l);
    }

    // ----- Detail list (right side of tabs 1–4) -----
    public void setDetailList(List<?> items, String title) {
        int idx = tabbedPane.getSelectedIndex();
        switch (idx) {
            case 0: // Course -> Sections
                courseDetailLabel.setText(title);
                setListData(courseSectionList, items);
                break;
            case 1: // Teacher -> Sections
                teacherDetailLabel.setText(title);
                setListData(teacherSectionList, items);
                break;
            case 2: // Section -> Students
                sectionDetailLabel.setText(title);
                setListData(sectionStudentList, items);
                break;
            case 3: // Student -> Sections
                studentDetailLabel.setText(title);
                setListData(studentSectionList, items);
                break;
            default:
                // ignore for tab 5
        }
    }

    // ==============================================================
    //  New methods for Tab 5: Section Detail (one combined view)
    // ==============================================================

    /** Fill the combo box with all sections. */
    public void setSectionDetailSections(List<Section> sections) {
        DefaultComboBoxModel<Section> model = new DefaultComboBoxModel<>();
        if (sections != null) {
            for (Section s : sections) {
                model.addElement(s);
            }
        }
        detailSectionCombo.setModel(model);
    }

    /** Which section is chosen in the Section Detail tab. */
    public Section getSelectedDetailSection() {
        return (Section) detailSectionCombo.getSelectedItem();
    }

    /** Controller subscribes to changes in the combo box. */
    public void addSectionDetailSelectionListener(java.awt.event.ActionListener l) {
        detailSectionCombo.addActionListener(l);
    }

    /** Display course/section/teacher/students information. */
    public void showSectionDetail(Lesson lesson,
                                  Section section,
                                  Instructor instructor,
                                  List<Student> students) {

        // Course
        if (lesson == null) {
            courseInfoArea.setText("No course information found for this section.");
        } else {
            courseInfoArea.setText(
                    "Lesson ID: " + lesson.getLessonId() + "\n" +
                            "Title: " + lesson.getTitle() + "\n" +
                            "Instrument: " + lesson.getInstrument() + "\n" +
                            "Start: " + lesson.getStartTime() + "\n" +
                            "End: " + lesson.getEndTime() + "\n" +
                            "Room: " + lesson.getRoom() + "\n" +
                            "Description: " + lesson.getDescription()
            );
        }

        // Section
        if (section == null) {
            sectionInfoArea.setText("No section selected.");
        } else {
            sectionInfoArea.setText(
                    "Section ID: " + section.getSectionId() + "\n" +
                            "Section Name: " + section.getSectionName() + "\n" +
                            "Lesson ID: " + section.getLessonId() + "\n" +
                            "Instructor ID: " + section.getInstructorId()
            );
        }

        // Teacher
        if (instructor == null) {
            teacherInfoArea.setText("No teacher assigned.");
        } else {
            teacherInfoArea.setText(
                    "Instructor ID: " + instructor.getInstructorId() + "\n" +
                            "User ID: " + instructor.getUserId() + "\n" +
                            "Name: " + instructor.getUserName()
            );
        }

        // Students
        StringBuilder sb = new StringBuilder();
        if (students == null || students.isEmpty()) {
            sb.append("No students registered in this section.");
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
