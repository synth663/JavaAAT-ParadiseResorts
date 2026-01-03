package views;

import models.User;
import utils.UITheme;
import views.customer.*;

import javax.swing.*;
import java.awt.*;

/**
 * Main application frame for customers.
 */
public class MainFrame extends JFrame {
    private User currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Panels
    private ResortBrowsePanel resortBrowsePanel;
    private RoomSelectionPanel roomSelectionPanel;
    private CuisineSelectionPanel cuisineSelectionPanel;
    private BookingSummaryPanel bookingSummaryPanel;
    private InvoiceViewPanel invoiceViewPanel;
    private MyBookingsPanel myBookingsPanel;

    public MainFrame(User user) {
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Resort Reservation System - Welcome, " + currentUser.getUsername());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Menu bar
        setJMenuBar(createMenuBar());

        // Header panel
        JPanel headerPanel = createHeaderPanel();

        // Content panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(236, 240, 241));

        // Initialize panels
        resortBrowsePanel = new ResortBrowsePanel(this);
        roomSelectionPanel = new RoomSelectionPanel(this);
        cuisineSelectionPanel = new CuisineSelectionPanel(this);
        bookingSummaryPanel = new BookingSummaryPanel(this);
        invoiceViewPanel = new InvoiceViewPanel(this);
        myBookingsPanel = new MyBookingsPanel(this);

        contentPanel.add(resortBrowsePanel, "BROWSE");
        contentPanel.add(roomSelectionPanel, "ROOMS");
        contentPanel.add(cuisineSelectionPanel, "CUISINE");
        contentPanel.add(bookingSummaryPanel, "SUMMARY");
        contentPanel.add(invoiceViewPanel, "INVOICES");
        contentPanel.add(myBookingsPanel, "BOOKINGS");

        // Layout
        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // Show initial panel
        showPanel("BROWSE");
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(236, 240, 241));
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(189, 195, 199)));
        menuBar.setOpaque(true);

        // Bookings menu
        JMenu bookingMenu = new JMenu("Bookings");
        bookingMenu.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 13));
        bookingMenu.setForeground(new Color(44, 62, 80));
        bookingMenu.setOpaque(true);
        bookingMenu.setBackground(new Color(236, 240, 241));

        JMenuItem newBooking = new JMenuItem("New Booking");
        newBooking.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 13));
        newBooking.addActionListener(e -> showPanel("BROWSE"));

        JMenuItem myBookings = new JMenuItem("My Bookings");
        myBookings.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 13));
        myBookings.addActionListener(e -> {
            myBookingsPanel.loadBookings();
            showPanel("BOOKINGS");
        });

        JMenuItem myInvoices = new JMenuItem("My Invoices");
        myInvoices.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 13));
        myInvoices.addActionListener(e -> {
            invoiceViewPanel.loadInvoices();
            showPanel("INVOICES");
        });

        bookingMenu.add(newBooking);
        bookingMenu.add(myBookings);
        bookingMenu.addSeparator();
        bookingMenu.add(myInvoices);

        // Account menu
        JMenu accountMenu = new JMenu("Account");
        accountMenu.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 13));
        accountMenu.setForeground(new Color(44, 62, 80));
        accountMenu.setOpaque(true);
        accountMenu.setBackground(new Color(236, 240, 241));

        JMenuItem logout = new JMenuItem("Logout");
        logout.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 13));
        logout.addActionListener(e -> logout());

        accountMenu.add(logout);

        menuBar.add(bookingMenu);
        menuBar.add(accountMenu);

        return menuBar;
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
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
        // panel.setBackground(UITheme.PRIMARY); // Replaced by gradient
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Paradise Resorts");
        titleLabel.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel userLabel = new JLabel("Logged in as: " + currentUser.getUsername());
        userLabel.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 14));
        userLabel.setForeground(new Color(189, 195, 199));

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(userLabel, BorderLayout.EAST);

        return panel;
    }

    public void showPanel(String name) {
        cardLayout.show(contentPanel, name);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public RoomSelectionPanel getRoomSelectionPanel() {
        return roomSelectionPanel;
    }

    public CuisineSelectionPanel getCuisineSelectionPanel() {
        return cuisineSelectionPanel;
    }

    public BookingSummaryPanel getBookingSummaryPanel() {
        return bookingSummaryPanel;
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
}
