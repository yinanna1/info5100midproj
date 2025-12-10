package view;

import dao.AttendanceDAO;
import dao.LessonDAO;
import dao.SectionStudentDAO;
import model.Section;
import model.Student;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceDialog extends JDialog {

    private final LessonDAO lessonDAO;
    private final SectionStudentDAO sectionStudentDAO;
    private final AttendanceDAO attendanceDAO;

    private final JComboBox<Section> sectionCombo = new JComboBox<>();
    private final JButton loadBtn = new JButton("Load Students");

    private final JLabel lessonLabel = new JLabel("Lesson: (auto latest)");
    private final JLabel hintLabel = new JLabel("Choose a section, then mark Attend/Absent.");

    private final AttendanceTableModel tableModel = new AttendanceTableModel();
    private final JTable table = new JTable(tableModel);

    private Integer activeLessonId = null;

    public AttendanceDialog(
            Window owner,
            List<Section> instructorSections,
            LessonDAO lessonDAO,
            SectionStudentDAO sectionStudentDAO,
            AttendanceDAO attendanceDAO
    ) {
        super(owner, "Attendance", ModalityType.APPLICATION_MODAL);

        this.lessonDAO = lessonDAO;
        this.sectionStudentDAO = sectionStudentDAO;
        this.attendanceDAO = attendanceDAO;

        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(720, 420));

        // ---------- Top Area ----------
        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 6, 4, 6);
        gc.anchor = GridBagConstraints.WEST;

        for (Section s : instructorSections) {
            sectionCombo.addItem(s);
        }

        gc.gridx = 0; gc.gridy = 0;
        top.add(new JLabel("Section:"), gc);

        gc.gridx = 1;
        sectionCombo.setPreferredSize(new Dimension(240, 26));
        top.add(sectionCombo, gc);

        gc.gridx = 2;
        top.add(loadBtn, gc);

        gc.gridx = 0; gc.gridy = 1; gc.gridwidth = 3;
        lessonLabel.setForeground(new Color(80, 80, 80));
        top.add(lessonLabel, gc);

        gc.gridy = 2;
        hintLabel.setForeground(new Color(80, 80, 80));
        top.add(hintLabel, gc);

        add(top, BorderLayout.NORTH);

        // ---------- Table ----------
        table.setRowHeight(28);

        // column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(260); // name
        table.getColumnModel().getColumn(1).setPreferredWidth(220); // email
        table.getColumnModel().getColumn(2).setPreferredWidth(90);  // attend
        table.getColumnModel().getColumn(3).setPreferredWidth(90);  // absent

        // button columns
        new ButtonColumn(table, 2, "Attend", student -> mark(student, "Attend"));
        new ButtonColumn(table, 3, "Absent", student -> mark(student, "Absent"));

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ---------- Bottom ----------
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton close = new JButton("Close");
        close.addActionListener(e -> dispose());
        bottom.add(close);
        add(bottom, BorderLayout.SOUTH);

        // ---------- Actions ----------
        loadBtn.addActionListener(e -> loadStudents());

        pack();
        setLocationRelativeTo(owner);

        // Auto-load first selection
        if (sectionCombo.getItemCount() > 0) {
            loadStudents();
        } else {
            loadBtn.setEnabled(false);
            lessonLabel.setText("Lesson: No sections available");
        }
    }

    private void loadStudents() {
        Section selected = (Section) sectionCombo.getSelectedItem();
        if (selected == null) return;

        activeLessonId = lessonDAO.getLatestLessonIdBySection(selected.getSectionId());

        if (activeLessonId == null) {
            lessonLabel.setText("Lesson: None in this section (create a lesson first)");
            tableModel.setStudents(new ArrayList<>());
            table.setEnabled(false);
            return;
        }

        lessonLabel.setText("Lesson: Using latest lesson ID = " + activeLessonId);
        table.setEnabled(true);

        List<Student> students = sectionStudentDAO.getStudentsBySection(selected.getSectionId());
        tableModel.setStudents(students);
    }

    private void mark(Student student, String status) {
        if (activeLessonId == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "No lesson found for this section.\nPlease create a lesson first.",
                    "Cannot Mark Attendance",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        attendanceDAO.upsertAttendance(activeLessonId, student.getStudentId(), status);
        tableModel.setStatus(student.getStudentId(), status);
    }

    // ================= TABLE MODEL =================

    private static class AttendanceTableModel extends AbstractTableModel {
        private final String[] cols = {"Student", "Email", "Attend", "Absent"};
        private List<Student> students = new ArrayList<>();
        private final Map<Integer, String> statusMap = new HashMap<>();

        public void setStudents(List<Student> students) {
            this.students = students != null ? students : new ArrayList<>();
            statusMap.clear();
            fireTableDataChanged();
        }

        public void setStatus(int studentId, String status) {
            statusMap.put(studentId, status);
            fireTableDataChanged();
        }

        public Student getStudentAt(int row) {
            if (row < 0 || row >= students.size()) return null;
            return students.get(row);
        }

        @Override public int getRowCount() { return students.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int col) { return cols[col]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Student s = students.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> s.getName();
                case 1 -> s.getEmail();
                case 2 -> "Attend";
                case 3 -> "Absent";
                default -> "";
            };
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 2 || columnIndex == 3;
        }
    }

    // ================= BUTTON COLUMN =================

    private interface StudentAction {
        void run(Student student);
    }

    private static class ButtonColumn extends AbstractCellEditor
            implements TableCellRenderer, TableCellEditor {

        private final JTable table;
        private final JButton renderButton = new JButton();
        private final JButton editButton = new JButton();
        private final int column;
        private final String label;
        private final StudentAction action;

        public ButtonColumn(JTable table, int column, String label, StudentAction action) {
            this.table = table;
            this.column = column;
            this.label = label;
            this.action = action;

            renderButton.setText(label);
            editButton.setText(label);

            editButton.addActionListener(this::onClick);

            table.getColumnModel().getColumn(column).setCellRenderer(this);
            table.getColumnModel().getColumn(column).setCellEditor(this);
        }

        private void onClick(ActionEvent e) {
            int row = table.getEditingRow();
            if (row >= 0 && table.getModel() instanceof AttendanceTableModel model) {
                Student s = model.getStudentAt(row);
                if (s != null) {
                    action.run(s);
                }
            }
            fireEditingStopped();
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col
        ) {
            renderButton.setText(label);
            return renderButton;
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int col
        ) {
            editButton.setText(label);
            return editButton;
        }

        @Override
        public Object getCellEditorValue() {
            return label;
        }
    }
}
