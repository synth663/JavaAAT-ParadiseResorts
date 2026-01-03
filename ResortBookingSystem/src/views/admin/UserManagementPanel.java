package views.admin;

import dao.UserDAO;
import models.User;
import utils.PasswordUtils;
import utils.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing user accounts (view, create, delete).
 */
public class UserManagementPanel extends JPanel implements AdminDashboardFrame.RefreshablePanel {
    private UserDAO userDAO;
    private JTable userTable;
    private DefaultTableModel tableModel;

    // Form fields for creating new user
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JTextField phoneField;
    private JComboBox<String> roleCombo;

    public UserManagementPanel() {
        this.userDAO = new UserDAO();
        initializeUI();
        loadUsers();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(550);

        // Left: User table
        JPanel tablePanel = createTablePanel();
        splitPane.setLeftComponent(tablePanel);

        // Right: Create user form
        JPanel formPanel = createFormPanel();
        splitPane.setRightComponent(formPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Registered Users"));
        panel.setBackground(Color.WHITE);

        String[] columns = { "ID", "Username", "Email", "Phone", "Role", "Created" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(tableModel);
        userTable.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 13));
        userTable.setRowHeight(28);
        userTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        userTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        userTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        userTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        userTable.getColumnModel().getColumn(4).setPreferredWidth(70);
        userTable.getColumnModel().getColumn(5).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(userTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        btnPanel.setOpaque(false);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadUsers());

        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.setBackground(new Color(231, 76, 60));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.addActionListener(e -> deleteSelectedUser());

        btnPanel.add(refreshBtn);
        btnPanel.add(deleteBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Create New User"));
        panel.setBackground(Color.WHITE);

        JPanel formFields = new JPanel(new GridBagLayout());
        formFields.setOpaque(false);
        formFields.setBorder(new EmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        formFields.add(new JLabel("Username *:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        usernameField = new JTextField(15);
        formFields.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formFields.add(new JLabel("Password *:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(15);
        formFields.add(passwordField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formFields.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        emailField = new JTextField(15);
        formFields.add(emailField, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        formFields.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        phoneField = new JTextField(15);
        formFields.add(phoneField, gbc);

        // Role
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        formFields.add(new JLabel("Role *:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        roleCombo = new JComboBox<>(new String[] { "CUSTOMER", "ADMIN" });
        formFields.add(roleCombo, gbc);

        panel.add(formFields, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);

        JButton createBtn = new JButton("Create User");
        createBtn.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 12));
        createBtn.setBackground(new Color(46, 204, 113));
        createBtn.setForeground(Color.WHITE);
        createBtn.setFocusPainted(false);
        createBtn.setBorderPainted(false);
        createBtn.setPreferredSize(new Dimension(120, 35));
        createBtn.addActionListener(e -> createUser());

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 12));
        clearBtn.setBackground(new Color(149, 165, 166));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFocusPainted(false);
        clearBtn.setBorderPainted(false);
        clearBtn.setPreferredSize(new Dimension(100, 35));
        clearBtn.addActionListener(e -> clearForm());

        buttonPanel.add(createBtn);
        buttonPanel.add(clearBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        List<User> users = userDAO.getAllUsers();

        for (User u : users) {
            tableModel.addRow(new Object[] {
                    u.getId(),
                    u.getUsername(),
                    u.getEmail() != null ? u.getEmail() : "-",
                    u.getPhone() != null ? u.getPhone() : "-",
                    u.getRole().name(),
                    u.getCreatedAt() != null ? u.getCreatedAt().toString().substring(0, 10) : "-"
            });
        }
    }

    private void createUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String role = (String) roleCombo.getSelectedItem();

        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password are required.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (username.length() < 3) {
            JOptionPane.showMessageDialog(this, "Username must be at least 3 characters.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (userDAO.usernameExists(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create user
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(PasswordUtils.hashPassword(password));
        user.setEmail(email.isEmpty() ? null : email);
        user.setPhone(phone.isEmpty() ? null : phone);
        user.setRole(User.Role.valueOf(role));

        if (userDAO.create(user)) {
            JOptionPane.showMessageDialog(this, "User created successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadUsers();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create user.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);

        // Prevent deleting the admin user
        if ("admin".equals(username)) {
            JOptionPane.showMessageDialog(this, "Cannot delete the default admin account.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete user '" + username + "'?\nThis action cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (userDAO.delete(userId)) {
                JOptionPane.showMessageDialog(this, "User deleted.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete user.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        usernameField.setText("");
        passwordField.setText("");
        emailField.setText("");
        phoneField.setText("");
        roleCombo.setSelectedIndex(0);
    }

    @Override
    public void refresh() {
        loadUsers();
    }
}
