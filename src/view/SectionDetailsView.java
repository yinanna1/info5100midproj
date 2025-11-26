package view;

import model.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SectionDetailsView extends JFrame {

    private JComboBox<Section> sectionDropdown;

    private JTextArea courseInfoArea;
    private JTextArea sectionInfoArea;
    private JTextArea teacherInfoArea;
    private JTextArea studentListArea;

    public SectionDetailsView() {

        setTitle("Section Detail Viewer");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        sectionDropdown = new JComboBox<>();
        topPanel.add(new JLabel("Select Section: "));
        topPanel.add(sectionDropdown);

        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        courseInfoArea   = createTextArea("Course Info");
        sectionInfoArea  = createTextArea("Section Info");
        teacherInfoArea  = createTextArea("Teacher Info");
        studentListArea  = createTextArea("Students Registered");

        centerPanel.add(courseInfoArea);
        centerPanel.add(sectionInfoArea);
        centerPanel.add(teacherInfoArea);
        centerPanel.add(studentListArea);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JTextArea createTextArea(String title) {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        JScrollPane pane = new JScrollPane(area);
        pane.setBorder(BorderFactory.createTitledBorder(title));
        return area;
    }

    // UI population methods
    public void setSectionList(List<Section> sections) {
        sectionDropdown.removeAllItems();
        for (Section s : sections) {
            sectionDropdown.addItem(s);
        }
    }

    public Section getSelectedSection() {
        return (Section) sectionDropdown.getSelectedItem();
    }

    public void addSectionSelectListener(Runnable callback) {
        sectionDropdown.addActionListener(e -> callback.run());
    }

    // update display panels
    public void showCourseInfo(Lesson lesson) {
        if (lesson == null) {
            courseInfoArea.setText("No course found");
            return;
        }

        courseInfoArea.setText(
                "Course ID: " + lesson.getLessonId() + "\n" +
                        "Title: " + lesson.getTitle() + "\n" +
                        "Instrument: " + lesson.getInstrument() + "\n" +
                        "Start: " + lesson.getStartTime() + "\n" +
                        "End: " + lesson.getEndTime()
        );
    }

    public void showSectionInfo(Section section) {
        if (section == null) {
            sectionInfoArea.setText("No section selected");
            return;
        }

        sectionInfoArea.setText(
                "Section ID: " + section.getSectionId() + "\n" +
                        "Room: " + section.getRoom()
        );
    }

    public void showTeacherInfo(Instructor teacher) {
        if (teacher == null) {
            teacherInfoArea.setText("No teacher found");
            return;
        }

        teacherInfoArea.setText(
                "Instructor ID: " + teacher.getInstructorId() + "\n" +
                        "Name: " + teacher.getUserName() + "\n" +
                        "User ID: " + teacher.getUserId()
        );
    }

    public void showStudentList(List<Student> students) {
        StringBuilder sb = new StringBuilder();
        for (Student s : students) {
            sb.append("Student ID: ").append(s.getStudentId())
                    .append(" | Name: ").append(s.getUserName())
                    .append("\n");
        }
        studentListArea.setText(sb.toString());
    }
}

