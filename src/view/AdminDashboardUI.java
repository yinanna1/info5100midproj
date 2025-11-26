package view;

import javax.swing.*;

public class AdminDashboardUI extends JFrame {

    public JTabbedPane tabs;

    // Course Panel
    public JPanel coursePanel;
    public JButton addCourseBtn, editCourseBtn, deleteCourseBtn;
    public JList lessonList;

    // Section Panel
    public JPanel sectionPanel;
    public JButton addSectionBtn, editSectionBtn, deleteSectionBtn;
    public JList sectionList;

    // Instructor Panel
    public JPanel instructorPanel;
    public JButton addInstructorBtn, editInstructorBtn, deleteInstructorBtn;
    public JList instructorList;

    // Student Panel
    public JPanel studentPanel;
    public JButton addStudentBtn, editStudentBtn, deleteStudentBtn;
    public JList studentList;

    public AdminDashboardUI() {
        setTitle("Admin Dashboard");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tabs = new JTabbedPane();

        // Build course tab
        coursePanel = new JPanel();
        lessonList = new JList();
        addCourseBtn = new JButton("Add Course");
        editCourseBtn = new JButton("Edit Course");
        deleteCourseBtn = new JButton("Delete Course");
        coursePanel.add(new JScrollPane(lessonList));
        coursePanel.add(addCourseBtn);
        coursePanel.add(editCourseBtn);
        coursePanel.add(deleteCourseBtn);

        // Build section tab
        sectionPanel = new JPanel();
        sectionList = new JList();
        addSectionBtn = new JButton("Add Section");
        editSectionBtn = new JButton("Edit Section");
        deleteSectionBtn = new JButton("Delete Section");
        sectionPanel.add(new JScrollPane(sectionList));
        sectionPanel.add(addSectionBtn);
        sectionPanel.add(editSectionBtn);
        sectionPanel.add(deleteSectionBtn);

        // Instructor tab
        instructorPanel = new JPanel();
        instructorList = new JList();
        addInstructorBtn = new JButton("Add Instructor");
        editInstructorBtn = new JButton("Edit Instructor");
        deleteInstructorBtn = new JButton("Delete Instructor");
        instructorPanel.add(new JScrollPane(instructorList));
        instructorPanel.add(addInstructorBtn);
        instructorPanel.add(editInstructorBtn);
        instructorPanel.add(deleteInstructorBtn);

        // Student tab
        studentPanel = new JPanel();
        studentList = new JList();
        addStudentBtn = new JButton("Add Student");
        editStudentBtn = new JButton("Edit Student");
        deleteStudentBtn = new JButton("Delete Student");
        studentPanel.add(new JScrollPane(studentList));
        studentPanel.add(addStudentBtn);
        studentPanel.add(editStudentBtn);
        studentPanel.add(deleteStudentBtn);

        // Attach tabs
        tabs.add("Courses", coursePanel);
        tabs.add("Sections", sectionPanel);
        tabs.add("Teachers", instructorPanel);
        tabs.add("Students", studentPanel);

        add(tabs);
    }
}

