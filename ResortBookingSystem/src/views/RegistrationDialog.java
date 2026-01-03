package views;

import dao.UserDAO;
import models.User;
import utils.PasswordUtils;
import utils.UITheme;

import javax.swing.*;
import java.awt.*;

/**
 * Registration dialog for new users.
 */
public class RegistrationDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JTextField phoneField;
    private UserDAO userDAO;

    public RegistrationDialog(JFrame parent) {
        super(parent, "Register New Account", true);
        userDAO = new UserDAO();
        initializeUI();
    }

    private void initializeUI() {
        setSize(420, 480);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        mainPanel.setBackground(new Color(236, 240, 241));

        // Header
        JLabel headerLabel = new JLabel("Create Account", JLabel.CENTER);
        headerLabel.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 24));
        headerLabel.setForeground(new Color(44, 62, 80));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Form using GridBagLayout for proper sizing
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.weightx = 1.0;

        int row = 0;

        // Username
        gbc.gridy = row++;
        formPanel.add(createLabel("Username *"), gbc);
        gbc.gridy = row++;
        usernameField = createTextField();
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridy = row++;
        gbc.insets = new Insets(12, 0, 5, 0);
        formPanel.add(createLabel("Password *"), gbc);
        gbc.gridy = row++;
        gbc.insets = new Insets(5, 0, 5, 0);
        passwordField = createPasswordField();
        formPanel.add(passwordField, gbc);

        // Confirm Password
        gbc.gridy = row++;
        gbc.insets = new Insets(12, 0, 5, 0);
        formPanel.add(createLabel("Confirm Password *"), gbc);
        gbc.gridy = row++;
        gbc.insets = new Insets(5, 0, 5, 0);
        confirmPasswordField = createPasswordField();
        formPanel.add(confirmPasswordField, gbc);

        // Email
        gbc.gridy = row++;
        gbc.insets = new Insets(12, 0, 5, 0);
        formPanel.add(createLabel("Email (optional)"), gbc);
        gbc.gridy = row++;
        gbc.insets = new Insets(5, 0, 5, 0);
        emailField = createTextField();
        formPanel.add(emailField, gbc);

        // Phone
        gbc.gridy = row++;
        gbc.insets = new Insets(12, 0, 5, 0);
        formPanel.add(createLabel("Phone (optional)"), gbc);
        gbc.gridy = row++;
        gbc.insets = new Insets(5, 0, 5, 0);
        phoneField = createTextField();
        formPanel.add(phoneField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setOpaque(false);

        JButton registerBtn = new JButton("Register");
        registerBtn.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 14));
        registerBtn.setBackground(new Color(46, 204, 113));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setPreferredSize(new Dimension(130, 40));
        registerBtn.setFocusPainted(false);
        registerBtn.setBorderPainted(false);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 14));
        cancelBtn.setBackground(new Color(149, 165, 166));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setPreferredSize(new Dimension(130, 40));
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorderPainted(false);
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonPanel.add(registerBtn);
        buttonPanel.add(cancelBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Events
        registerBtn.addActionListener(e -> handleRegistration());
        cancelBtn.addActionListener(e -> dispose());
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 13));
        label.setForeground(new Color(52, 73, 94));
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(300, 35));
        field.setMinimumSize(new Dimension(300, 35));
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(300, 35));
        field.setMinimumSize(new Dimension(300, 35));
        return field;
    }

    private void handleRegistration() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password are required.");
            return;
        }

        if (username.length() < 3) {
            showError("Username must be at least 3 characters.");
            return;
        }

        if (password.length() < 4) {
            showError("Password must be at least 4 characters.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        if (userDAO.usernameExists(username)) {
            showError("Username already exists. Please choose another.");
            return;
        }

        // Create user
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(PasswordUtils.hashPassword(password));
        user.setEmail(email.isEmpty() ? null : email);
        user.setPhone(phone.isEmpty() ? null : phone);
        user.setRole(User.Role.CUSTOMER);

        if (userDAO.create(user)) {
            JOptionPane.showMessageDialog(this,
                    "Registration successful! You can now login.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            showError("Registration failed. Please try again.");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
