package views.admin;

import dao.BookingDAO;
import dao.InvoiceDAO;
import models.Booking;
import models.Invoice;
import utils.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel for viewing and managing all bookings.
 */
public class BookingManagementPanel extends JPanel implements AdminDashboardFrame.RefreshablePanel {
    private BookingDAO bookingDAO;
    private InvoiceDAO invoiceDAO;
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusFilter;

    public BookingManagementPanel() {
        this.bookingDAO = new BookingDAO();
        this.invoiceDAO = new InvoiceDAO();
        initializeUI();
        loadBookings();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Top: Filter bar
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        filterPanel.add(createLabel("Filter by Status:"));
        statusFilter = new JComboBox<>(new String[] { "All", "confirmed", "cancelled", "completed" });
        statusFilter.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 13));
        statusFilter.addActionListener(e -> filterBookings());
        filterPanel.add(statusFilter);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadBookings());
        filterPanel.add(refreshBtn);

        add(filterPanel, BorderLayout.NORTH);

        // Center: Table
        // Added columns: Beds, Cuisine, Meal Plan
        String[] columns = { "ID", "User", "Resort", "Room", "Beds", "Check-in", "Check-out", "Guests", "Cuisine",
                "Meal Plan", "Status" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingsTable = new JTable(tableModel);
        bookingsTable.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 13));
        bookingsTable.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("All Bookings"));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom: Actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        actionPanel.setOpaque(false);

        JButton confirmBtn = createStyledButton("Mark Confirmed", new Color(46, 204, 113));
        JButton completeBtn = createStyledButton("Mark Completed", new Color(52, 152, 219));
        JButton cancelBtn = createStyledButton("Mark Cancelled", new Color(231, 76, 60));
        JButton viewInvoiceBtn = createStyledButton("View Invoice", new Color(155, 89, 182));

        confirmBtn.addActionListener(e -> updateStatus("confirmed"));
        completeBtn.addActionListener(e -> updateStatus("completed"));
        cancelBtn.addActionListener(e -> updateStatus("cancelled"));
        viewInvoiceBtn.addActionListener(e -> showInvoice());

        actionPanel.add(createLabel("Change Status:"));
        actionPanel.add(confirmBtn);
        actionPanel.add(completeBtn);
        actionPanel.add(cancelBtn);
        actionPanel.add(Box.createHorizontalStrut(20)); // Spacer
        actionPanel.add(createLabel("Actions:"));
        actionPanel.add(viewInvoiceBtn);

        add(actionPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 11));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 13));
        return label;
    }

    private void loadBookings() {
        tableModel.setRowCount(0);
        List<Booking> bookings = bookingDAO.getAll();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Booking b : bookings) {
            tableModel.addRow(new Object[] {
                    b.getId(),
                    b.getUsername(),
                    b.getResortName(),
                    b.getRoomType(),
                    b.getBeds(),
                    b.getCheckInDate().format(fmt),
                    b.getCheckOutDate().format(fmt),
                    b.getNumGuests(),
                    b.getCuisineType() != null ? b.getCuisineType() : "N/A",
                    b.getMealPlan() != null ? b.getMealPlan() : "N/A",
                    b.getStatus()
            });
        }
    }

    private void filterBookings() {
        String filter = (String) statusFilter.getSelectedItem();
        tableModel.setRowCount(0);
        List<Booking> bookings = bookingDAO.getAll();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Booking b : bookings) {
            if ("All".equals(filter) || b.getStatus().equals(filter)) {
                tableModel.addRow(new Object[] {
                        b.getId(),
                        b.getUsername(),
                        b.getResortName(),
                        b.getRoomType(),
                        b.getBeds(),
                        b.getCheckInDate().format(fmt),
                        b.getCheckOutDate().format(fmt),
                        b.getNumGuests(),
                        b.getCuisineType() != null ? b.getCuisineType() : "N/A",
                        b.getMealPlan() != null ? b.getMealPlan() : "N/A",
                        b.getStatus()
                });
            }
        }
    }

    private void updateStatus(String newStatus) {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookingId = (int) tableModel.getValueAt(selectedRow, 0);

        if (bookingDAO.updateStatus(bookingId, newStatus)) {
            JOptionPane.showMessageDialog(this, "Status updated to: " + newStatus, "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            filterBookings();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update status.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showInvoice() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking to view invoice.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
        Invoice invoice = invoiceDAO.findByBooking(bookingId);

        if (invoice == null) {
            JOptionPane.showMessageDialog(this, "No invoice generated for this booking (e.g. Cancelled).",
                    "Invoice Not Found", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Invoice #" + invoice.getInvoiceNumber(), true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);

        JTextArea detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        detailArea.setBackground(Color.WHITE);
        detailArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("         INVOICE                       \n");
        sb.append("═══════════════════════════════════════\n\n");
        sb.append("Invoice #: ").append(invoice.getInvoiceNumber()).append("\n");
        sb.append("Date:      ").append(invoice.getCreatedAt() != null ? invoice.getCreatedAt().format(fmt) : "N/A")
                .append("\n");
        sb.append("Resort:    ").append(invoice.getResortName()).append("\n\n");
        sb.append("───────────────────────────────────────\n");
        sb.append("CHARGES:\n");
        sb.append("───────────────────────────────────────\n");
        sb.append(String.format("Room Charges:    $%10.2f\n", invoice.getRoomCharges()));
        sb.append(String.format("Food Charges:    $%10.2f\n", invoice.getFoodCharges()));
        sb.append(String.format("Taxes:           $%10.2f\n", invoice.getTaxes()));
        sb.append("───────────────────────────────────────\n");
        sb.append(String.format("TOTAL:           $%10.2f\n", invoice.getTotalAmount()));
        sb.append("═══════════════════════════════════════\n");

        detailArea.setText(sb.toString());
        detailArea.setCaretPosition(0);

        dialog.add(new JScrollPane(detailArea));
        dialog.setVisible(true);
    }

    @Override
    public void refresh() {
        loadBookings();
    }
}
