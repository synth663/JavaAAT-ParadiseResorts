package views.customer;

import dao.ResortDAO;
import models.Resort;
import utils.UITheme;
import views.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Panel for browsing and selecting resorts.
 */
public class ResortBrowsePanel extends JPanel {
    private MainFrame mainFrame;
    private ResortDAO resortDAO;
    private JPanel resortCardsPanel;

    public ResortBrowsePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.resortDAO = new ResortDAO();
        initializeUI();
        loadResorts();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Select Your Dream Resort");
        titleLabel.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 22));
        titleLabel.setForeground(new Color(44, 62, 80));

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 12));
        refreshBtn.addActionListener(e -> loadResorts());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(refreshBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Resort cards container
        resortCardsPanel = new JPanel();
        resortCardsPanel.setLayout(new BoxLayout(resortCardsPanel, BoxLayout.Y_AXIS));
        resortCardsPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(resortCardsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadResorts() {
        resortCardsPanel.removeAll();

        List<Resort> resorts = resortDAO.getAll();

        if (resorts.isEmpty()) {
            JLabel noDataLabel = new JLabel("No resorts available. Please contact admin.");
            noDataLabel.setFont(new Font(UITheme.getFontFamily(), Font.ITALIC, 16));
            noDataLabel.setForeground(new Color(149, 165, 166));
            resortCardsPanel.add(noDataLabel);
        } else {
            for (Resort resort : resorts) {
                resortCardsPanel.add(createResortCard(resort));
                resortCardsPanel.add(Box.createVerticalStrut(15));
            }
        }

        resortCardsPanel.revalidate();
        resortCardsPanel.repaint();
    }

    private JPanel createResortCard(Resort resort) {
        JPanel card = new JPanel(new BorderLayout(15, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        // Resort icon placeholder
        JLabel iconLabel = new JLabel("[RESORT]");
        iconLabel.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 12));
        iconLabel.setForeground(new Color(149, 165, 166));
        iconLabel.setPreferredSize(new Dimension(70, 50));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(iconLabel, BorderLayout.WEST);

        // Resort details
        JPanel detailsPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        detailsPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(resort.getName());
        nameLabel.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 18));
        nameLabel.setForeground(new Color(44, 62, 80));

        JLabel locationLabel = new JLabel(resort.getLocation());
        locationLabel.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 14));
        locationLabel.setForeground(new Color(41, 128, 185));

        JLabel descLabel = new JLabel(resort.getDescription() != null ? resort.getDescription() : "");
        descLabel.setFont(new Font(UITheme.getFontFamily(), Font.ITALIC, 13));
        descLabel.setForeground(new Color(149, 165, 166));

        detailsPanel.add(nameLabel);
        detailsPanel.add(locationLabel);
        detailsPanel.add(descLabel);
        card.add(detailsPanel, BorderLayout.CENTER);

        // Select button
        JButton selectBtn = new JButton("Select Resort");
        selectBtn.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 14));
        selectBtn.setBackground(new Color(46, 204, 113));
        selectBtn.setForeground(Color.WHITE);
        selectBtn.setFocusPainted(false);
        selectBtn.setBorderPainted(false);
        selectBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        selectBtn.setPreferredSize(new Dimension(130, 40));

        selectBtn.addActionListener(e -> {
            mainFrame.getRoomSelectionPanel().setSelectedResort(resort);
            mainFrame.showPanel("ROOMS");
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(selectBtn);
        card.add(buttonPanel, BorderLayout.EAST);

        return card;
    }
}
