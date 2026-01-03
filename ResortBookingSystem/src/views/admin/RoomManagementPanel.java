package views.admin;

import dao.ResortDAO;
import dao.RoomDAO;
import models.Resort;
import models.Room;
import utils.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing rooms (CRUD operations).
 */
public class RoomManagementPanel extends JPanel implements AdminDashboardFrame.RefreshablePanel {
    private RoomDAO roomDAO;
    private ResortDAO resortDAO;
    private JTable roomTable;
    private DefaultTableModel tableModel;

    // Form fields
    private JComboBox<Resort> resortCombo;
    private JComboBox<String> roomTypeCombo;
    private JSpinner bedsSpinner;
    private JSpinner priceSpinner;
    private JSpinner availableSpinner;
    private JButton saveButton;
    private JButton deleteButton;
    private JButton clearButton;

    private Room selectedRoom = null;

    public RoomManagementPanel() {
        this.roomDAO = new RoomDAO();
        this.resortDAO = new ResortDAO();
        initializeUI();
        loadRooms();
        loadResorts();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);

        // Left: Table
        JPanel tablePanel = createTablePanel();
        splitPane.setLeftComponent(tablePanel);

        // Right: Form
        JPanel formPanel = createFormPanel();
        splitPane.setRightComponent(formPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Rooms"));
        panel.setBackground(Color.WHITE);

        String[] columns = { "ID", "Resort", "Type", "Beds", "Price/Night", "Available" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        roomTable = new JTable(tableModel);
        roomTable.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 13));
        roomTable.setRowHeight(28);
        roomTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedRoom();
            }
        });

        JScrollPane scrollPane = new JScrollPane(roomTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> {
            loadRooms();
            loadResorts();
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(refreshBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Room Details"));
        panel.setBackground(Color.WHITE);

        JPanel formFields = new JPanel(new GridBagLayout());
        formFields.setOpaque(false);
        formFields.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Resort
        gbc.gridx = 0;
        gbc.gridy = 0;
        formFields.add(createLabel("Resort:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        resortCombo = new JComboBox<>();
        resortCombo.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 13));
        formFields.add(resortCombo, gbc);

        // Room Type
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formFields.add(createLabel("Room Type:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        roomTypeCombo = new JComboBox<>(new String[] { "Eco", "Premium", "Business", "Luxury" });
        roomTypeCombo.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 13));
        formFields.add(roomTypeCombo, gbc);

        // Beds
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formFields.add(createLabel("Beds:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        bedsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        bedsSpinner.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 13));
        formFields.add(bedsSpinner, gbc);

        // Price
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        formFields.add(createLabel("Price/Night ($):"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        priceSpinner = new JSpinner(new SpinnerNumberModel(100.0, 1.0, 10000.0, 10.0));
        priceSpinner.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 13));
        formFields.add(priceSpinner, gbc);

        // Available Count
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        formFields.add(createLabel("Available Rooms:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        availableSpinner = new JSpinner(new SpinnerNumberModel(10, 0, 1000, 1));
        availableSpinner.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 13));
        formFields.add(availableSpinner, gbc);

        panel.add(formFields, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);

        saveButton = createStyledButton("Save", new Color(46, 204, 113));
        deleteButton = createStyledButton("Delete", new Color(231, 76, 60));
        clearButton = createStyledButton("Clear", new Color(149, 165, 166));

        saveButton.addActionListener(e -> saveRoom());
        deleteButton.addActionListener(e -> deleteRoom());
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(90, 32));
        return button;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 13));
        return label;
    }

    private void loadRooms() {
        tableModel.setRowCount(0);
        List<Room> rooms = roomDAO.getAll();
        for (Room r : rooms) {
            tableModel.addRow(new Object[] {
                    r.getId(),
                    r.getResortName(),
                    r.getRoomType(),
                    r.getBeds(),
                    String.format("$%.2f", r.getPricePerNight()),
                    r.getAvailableCount()
            });
        }
    }

    private void loadResorts() {
        resortCombo.removeAllItems();
        List<Resort> resorts = resortDAO.getAll();
        for (Resort r : resorts) {
            resortCombo.addItem(r);
        }
    }

    private void loadSelectedRoom() {
        int row = roomTable.getSelectedRow();
        if (row < 0)
            return;

        int id = (int) tableModel.getValueAt(row, 0);
        selectedRoom = roomDAO.findById(id);

        if (selectedRoom != null) {
            // Select resort
            for (int i = 0; i < resortCombo.getItemCount(); i++) {
                if (resortCombo.getItemAt(i).getId() == selectedRoom.getResortId()) {
                    resortCombo.setSelectedIndex(i);
                    break;
                }
            }
            roomTypeCombo.setSelectedItem(selectedRoom.getRoomType());
            bedsSpinner.setValue(selectedRoom.getBeds());
            priceSpinner.setValue(selectedRoom.getPricePerNight());
            availableSpinner.setValue(selectedRoom.getAvailableCount());
        }
    }

    private void saveRoom() {
        Resort resort = (Resort) resortCombo.getSelectedItem();
        if (resort == null) {
            JOptionPane.showMessageDialog(this, "Please select a resort.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String roomType = (String) roomTypeCombo.getSelectedItem();
        int beds = (int) bedsSpinner.getValue();
        double price = (double) priceSpinner.getValue();
        int available = (int) availableSpinner.getValue();

        boolean success;
        if (selectedRoom == null) {
            // Create new
            Room room = new Room(resort.getId(), roomType, beds, price, available);
            success = roomDAO.create(room);
        } else {
            // Update existing
            selectedRoom.setResortId(resort.getId());
            selectedRoom.setRoomType(roomType);
            selectedRoom.setBeds(beds);
            selectedRoom.setPricePerNight(price);
            selectedRoom.setAvailableCount(available);
            success = roomDAO.update(selectedRoom);
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Room saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadRooms();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save room.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRoom() {
        if (selectedRoom == null) {
            JOptionPane.showMessageDialog(this, "Please select a room to delete.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this room?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (roomDAO.delete(selectedRoom.getId())) {
                JOptionPane.showMessageDialog(this, "Room deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadRooms();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete room.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        selectedRoom = null;
        if (resortCombo.getItemCount() > 0) {
            resortCombo.setSelectedIndex(0);
        }
        roomTypeCombo.setSelectedIndex(0);
        bedsSpinner.setValue(1);
        priceSpinner.setValue(100.0);
        availableSpinner.setValue(10);
        roomTable.clearSelection();
    }

    @Override
    public void refresh() {
        loadRooms();
        loadResorts();
    }
}
