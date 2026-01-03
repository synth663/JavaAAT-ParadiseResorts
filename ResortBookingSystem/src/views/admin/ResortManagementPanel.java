package views.admin;

import dao.ResortDAO;
import models.Resort;
import utils.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing resorts (CRUD operations).
 */
public class ResortManagementPanel extends JPanel implements AdminDashboardFrame.RefreshablePanel {
    private ResortDAO resortDAO;
    private JTable resortTable;
    private DefaultTableModel tableModel;

    // Form fields
    private JTextField nameField;
    private JTextField locationField;
    private JTextArea descriptionArea;
    private JButton saveButton;
    private JButton deleteButton;
    private JButton clearButton;

    private Resort selectedResort = null;

    public ResortManagementPanel() {
        this.resortDAO = new ResortDAO();
        initializeUI();
        loadResorts();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(550);

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
        panel.setBorder(BorderFactory.createTitledBorder("Resorts"));
        panel.setBackground(Color.WHITE);

        String[] columns = { "ID", "Name", "Location" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        resortTable = new JTable(tableModel);
        resortTable.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 13));
        resortTable.setRowHeight(28);
        resortTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedResort();
            }
        });

        JScrollPane scrollPane = new JScrollPane(resortTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Refresh button
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadResorts());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(refreshBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Resort Details"));
        panel.setBackground(Color.WHITE);

        JPanel formFields = new JPanel(new GridBagLayout());
        formFields.setOpaque(false);
        formFields.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formFields.add(createLabel("Name:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        nameField.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 13));
        formFields.add(nameField, gbc);

        // Location
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formFields.add(createLabel("Location:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        locationField = new JTextField(20);
        locationField.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 13));
        formFields.add(locationField, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formFields.add(createLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 13));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        formFields.add(descScroll, gbc);

        panel.add(formFields, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);

        saveButton = createStyledButton("Save", new Color(46, 204, 113));
        deleteButton = createStyledButton("Delete", new Color(231, 76, 60));
        clearButton = createStyledButton("Clear", new Color(149, 165, 166));

        saveButton.addActionListener(e -> saveResort());
        deleteButton.addActionListener(e -> deleteResort());
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

    private void loadResorts() {
        tableModel.setRowCount(0);
        List<Resort> resorts = resortDAO.getAll();
        for (Resort r : resorts) {
            tableModel.addRow(new Object[] { r.getId(), r.getName(), r.getLocation() });
        }
    }

    private void loadSelectedResort() {
        int row = resortTable.getSelectedRow();
        if (row < 0)
            return;

        int id = (int) tableModel.getValueAt(row, 0);
        selectedResort = resortDAO.findById(id);

        if (selectedResort != null) {
            nameField.setText(selectedResort.getName());
            locationField.setText(selectedResort.getLocation());
            descriptionArea.setText(selectedResort.getDescription() != null ? selectedResort.getDescription() : "");
        }
    }

    private void saveResort() {
        String name = nameField.getText().trim();
        String location = locationField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (name.isEmpty() || location.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Location are required.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success;
        if (selectedResort == null) {
            // Create new
            Resort resort = new Resort(name, location, description);
            success = resortDAO.create(resort);
        } else {
            // Update existing
            selectedResort.setName(name);
            selectedResort.setLocation(location);
            selectedResort.setDescription(description);
            success = resortDAO.update(selectedResort);
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Resort saved successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadResorts();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save resort.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteResort() {
        if (selectedResort == null) {
            JOptionPane.showMessageDialog(this, "Please select a resort to delete.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete resort '" + selectedResort.getName() + "'?\nThis will also affect related rooms.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (resortDAO.delete(selectedResort.getId())) {
                JOptionPane.showMessageDialog(this, "Resort deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadResorts();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete resort.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        selectedResort = null;
        nameField.setText("");
        locationField.setText("");
        descriptionArea.setText("");
        resortTable.clearSelection();
    }

    @Override
    public void refresh() {
        loadResorts();
    }
}
