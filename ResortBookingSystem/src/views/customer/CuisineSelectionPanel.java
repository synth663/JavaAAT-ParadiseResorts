package views.customer;

import dao.FoodDAO;
import models.FoodOption;
import models.Resort;
import models.Room;
import utils.UITheme;
import views.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Panel for selecting cuisine and meal plan options.
 */
public class CuisineSelectionPanel extends JPanel {
    private MainFrame mainFrame;
    private FoodDAO foodDAO;

    private Resort selectedResort;
    private Room selectedRoom;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numGuests;
    private FoodOption selectedFoodOption;

    private JLabel headerLabel;
    private JPanel foodCardsPanel;
    private JButton nextButton;
    private JButton skipButton;

    public CuisineSelectionPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.foodDAO = new FoodDAO();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        headerLabel = new JLabel("Select Your Cuisine & Meal Plan");
        headerLabel.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 22));
        headerLabel.setForeground(new Color(44, 62, 80));

        JButton backBtn = new JButton("← Back to Rooms");
        backBtn.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 12));
        backBtn.addActionListener(e -> mainFrame.showPanel("ROOMS"));

        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(backBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Food cards
        foodCardsPanel = new JPanel();
        foodCardsPanel.setLayout(new BoxLayout(foodCardsPanel, BoxLayout.Y_AXIS));
        foodCardsPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(foodCardsPanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Available Meal Plans"));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottomPanel.setOpaque(false);

        skipButton = new JButton("Skip Meal Plan");
        skipButton.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 14));
        skipButton.addActionListener(e -> {
            selectedFoodOption = null;
            proceedToSummary();
        });

        nextButton = new JButton("Next: Review Booking →");
        nextButton.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 14));
        nextButton.setBackground(new Color(52, 152, 219));
        nextButton.setForeground(Color.WHITE);
        nextButton.setFocusPainted(false);
        nextButton.setBorderPainted(false);
        nextButton.setEnabled(false);
        nextButton.addActionListener(e -> proceedToSummary());

        bottomPanel.add(skipButton);
        bottomPanel.add(nextButton);
        add(bottomPanel, BorderLayout.SOUTH);

        loadFoodOptions();
    }

    public void setBookingDetails(Resort resort, Room room, LocalDate checkIn, LocalDate checkOut, int guests) {
        this.selectedResort = resort;
        this.selectedRoom = room;
        this.checkInDate = checkIn;
        this.checkOutDate = checkOut;
        this.numGuests = guests;
        this.selectedFoodOption = null;
        nextButton.setEnabled(false);
        loadFoodOptions();
    }

    private void loadFoodOptions() {
        foodCardsPanel.removeAll();

        List<FoodOption> options = foodDAO.getAll();

        if (options.isEmpty()) {
            JLabel noDataLabel = new JLabel("No meal plans available.");
            noDataLabel.setFont(new Font(UITheme.getFontFamily(), Font.ITALIC, 14));
            foodCardsPanel.add(noDataLabel);
        } else {
            ButtonGroup foodGroup = new ButtonGroup();
            for (FoodOption option : options) {
                JPanel card = createFoodCard(option, foodGroup);
                foodCardsPanel.add(card);
                foodCardsPanel.add(Box.createVerticalStrut(10));
            }
        }

        foodCardsPanel.revalidate();
        foodCardsPanel.repaint();
    }

    private JPanel createFoodCard(FoodOption food, ButtonGroup group) {
        JPanel card = new JPanel(new BorderLayout(15, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        // Radio button
        JRadioButton radioButton = new JRadioButton();
        radioButton.setOpaque(false);
        group.add(radioButton);
        card.add(radioButton, BorderLayout.WEST);

        // Food details
        JPanel detailsPanel = new JPanel(new GridLayout(2, 1));
        detailsPanel.setOpaque(false);

        JLabel typeLabel = new JLabel(food.getCuisineType() + " Cuisine");
        typeLabel.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 16));
        typeLabel.setForeground(new Color(44, 62, 80));

        JLabel planLabel = new JLabel(food.getMealPlan());
        planLabel.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 13));
        planLabel.setForeground(new Color(127, 140, 141));

        detailsPanel.add(typeLabel);
        detailsPanel.add(planLabel);
        card.add(detailsPanel, BorderLayout.CENTER);

        // Price
        JLabel priceLabel = new JLabel("$" + String.format("%.0f", food.getPricePerDay()) + "/day");
        priceLabel.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 18));
        priceLabel.setForeground(new Color(230, 126, 34));
        card.add(priceLabel, BorderLayout.EAST);

        // Selection handler
        radioButton.addActionListener(e -> {
            selectedFoodOption = food;
            nextButton.setEnabled(true);
        });

        // Make whole card clickable
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                radioButton.setSelected(true);
                selectedFoodOption = food;
                nextButton.setEnabled(true);
            }
        });

        return card;
    }

    private void proceedToSummary() {
        mainFrame.getBookingSummaryPanel().setBookingDetails(
                selectedResort,
                selectedRoom,
                selectedFoodOption,
                checkInDate,
                checkOutDate,
                numGuests);
        mainFrame.showPanel("SUMMARY");
    }

    public FoodOption getSelectedFoodOption() {
        return selectedFoodOption;
    }
}
