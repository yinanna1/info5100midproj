package view;

import dao.LibraryItemDAO;
import model.LibraryItem;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.util.List;

public class LibraryStudentUI extends JDialog {

    private final LibraryItemDAO libraryDAO;

    private final DefaultListModel<LibraryItem> itemsModel = new DefaultListModel<>();
    private final JList<LibraryItem> itemsList = new JList<>(itemsModel);

    // CHANGED: use JEditorPane so links are clickable
    private final JEditorPane detailPane = new JEditorPane();

    public LibraryStudentUI(LibraryItemDAO libraryDAO) {
        super((Frame) null, "Library", false);
        this.libraryDAO = libraryDAO;

        setSize(700, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(new JLabel("Library Items", SwingConstants.CENTER), BorderLayout.NORTH);

        // Left: list
        JScrollPane listScroll = new JScrollPane(itemsList);

        // Right: details (HTML + clickable)
        detailPane.setContentType("text/html");
        detailPane.setEditable(false);
        detailPane.setText("<html><body style='font-family:sans-serif;'>Select an item.</body></html>");

        detailPane.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Failed to open link:\n" + ex.getMessage(),
                            "Open Link Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        JScrollPane detailScroll = new JScrollPane(detailPane);

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
                dispose();
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
            detailPane.setText("<html><body style='font-family:sans-serif;'>No item selected.</body></html>");
            return;
        }

        String title = safe(item.getTitle());
        String instrument = safe(item.getInstrument());
        String url = safe(item.getUrl());

        // If user forgot to include http(s), browser open might fail.
        // We can auto-fix by adding https://
        String clickableUrl = url;
        if (!clickableUrl.isBlank()
                && !(clickableUrl.startsWith("http://") || clickableUrl.startsWith("https://"))) {
            clickableUrl = "https://" + clickableUrl;
        }

        String html =
                "<html><body style='font-family:sans-serif; font-size:12px; padding:8px;'>" +
                        "<h2 style='margin:0 0 8px 0;'>" + escapeHtml(title) + "</h2>" +
                        "<p style='margin:0 0 8px 0;'><b>Instrument:</b> " + escapeHtml(instrument) + "</p>" +
                        "<p style='margin:0;'><b>URL:</b><br/>" +
                        (clickableUrl.isBlank()
                                ? "<i>No URL provided</i>"
                                : "<a href='" + escapeHtml(clickableUrl) + "'>" + escapeHtml(url) + "</a>") +
                        "</p>" +
                        "</body></html>";

        detailPane.setText(html);
        detailPane.setCaretPosition(0);
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    // Minimal HTML escaping so titles like "A&B" don't break HTML
    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
