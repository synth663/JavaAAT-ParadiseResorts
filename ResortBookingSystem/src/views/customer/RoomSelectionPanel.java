package views.customer;

import dao.RoomDAO;
import models.Resort;
import models.Room;
import utils.DatePickerDialog;
import utils.UITheme;
import views.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel for selecting rooms, nights (1-30), and dates.
 */
public class RoomSelectionPanel extends JPanel {
    private MainFrame mainFrame;
    private RoomDAO roomDAO;
    private Resort selectedResort;
    private Room selectedRoom;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private JLabel resortNameLabel;
    private JPanel roomCardsPanel;
    private JTextField checkInField;
    private JButton dateButton;
    private JSpinner nightsSpinner;
    private JLabel checkOutLabel;
    private JSpinner guestsSpinner;
    private JButton nextButton;

    public RoomSelectionPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.roomDAO = new RoomDAO();
        this.checkInDate = LocalDate.now().plusDays(1);
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        resortNameLabel = new JLabel("Select Room");
        resortNameLabel.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 22));
        resortNameLabel.setForeground(new Color(44, 62, 80));

        JButton backBtn = new JButton("← Back to Resorts");
        backBtn.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 12));
        backBtn.addActionListener(e -> mainFrame.showPanel("BROWSE"));

        headerPanel.add(resortNameLabel, BorderLayout.WEST);
        headerPanel.add(backBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Main content
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setOpaque(false);

        // Date selection panel
        JPanel datePanel = createDatePanel();
        contentPanel.add(datePanel, BorderLayout.NORTH);

        // Room cards
        roomCardsPanel = new JPanel();
        roomCardsPanel.setLayout(new BoxLayout(roomCardsPanel, BoxLayout.Y_AXIS));
        roomCardsPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(roomCardsPanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Available Rooms"));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);

        nextButton = new JButton("Next: Select Cuisine →");
        nextButton.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 14));
        nextButton.setBackground(new Color(52, 152, 219));
        nextButton.setForeground(Color.WHITE);
        nextButton.setFocusPainted(false);
        nextButton.setBorderPainted(false);
        nextButton.setEnabled(false);
        nextButton.addActionListener(e -> proceedToCuisine());

        bottomPanel.add(nextButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createDatePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 8, 0, 8);
        gbc.anchor = GridBagConstraints.WEST;

        int col = 0;

        // Check-in date
        gbc.gridx = col++;
        JLabel checkInLabel = new JLabel("Check-in:");
        checkInLabel.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 12));
        panel.add(checkInLabel, gbc);

        gbc.gridx = col++;
        JPanel dateFieldPanel = new JPanel(new BorderLayout());
        dateFieldPanel.setOpaque(false);

        checkInField = new JTextField(checkInDate.toString());
        checkInField.setEditable(false);
        checkInField.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 13));
        checkInField.setPreferredSize(new Dimension(90, 30));
        checkInField.setBackground(Color.WHITE);
        checkInField.setHorizontalAlignment(JTextField.CENTER);

        dateButton = new JButton("📅");
        dateButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14)); // Emoji font for icon
        dateButton.setMargin(new Insets(2, 5, 2, 5));
        dateButton.setPreferredSize(new Dimension(30, 30));
        dateButton.setFocusPainted(false);
        dateButton.addActionListener(e -> openDatePicker());

        dateFieldPanel.add(checkInField, BorderLayout.CENTER);
        dateFieldPanel.add(dateButton, BorderLayout.EAST);

        panel.add(dateFieldPanel, gbc);

        // Number of Nights (1-30)
        gbc.gridx = col++;
        gbc.insets = new Insets(0, 20, 0, 8);
        JLabel nightsLabel = new JLabel("Nights:");
        nightsLabel.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 12));
        panel.add(nightsLabel, gbc);

        gbc.gridx = col++;
        gbc.insets = new Insets(0, 8, 0, 8);
        nightsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 30, 1));
        nightsSpinner.setPreferredSize(new Dimension(60, 30));
        nightsSpinner.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 14));
        panel.add(nightsSpinner, gbc);

        // Check-out display (auto-calculated)
        gbc.gridx = col++;
        gbc.insets = new Insets(0, 20, 0, 8);
        JLabel checkOutLbl = new JLabel("Check-out:");
        checkOutLbl.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 12));
        panel.add(checkOutLbl, gbc);

        gbc.gridx = col++;
        gbc.insets = new Insets(0, 8, 0, 8);
        checkOutLabel = new JLabel();
        checkOutLabel.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 14));
        checkOutLabel.setForeground(new Color(46, 204, 113));
        checkOutLabel.setPreferredSize(new Dimension(90, 30));
        panel.add(checkOutLabel, gbc);

        // Guests
        gbc.gridx = col++;
        gbc.insets = new Insets(0, 20, 0, 8);
        JLabel guestsLabel = new JLabel("Guests:");
        guestsLabel.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 12));
        panel.add(guestsLabel, gbc);

        gbc.gridx = col++;
        gbc.insets = new Insets(0, 8, 0, 8);
        guestsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        guestsSpinner.setPreferredSize(new Dimension(55, 30));
        panel.add(guestsSpinner, gbc);

        // Add listeners to update checkout date
        nightsSpinner.addChangeListener(e -> {
            calculateCheckOutDate();
        });

        // Initialize checkout date
        calculateCheckOutDate();

        return panel;
    }

    private void openDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog((Frame) SwingUtilities.getWindowAncestor(this), checkInDate);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            checkInDate = dialog.getSelectedDate();
            checkInField.setText(checkInDate.toString());
            calculateCheckOutDate();
            loadRooms(); // Reload availability
        }
    }

    private void calculateCheckOutDate() {
        int nights = (int) nightsSpinner.getValue();
        checkOutDate = checkInDate.plusDays(nights);
        checkOutLabel.setText(checkOutDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    public void setSelectedResort(Resort resort) {
        this.selectedResort = resort;
        this.selectedRoom = null;
        nextButton.setEnabled(false);
        resortNameLabel.setText("Select Room at " + resort.getName());
        loadRooms();
    }

    private void loadRooms() {
        roomCardsPanel.removeAll();

        if (selectedResort == null)
            return;

        List<Room> rooms = roomDAO.getAvailable(selectedResort.getId());

        if (rooms.isEmpty()) {
            JLabel noRoomsLabel = new JLabel("No rooms available at this resort.");
            noRoomsLabel.setFont(new Font(UITheme.getFontFamily(), Font.ITALIC, 14));
            roomCardsPanel.add(noRoomsLabel);
        } else {
            ButtonGroup roomGroup = new ButtonGroup();
            for (Room room : rooms) {
                JPanel roomCard = createRoomCard(room, roomGroup);
                roomCardsPanel.add(roomCard);
                roomCardsPanel.add(Box.createVerticalStrut(10));
            }
        }

        roomCardsPanel.revalidate();
        roomCardsPanel.repaint();
    }

    private JPanel createRoomCard(Room room, ButtonGroup group) {
        JPanel card = new JPanel(new BorderLayout(15, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // Radio button
        JRadioButton radioButton = new JRadioButton();
        radioButton.setOpaque(false);
        group.add(radioButton);
        card.add(radioButton, BorderLayout.WEST);

        // Room details
        JPanel detailsPanel = new JPanel(new GridLayout(2, 1));
        detailsPanel.setOpaque(false);

        JLabel typeLabel = new JLabel(room.getRoomType() + " Room");
        typeLabel.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 16));
        typeLabel.setForeground(new Color(44, 62, 80));

        JLabel infoLabel = new JLabel(
                room.getBeds() + " Beds  |  " + room.getAvailableCount() + " Available");
        infoLabel.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 13));
        infoLabel.setForeground(new Color(127, 140, 141));

        detailsPanel.add(typeLabel);
        detailsPanel.add(infoLabel);
        card.add(detailsPanel, BorderLayout.CENTER);

        // Price
        JLabel priceLabel = new JLabel("$" + String.format("%.0f", room.getPricePerNight()) + "/night");
        priceLabel.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 18));
        priceLabel.setForeground(new Color(46, 204, 113));
        card.add(priceLabel, BorderLayout.EAST);

        // Selection handler
        radioButton.addActionListener(e -> {
            selectedRoom = room;
            nextButton.setEnabled(true);
        });

        // Make whole card clickable
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                radioButton.setSelected(true);
                selectedRoom = room;
                nextButton.setEnabled(true);
            }
        });

        return card;
    }

    private void proceedToCuisine() {
        if (selectedRoom == null) {
            JOptionPane.showMessageDialog(this, "Please select a room.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int nights = (int) nightsSpinner.getValue();
        checkOutDate = checkInDate.plusDays(nights);

        // Validate check-in is not in the past (optional warning)
        if (checkInDate.isBefore(LocalDate.now())) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Check-in date is in the past. Continue anyway?",
                    "Date Warning",
                    JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }

        // Pass data to cuisine panel
        mainFrame.getCuisineSelectionPanel().setBookingDetails(
                selectedResort,
                selectedRoom,
                checkInDate,
                checkOutDate,
                (int) guestsSpinner.getValue());
        mainFrame.showPanel("CUISINE");
    }

    public Resort getSelectedResort() {
        return selectedResort;
    }

    public Room getSelectedRoom() {
        return selectedRoom;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public int getNumGuests() {
        return (int) guestsSpinner.getValue();
    }
}
