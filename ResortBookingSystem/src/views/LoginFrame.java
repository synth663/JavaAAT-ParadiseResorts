package views;

import dao.UserDAO;
import models.User;
import utils.UITheme;
import views.admin.AdminDashboardFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Login frame for user authentication.
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private UserDAO userDAO;

    public LoginFrame() {
        userDAO = new UserDAO();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Resort Reservation System - Login");
        setSize(480, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with modern gradient background
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gradient = new GradientPaint(0, 0, UITheme.BG_MEDIUM, 0, getHeight(),
                        UITheme.BG_DARK);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Header
        JLabel headerLabel = new JLabel("Paradise Resorts", JLabel.CENTER);
        headerLabel.setFont(UITheme.title());
        headerLabel.setForeground(UITheme.TEXT_WHITE);

        JLabel subtitleLabel = new JLabel("Reservation System", JLabel.CENTER);
        subtitleLabel.setFont(UITheme.body());
        subtitleLabel.setForeground(UITheme.TEXT_MUTED);

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        headerPanel.setOpaque(false);
        headerPanel.add(headerLabel);
        headerPanel.add(subtitleLabel);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        JLabel userLabel = new JLabel("Username");
        userLabel.setForeground(UITheme.TEXT_MUTED);
        userLabel.setFont(UITheme.label());
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(userLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setFont(UITheme.body());
        usernameField.setPreferredSize(new Dimension(220, 40));
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(usernameField, gbc);

        // Password
        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(UITheme.TEXT_MUTED);
        passLabel.setFont(UITheme.label());
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(passLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(UITheme.body());
        passwordField.setPreferredSize(new Dimension(220, 40));
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(passwordField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);

        loginButton = createStyledButton("Login", UITheme.SUCCESS);
        registerButton = createStyledButton("Register", UITheme.PRIMARY);

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.insets = new Insets(25, 10, 10, 10);
        formPanel.add(buttonPanel, gbc);

        // Add components
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Footer
        JLabel footerLabel = new JLabel("Admin: admin / admin123", JLabel.CENTER);
        footerLabel.setForeground(UITheme.TEXT_MUTED);
        footerLabel.setFont(UITheme.caption());
        mainPanel.add(footerLabel, BorderLayout.SOUTH);

        add(mainPanel);

        // Event handlers
        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> showRegistrationDialog());

        // Enter key support
        passwordField.addActionListener(e -> handleLogin());

        getRootPane().setDefaultButton(loginButton);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(UITheme.bodyBold());
        button.setBackground(bgColor);
        button.setForeground(UITheme.TEXT_WHITE);
        button.setPreferredSize(new Dimension(130, 44));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = userDAO.authenticate(username, password);

        if (user != null) {
            dispose();
            if (user.isAdmin()) {
                new AdminDashboardFrame(user).setVisible(true);
            } else {
                new MainFrame(user).setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }

    private void showRegistrationDialog() {
        new RegistrationDialog(this).setVisible(true);
    }
}
