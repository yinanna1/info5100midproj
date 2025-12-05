package view;

import dao.LibraryItemDAO;
import model.LibraryItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class LibraryStudentUI extends JDialog {

    private final LibraryItemDAO libraryDAO;

    private final DefaultListModel<LibraryItem> itemsModel = new DefaultListModel<>();
    private final JList<LibraryItem> itemsList = new JList<>(itemsModel);
    private final JTextArea detailArea = new JTextArea();

    public LibraryStudentUI(LibraryItemDAO libraryDAO) {
        super((Frame) null, "Library", false);
        this.libraryDAO = libraryDAO;

        setSize(700, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top label
        add(new JLabel("Library Items", SwingConstants.CENTER), BorderLayout.NORTH);

        // Left: list
        JScrollPane listScroll = new JScrollPane(itemsList);

        // Right: details
        detailArea.setEditable(false);
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);
        JScrollPane detailScroll = new JScrollPane(detailArea);

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                listScroll,
                detailScroll
        );
        split.setResizeWeight(0.5);

        add(split, BorderLayout.CENTER);

        // When selection changes, show details
        itemsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showDetails();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();   // just close this window
            }
        });

        reloadItems();
        if (!itemsModel.isEmpty()) {
            itemsList.setSelectedIndex(0);
            showDetails();
        }
    }

    private void reloadItems() {
        itemsModel.clear();
        List<LibraryItem> items = libraryDAO.getAllItems();
        for (LibraryItem item : items) {
            itemsModel.addElement(item);
        }
    }

    private void showDetails() {
        LibraryItem item = itemsList.getSelectedValue();
        if (item == null) {
            detailArea.setText("No item selected.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Title: ").append(item.getTitle()).append("\n");
        sb.append("Instrument: ").append(item.getInstrument()).append("\n\n");
        sb.append("URL:\n").append(item.getUrl()).append("\n");

        detailArea.setText(sb.toString());
    }
}

