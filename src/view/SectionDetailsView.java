package view;

import model.Section;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class SectionDetailsView extends JFrame {

    private final JComboBox<Section> sectionCombo = new JComboBox<>();

    private final JTextArea courseInfoArea = new JTextArea();
    private final JTextArea sectionInfoArea = new JTextArea();
    private final JTextArea instructorInfoArea = new JTextArea();
    private final JTextArea studentsInfoArea = new JTextArea();

    public SectionDetailsView() {
        setTitle("Section Detail Viewer");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        buildUI();

        setVisible(true);
    }

    // ============================================================
    // BUILD UI
    // ============================================================
    private void buildUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // ------------------ TOP: Section Selector ------------------
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Section: "));

        sectionCombo.setPreferredSize(new Dimension(300, 28));
        topPanel.add(sectionCombo);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // ------------------ CENTER PANELS ------------------
        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        disableEdit(courseInfoArea);
        disableEdit(sectionInfoArea);
        disableEdit(instructorInfoArea);
        disableEdit(studentsInfoArea);

        centerPanel.add(wrap("Course Info", courseInfoArea));
        centerPanel.add(wrap("Section Info", sectionInfoArea));
        centerPanel.add(wrap("Instructor Info", instructorInfoArea));
        centerPanel.add(wrap("Students", studentsInfoArea));

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    // Panel wrapper with title
    private JPanel wrap(String title, JTextArea area) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(title);
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(label, BorderLayout.NORTH);
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    private void disableEdit(JTextArea area) {
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
    }

    // ============================================================
    // PUBLIC API METHODS (Called by Controller)
    // ============================================================

    public void addSelectionListener(ActionListener l) {
        sectionCombo.addActionListener(l);
    }

    public void setSectionList(List<Section> sections) {
        DefaultComboBoxModel<Section> model = new DefaultComboBoxModel<>();
        for (Section s : sections) model.addElement(s);
        sectionCombo.setModel(model);
    }

    public Section getSelectedSection() {
        return (Section) sectionCombo.getSelectedItem();
    }

    // ============================================================
    // SHOW METHODS (the controller will call these)
    // ============================================================

    public void showCourseInfo(String text) {
        courseInfoArea.setText(text);
    }

    public void showSectionInfo(String text) {
        sectionInfoArea.setText(text);
    }

    public void showInstructorInfo(String text) {
        instructorInfoArea.setText(text);
    }

    public void showStudentsInfo(String text) {
        studentsInfoArea.setText(text);
    }
}
