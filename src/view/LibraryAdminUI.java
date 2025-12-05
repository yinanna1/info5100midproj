package view;

import dao.LibraryItemDAO;
import model.LibraryItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.List;

public class LibraryAdminUI extends JDialog {

    private final LibraryItemDAO libraryDAO;

    private final DefaultListModel<LibraryItem> itemsModel = new DefaultListModel<>();
    private final JList<LibraryItem> itemsList = new JList<>(itemsModel);

    public LibraryAdminUI(LibraryItemDAO libraryDAO) {
        super((Frame) null, "Library Items", false);
        this.libraryDAO = libraryDAO;

        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(new JLabel("Library Items", SwingConstants.CENTER), BorderLayout.NORTH);
        add(new JScrollPane(itemsList), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton addBtn = new JButton("Add Item");
        JButton deleteBtn = new JButton("Delete Item");
        bottom.add(addBtn);
        bottom.add(deleteBtn);
        add(bottom, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> addItem());
        deleteBtn.addActionListener(e -> deleteItem());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        reloadItems();
    }

    // ------------------------ LOAD ITEMS ------------------------
    private void reloadItems() {
        itemsModel.clear();
        List<LibraryItem> items = libraryDAO.getAllItems();
        for (LibraryItem item : items) {
            itemsModel.addElement(item);
        }
    }

    // ------------------------ ADD ITEM --------------------------
    private void addItem() {
        JTextField titleF = new JTextField();
        JTextField urlF = new JTextField();
        JTextField instrumentF = new JTextField();

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Title:"));
        panel.add(titleF);
        panel.add(new JLabel("URL:"));
        panel.add(urlF);
        panel.add(new JLabel("Instrument:"));
        panel.add(instrumentF);

        int ok = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add Library Item",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (ok == JOptionPane.OK_OPTION) {
            try {
                String title = titleF.getText();
                String url = urlF.getText();
                String instrument = instrumentF.getText();

                boolean success = libraryDAO.createItem(title, url, instrument);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Library item added.");
                    reloadItems();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add item (no rows inserted).");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        this,
                        "SQL error while adding item:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        this,
                        "Unexpected error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    // ------------------------ DELETE ITEM -----------------------
    private void deleteItem() {
        LibraryItem selected = itemsList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select an item to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete selected item?\n" + selected,
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean ok = libraryDAO.deleteItem(selected.getLibraryId());
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Item deleted.");
                    reloadItems();
                } else {
                    JOptionPane.showMessageDialog(this, "Delete failed (no rows affected).");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        this,
                        "SQL error while deleting item:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}
