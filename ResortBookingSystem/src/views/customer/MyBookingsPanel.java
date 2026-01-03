package views.customer;

import dao.BookingDAO;
import models.Booking;
import utils.UITheme;
import views.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel for viewing customer's bookings.
 */
public class MyBookingsPanel extends JPanel {
    private MainFrame mainFrame;
    private BookingDAO bookingDAO;
    private JTable bookingsTable;
    private DefaultTableModel tableModel;

    public MyBookingsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.bookingDAO = new BookingDAO();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("My Bookings");
        titleLabel.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 22));
        titleLabel.setForeground(new Color(44, 62, 80));

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadBookings());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(refreshBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Bookings table
        String[] columns = { "ID", "Resort", "Room Type", "Check-in", "Check-out", "Guests", "Status" };
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
        scrollPane.setBorder(BorderFactory.createTitledBorder("Booking History"));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setOpaque(false);

        JButton backBtn = new JButton("← Back to Browse");
        backBtn.addActionListener(e -> mainFrame.showPanel("BROWSE"));
        bottomPanel.add(backBtn);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void loadBookings() {
        tableModel.setRowCount(0);

        List<Booking> bookings = bookingDAO.findByUser(mainFrame.getCurrentUser().getId());
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Booking b : bookings) {
            tableModel.addRow(new Object[] {
                    b.getId(),
                    b.getResortName(),
                    b.getRoomType(),
                    b.getCheckInDate().format(fmt),
                    b.getCheckOutDate().format(fmt),
                    b.getNumGuests(),
                    b.getStatus()
            });
        }

        if (bookings.isEmpty()) {
            // Show message in table area
        }
    }
}
