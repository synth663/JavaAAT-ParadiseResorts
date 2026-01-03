package views.admin;

import models.User;
import utils.UITheme;
import views.LoginFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Admin dashboard with tabbed management panels.
 */
public class AdminDashboardFrame extends JFrame {
    private User currentUser;
    private JTabbedPane tabbedPane;

    public AdminDashboardFrame(User user) {
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Resort Admin Dashboard - " + currentUser.getUsername());
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Menu bar
        setJMenuBar(createMenuBar());

        // Header
        JPanel headerPanel = createHeaderPanel();

        // Tabbed pane for management panels
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 14));

        tabbedPane.addTab("Resorts", new ResortManagementPanel());
        tabbedPane.addTab("Rooms", new RoomManagementPanel());
        tabbedPane.addTab("Food Options", new FoodManagementPanel());
        tabbedPane.addTab("Bookings", new BookingManagementPanel());
        tabbedPane.addTab("Users", new UserManagementPanel());

        // Layout
        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(236, 240, 241));
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(189, 195, 199)));
        menuBar.setOpaque(true);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 13));
        fileMenu.setForeground(new Color(44, 62, 80));
        fileMenu.setOpaque(true);
        fileMenu.setBackground(new Color(236, 240, 241));

        JMenuItem refreshItem = new JMenuItem("Refresh All");
        refreshItem.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 13));
        refreshItem.addActionListener(e -> refreshAllPanels());

        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 13));
        logoutItem.addActionListener(e -> logout());

        fileMenu.add(refreshItem);
        fileMenu.addSeparator();
        fileMenu.add(logoutItem);

        menuBar.add(fileMenu);

        return menuBar;
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(192, 57, 43));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel adminLabel = new JLabel("Administrator: " + currentUser.getUsername());
        adminLabel.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 14));
        adminLabel.setForeground(new Color(236, 240, 241));

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(adminLabel, BorderLayout.EAST);

        return panel;
    }

    private void refreshAllPanels() {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            Component comp = tabbedPane.getComponentAt(i);
            if (comp instanceof RefreshablePanel) {
                ((RefreshablePanel) comp).refresh();
            }
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    // Interface for refreshable panels
    public interface RefreshablePanel {
        void refresh();
    }
}
