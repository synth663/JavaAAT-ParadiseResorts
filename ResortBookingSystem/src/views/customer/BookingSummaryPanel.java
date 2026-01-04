package views.customer;

import dao.*;
import models.*;
import utils.UITheme;
import views.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Panel for reviewing booking and generating invoice.
*/
public class BookingSummaryPanel extends JPanel {
    private MainFrame mainFrame;
    private BookingDAO bookingDAO;
    private InvoiceDAO invoiceDAO;
    private RoomDAO roomDAO;

    private Resort selectedResort;
    private Room selectedRoom;
    private FoodOption selectedFood;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numGuests;

    private JPanel summaryPanel;
    private JTextArea invoiceArea;

    public BookingSummaryPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.bookingDAO = new BookingDAO();
        this.invoiceDAO = new InvoiceDAO();
        this.roomDAO = new RoomDAO();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Review Your Booking");
        titleLabel.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 22));
        titleLabel.setForeground(new Color(44, 62, 80));

        JButton backBtn = new JButton("← Back to Cuisine");
        backBtn.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 12));
        backBtn.addActionListener(e -> mainFrame.showPanel("CUISINE"));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(backBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Summary content
        summaryPanel = new JPanel(new BorderLayout(15, 15));
        summaryPanel.setOpaque(false);

        invoiceArea = new JTextArea();
        invoiceArea.setEditable(false);
        invoiceArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        invoiceArea.setBackground(Color.WHITE);
        invoiceArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(invoiceArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        summaryPanel.add(scrollPane, BorderLayout.CENTER);

        add(summaryPanel, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottomPanel.setOpaque(false);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 14));
        cancelBtn.addActionListener(e -> mainFrame.showPanel("BROWSE"));

        JButton confirmBtn = new JButton("Confirm & Generate Invoice");
        confirmBtn.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 14));
        confirmBtn.setBackground(new Color(46, 204, 113));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFocusPainted(false);
        confirmBtn.setBorderPainted(false);
        confirmBtn.addActionListener(e -> confirmBooking());

        bottomPanel.add(cancelBtn);
        bottomPanel.add(confirmBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void setBookingDetails(Resort resort, Room room, FoodOption food,
            LocalDate checkIn, LocalDate checkOut, int guests) {
        this.selectedResort = resort;
        this.selectedRoom = room;
        this.selectedFood = food;
        this.checkInDate = checkIn;
        this.checkOutDate = checkOut;
        this.numGuests = guests;

        updateSummary();
    }

    private void updateSummary() {
        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        double roomCharges = selectedRoom.getPricePerNight() * nights;
        double foodCharges = selectedFood != null ? selectedFood.getPricePerDay() * nights : 0;
        double subtotal = roomCharges + foodCharges;
        double taxes = subtotal * 0.10; // 10% tax
        double total = subtotal + taxes;

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // All lines are exactly 58 characters wide (including borders)
        final int WIDTH = 56; // inner content width
        String dblBorder = "+" + "=".repeat(WIDTH) + "+";
        String divider = "|" + "-".repeat(WIDTH) + "|";

        StringBuilder sb = new StringBuilder();
        sb.append(dblBorder).append("\n");
        sb.append(padCenter("BOOKING SUMMARY", WIDTH)).append("\n");
        sb.append(dblBorder).append("\n");
        sb.append(padLeft("Guest: " + mainFrame.getCurrentUser().getUsername(), WIDTH)).append("\n");
        sb.append(divider).append("\n");
        sb.append(padLeft("RESORT DETAILS", WIDTH)).append("\n");
        sb.append(padLeft("  Resort: " + selectedResort.getName(), WIDTH)).append("\n");
        sb.append(padLeft("  Location: " + selectedResort.getLocation(), WIDTH)).append("\n");
        sb.append(padLeft("  Room: " + selectedRoom.getRoomType() + " (" + selectedRoom.getBeds() + " beds)", WIDTH))
                .append("\n");
        sb.append(divider).append("\n");
        sb.append(padLeft("STAY DETAILS", WIDTH)).append("\n");
        sb.append(padLeft("  Check-in: " + checkInDate.format(fmt), WIDTH)).append("\n");
        sb.append(padLeft("  Check-out: " + checkOutDate.format(fmt), WIDTH)).append("\n");
        sb.append(padLeft("  Nights: " + nights, WIDTH)).append("\n");
        sb.append(padLeft("  Guests: " + numGuests, WIDTH)).append("\n");

        if (selectedFood != null) {
            sb.append(divider).append("\n");
            sb.append(padLeft("MEAL PLAN", WIDTH)).append("\n");
            sb.append(padLeft("  Cuisine: " + selectedFood.getCuisineType(), WIDTH)).append("\n");
            sb.append(padLeft("  Plan: " + selectedFood.getMealPlan(), WIDTH)).append("\n");
        }

        sb.append(divider).append("\n");
        sb.append(padLeft("CHARGES", WIDTH)).append("\n");
        sb.append(divider).append("\n");

        // Room charges
        String roomLine = String.format("  Room: $%.2f x %d nights", selectedRoom.getPricePerNight(), nights);
        sb.append(formatChargeLine(roomLine, roomCharges, WIDTH)).append("\n");

        // Meals
        if (selectedFood != null) {
            String mealLine = String.format("  Meals: $%.2f x %d days", selectedFood.getPricePerDay(), nights);
            sb.append(formatChargeLine(mealLine, foodCharges, WIDTH)).append("\n");
        }

        sb.append(divider).append("\n");
        sb.append(formatChargeLine("  Subtotal:", subtotal, WIDTH)).append("\n");
        sb.append(formatChargeLine("  Taxes (10%):", taxes, WIDTH)).append("\n");
        sb.append(dblBorder).append("\n");
        sb.append(formatChargeLine("  TOTAL AMOUNT:", total, WIDTH)).append("\n");
        sb.append(dblBorder).append("\n");

        invoiceArea.setText(sb.toString());
        invoiceArea.setCaretPosition(0);
    }

    private String padLeft(String text, int width) {
        if (text.length() > width) {
            text = text.substring(0, width);
        }
        return "|" + String.format("%-" + width + "s", text) + "|";
    }

    private String padCenter(String text, int width) {
        int padding = (width - text.length()) / 2;
        String centered = " ".repeat(Math.max(0, padding)) + text;
        return "|" + String.format("%-" + width + "s", centered) + "|";
    }

    private String formatChargeLine(String label, double amount, int width) {
        String amountStr = String.format("$%,.2f", amount);
        int labelWidth = width - amountStr.length() - 2; // 2 spaces before amount
        if (label.length() > labelWidth) {
            label = label.substring(0, labelWidth);
        }
        return "|" + String.format("%-" + labelWidth + "s", label) + "  " + amountStr + "|";
    }

    private void confirmBooking() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Confirm this booking?",
                "Confirm Booking",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION)
            return;

        try {
            // Create booking
            Booking booking = new Booking();
            booking.setUserId(mainFrame.getCurrentUser().getId());
            booking.setResortId(selectedResort.getId());
            booking.setRoomId(selectedRoom.getId());
            if (selectedFood != null) {
                booking.setFoodOptionId(selectedFood.getId());
            }
            booking.setCheckInDate(checkInDate);
            booking.setCheckOutDate(checkOutDate);
            booking.setNumGuests(numGuests);
            booking.setStatus("confirmed");

            if (!bookingDAO.create(booking)) {
                throw new Exception("Failed to create booking");
            }

            // Decrease room availability
            roomDAO.updateAvailability(selectedRoom.getId(), -1);

            // Calculate charges
            long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            double roomCharges = selectedRoom.getPricePerNight() * nights;
            double foodCharges = selectedFood != null ? selectedFood.getPricePerDay() * nights : 0;
            double subtotal = roomCharges + foodCharges;
            double taxes = subtotal * 0.10;
            double total = subtotal + taxes;

            // Create invoice
            Invoice invoice = new Invoice();
            invoice.setBookingId(booking.getId());
            invoice.setUserId(mainFrame.getCurrentUser().getId());
            invoice.setInvoiceNumber(Invoice.generateInvoiceNumber());
            invoice.setRoomCharges(roomCharges);
            invoice.setFoodCharges(foodCharges);
            invoice.setTaxes(taxes);
            invoice.setTotalAmount(total);

            if (!invoiceDAO.create(invoice)) {
                throw new Exception("Failed to create invoice");
            }

            JOptionPane.showMessageDialog(this,
                    "Booking confirmed!\nInvoice Number: " + invoice.getInvoiceNumber(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            mainFrame.showPanel("BROWSE");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error creating booking: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
