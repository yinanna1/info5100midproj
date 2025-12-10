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
import java.util.ArrayList;
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

        // >>> DIRECTLY WIRE DROP BUTTON HERE <<<
        dropSectionBtn.addActionListener(e -> handleDropSection());

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
        bottom.add(viewLibraryBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel wrap(String title, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(title), BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    // -----------------------------------------------------
    // INTERNAL HELPERS
    // -----------------------------------------------------

    /**
     * After any change (add/drop) recompute:
     *  - My Sections for the currently selected lesson
     *  - Available Sections for that lesson
     */
    private void refreshSectionsForCurrentLesson() {
        Lesson selectedLesson = getSelectedLesson();

        if (selectedLesson == null) {
            // No lesson selected: show all student's sections, no available
            List<Section> myAll = sectionDAO.getSectionsByStudentId(student.getStudentId());
            setMySections(myAll);
            setAvailableSections(List.of());
            sectionInfoArea.setText("");
            return;
        }

        int lessonId = selectedLesson.getLessonId();

        // All sections belonging to this lesson
        List<Section> allForLesson = sectionDAO.getSectionsByLesson(lessonId);

        // All sections the student is enrolled in (any lesson)
        List<Section> myAll = sectionDAO.getSectionsByStudentId(student.getStudentId());

        // My sections for THIS lesson
        List<Section> myForLesson = new ArrayList<>();
        for (Section s : myAll) {
            if (s.getLessonId() == lessonId) {
                myForLesson.add(s);
            }
        }

        // Available = allForLesson - myForLesson
        List<Section> available = new ArrayList<>();
        for (Section sec : allForLesson) {
            boolean enrolled = false;
            for (Section mine : myForLesson) {
                if (mine.getSectionId() == sec.getSectionId()) {
                    enrolled = true;
                    break;
                }
            }
            if (!enrolled) {
                available.add(sec);
            }
        }

        setMySections(myForLesson);
        setAvailableSections(available);
        sectionInfoArea.setText("");
    }

    /**
     * Logic for dropping a section directly from this UI.
     */
    private void handleDropSection() {
        Section selected = getSelectedMySection();
        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a section to drop.",
                    "No Section Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to drop section: " + selected.getSectionName() + "?",
                "Confirm Drop",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // DAO signature is (sectionId, studentId)
        boolean ok = sectionStudentDAO.dropStudentFromSection(
                selected.getSectionId(),
                student.getStudentId()
        );

        if (!ok) {
            JOptionPane.showMessageDialog(this,
                    "Failed to drop this section (you may not be enrolled, or an error occurred).",
                    "Drop Failed",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Recompute My Sections and Available Sections so the dropped one
        // disappears from My Sections and shows up in Available Sections.
        refreshSectionsForCurrentLesson();
    }

    // -----------------------------------------------------
    // METHODS USED BY CONTROLLER (still available)
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
        // external controllers can still add listeners,
        // but we already have one internal listener wired in the constructor
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
