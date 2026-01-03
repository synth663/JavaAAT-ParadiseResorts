import javax.swing.*;
import java.awt.*;

// --- LOGIC LAYER (Inheritance) ---

// Base Class
class Room {
    protected int numBeds;
    protected int numNights; // Added from Code 2 logic

    public Room(int numBeds, int numNights) {
        this.numBeds = numBeds;
        this.numNights = numNights;
    }
}

// Subclass for Calculation
class ResortReservation extends Room {
    private String category;
    private String cuisine;

    public ResortReservation(int numBeds, int numNights, String category, String cuisine) {
        super(numBeds, numNights);
        this.category = category;
        this.cuisine = cuisine;
    }

    public int calculateTotal() {
        int roomPricePerNight = 0;
        // Logic from Code 1
        if (category.equals("Eco")) { roomPricePerNight = 100; }
        else if (category.equals("Premium")) { roomPricePerNight = 200; }
        else { roomPricePerNight = 350; } // Business

        int cuisinePricePerDay = 0;
        // Logic from Code 1
        if (cuisine.contains("Italian")) { cuisinePricePerDay = 50; }
        else if (cuisine.contains("Continental")) { cuisinePricePerDay = 70; }
        else { cuisinePricePerDay = 30; } // Local

        // Merged Formula: ((Beds * RoomPrice) + CuisinePrice) * Nights
        int dailyCost = (numBeds * roomPricePerNight) + cuisinePricePerDay;
        return dailyCost * numNights;
    }
}

// --- PRESENTATION LAYER (GUI) ---

public class IntegratedResortSystem {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Grand Integrated Resort System");
        frame.setSize(500, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
        frame.setLayout(new BorderLayout(10, 10));

        // Header 
        JLabel header = new JLabel("Resort Booking & Billing", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        frame.add(header, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        // --- TAB 1: GUEST INFO 
        JPanel guestPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        guestPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField nameField = new JTextField();
        JSpinner staySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 30, 1));
        
        guestPanel.add(new JLabel("Guest Name:"));
        guestPanel.add(nameField);
        guestPanel.add(new JLabel("Duration of Stay (Nights):"));
        guestPanel.add(staySpinner);
        
        JButton toRoomBtn = new JButton("Next: Room Selection ->");
        guestPanel.add(new JLabel("")); 
        guestPanel.add(toRoomBtn);

        // --- TAB 2: ROOM SELECTION (From Code 1) ---
        JPanel roomPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        roomPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JComboBox<Integer> bedCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4});
        JRadioButton eco = new JRadioButton("Eco ($100)", true);
        JRadioButton premium = new JRadioButton("Premium ($200)");
        JRadioButton business = new JRadioButton("Business/Luxury ($350)");
        ButtonGroup group = new ButtonGroup();
        group.add(eco); group.add(premium); group.add(business);

        roomPanel.add(new JLabel("Number of Beds:"));
        roomPanel.add(bedCombo);
        roomPanel.add(new JLabel("Room Category (Price per bed/night):"));
        roomPanel.add(eco); roomPanel.add(premium); roomPanel.add(business);
        
        JButton toCuisineBtn = new JButton("Next: Cuisine ➜");
        roomPanel.add(toCuisineBtn);

        // --- TAB 3: CUISINE & BILLING (Merged) ---
        JPanel finalPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        finalPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] cuisines = {"Local ($30/day)", "Italian ($50/day)", "Continental ($70/day)"};
        JComboBox<String> cuisineCombo = new JComboBox<>(cuisines);
        
        JButton generateBtn = new JButton("Confirm & Generate Invoice");
        generateBtn.setBackground(new Color(60, 179, 113)); // Green color
        generateBtn.setForeground(Color.WHITE);
        
        JTextArea billOutput = new JTextArea(14, 20);
        billOutput.setEditable(false);
        billOutput.setFont(new Font("Monospaced", Font.PLAIN, 12));

        finalPanel.add(new JLabel("Select Daily Meal Plan:"));
        finalPanel.add(cuisineCombo);
        finalPanel.add(new JSeparator());
        finalPanel.add(generateBtn);
        finalPanel.add(new JScrollPane(billOutput));

        // Add Tabs
        tabbedPane.addTab("1. Guest Info", guestPanel);
        tabbedPane.addTab("2. Room Setup", roomPanel);
        tabbedPane.addTab("3. Finalize", finalPanel);
        frame.add(tabbedPane, BorderLayout.CENTER);

        // --- EVENT HANDLING ---

        // Navigation Buttons
        toRoomBtn.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        toCuisineBtn.addActionListener(e -> tabbedPane.setSelectedIndex(2));

        // Calculation Logic
        generateBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if(name.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter Guest Name in Tab 1");
                tabbedPane.setSelectedIndex(0);
                return;
            }

            // Gather Data
            int nights = (int) staySpinner.getValue();
            int beds = (int) bedCombo.getSelectedItem();
            
            String category = "Business";
            if (eco.isSelected()) category = "Eco";
            else if (premium.isSelected()) category = "Premium";

            String selectedCuisine = (String) cuisineCombo.getSelectedItem();

            // Instantiate Logic Class
            ResortReservation res = new ResortReservation(beds, nights, category, selectedCuisine);
            int total = res.calculateTotal();

            // Generate Output
            billOutput.setText(
                "=== OFFICIAL INVOICE ===\n" +
                "Guest:    " + name + "\n" +
                "Stay:     " + nights + " Night(s)\n" +
                "------------------------\n" +
                "Type:     " + category + " Room\n" +
                "Config:   " + beds + " Bed(s)\n" +
                "Dining:   " + selectedCuisine + "\n" +
                "------------------------\n" +
                "TOTAL DUE: $" + total
            );
        });

        frame.setVisible(true);
    }
}