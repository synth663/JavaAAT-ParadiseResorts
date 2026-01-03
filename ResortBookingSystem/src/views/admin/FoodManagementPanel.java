package views.admin;

import dao.FoodDAO;
import models.FoodOption;
import utils.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing food/cuisine options.
 */
public class FoodManagementPanel extends JPanel implements AdminDashboardFrame.RefreshablePanel {
    private FoodDAO foodDAO;
    private JTable foodTable;
    private DefaultTableModel tableModel;

    // Form fields
    private JComboBox<String> cuisineTypeCombo;
    private JComboBox<String> mealPlanCombo;
    private JSpinner priceSpinner;
    private JButton saveButton;
    private JButton deleteButton;
    private JButton clearButton;

    private FoodOption selectedFood = null;

    public FoodManagementPanel() {
        this.foodDAO = new FoodDAO();
        initializeUI();
        loadFoodOptions();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);

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
        panel.setBorder(BorderFactory.createTitledBorder("Food Options"));
        panel.setBackground(Color.WHITE);

        String[] columns = { "ID", "Cuisine Type", "Meal Plan", "Price/Day" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        foodTable = new JTable(tableModel);
        foodTable.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 13));
        foodTable.setRowHeight(28);
        foodTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedFood();
            }
        });

        JScrollPane scrollPane = new JScrollPane(foodTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadFoodOptions());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(refreshBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Food Option Details"));
        panel.setBackground(Color.WHITE);

        JPanel formFields = new JPanel(new GridBagLayout());
        formFields.setOpaque(false);
        formFields.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Cuisine Type
        gbc.gridx = 0;
        gbc.gridy = 0;
        formFields.add(createLabel("Cuisine Type:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        cuisineTypeCombo = new JComboBox<>(
                new String[] { "Local", "Italian", "Continental", "Chinese", "Indian", "Mexican" });
        cuisineTypeCombo.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 13));
        cuisineTypeCombo.setEditable(true);
        formFields.add(cuisineTypeCombo, gbc);

        // Meal Plan
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formFields.add(createLabel("Meal Plan:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        mealPlanCombo = new JComboBox<>(new String[] { "Breakfast Only", "Half Board", "Full Board", "All Inclusive" });
        mealPlanCombo.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 13));
        mealPlanCombo.setEditable(true);
        formFields.add(mealPlanCombo, gbc);

        // Price
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formFields.add(createLabel("Price/Day ($):"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        priceSpinner = new JSpinner(new SpinnerNumberModel(50.0, 1.0, 1000.0, 5.0));
        priceSpinner.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 13));
        formFields.add(priceSpinner, gbc);

        panel.add(formFields, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);

        saveButton = createStyledButton("Save", new Color(46, 204, 113));
        deleteButton = createStyledButton("Delete", new Color(231, 76, 60));
        clearButton = createStyledButton("Clear", new Color(149, 165, 166));

        saveButton.addActionListener(e -> saveFoodOption());
        deleteButton.addActionListener(e -> deleteFoodOption());
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

    private void loadFoodOptions() {
        tableModel.setRowCount(0);
        List<FoodOption> options = foodDAO.getAll();
        for (FoodOption f : options) {
            tableModel.addRow(new Object[] {
                    f.getId(),
                    f.getCuisineType(),
                    f.getMealPlan(),
                    String.format("$%.2f", f.getPricePerDay())
            });
        }
    }

    private void loadSelectedFood() {
        int row = foodTable.getSelectedRow();
        if (row < 0)
            return;

        int id = (int) tableModel.getValueAt(row, 0);
        selectedFood = foodDAO.findById(id);

        if (selectedFood != null) {
            cuisineTypeCombo.setSelectedItem(selectedFood.getCuisineType());
            mealPlanCombo.setSelectedItem(selectedFood.getMealPlan());
            priceSpinner.setValue(selectedFood.getPricePerDay());
        }
    }

    private void saveFoodOption() {
        String cuisineType = (String) cuisineTypeCombo.getSelectedItem();
        String mealPlan = (String) mealPlanCombo.getSelectedItem();
        double price = (double) priceSpinner.getValue();

        if (cuisineType == null || cuisineType.trim().isEmpty() ||
                mealPlan == null || mealPlan.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success;
        if (selectedFood == null) {
            // Create new
            FoodOption food = new FoodOption(cuisineType.trim(), mealPlan.trim(), price);
            success = foodDAO.create(food);
        } else {
            // Update existing
            selectedFood.setCuisineType(cuisineType.trim());
            selectedFood.setMealPlan(mealPlan.trim());
            selectedFood.setPricePerDay(price);
            success = foodDAO.update(selectedFood);
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Food option saved successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadFoodOptions();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save food option.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteFoodOption() {
        if (selectedFood == null) {
            JOptionPane.showMessageDialog(this, "Please select a food option to delete.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this food option?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (foodDAO.delete(selectedFood.getId())) {
                JOptionPane.showMessageDialog(this, "Food option deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadFoodOptions();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete food option.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        selectedFood = null;
        cuisineTypeCombo.setSelectedIndex(0);
        mealPlanCombo.setSelectedIndex(0);
        priceSpinner.setValue(50.0);
        foodTable.clearSelection();
    }

    @Override
    public void refresh() {
        loadFoodOptions();
    }
}
