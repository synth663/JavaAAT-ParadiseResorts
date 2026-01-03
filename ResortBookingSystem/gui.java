import javax.swing.*;
import java.awt.*;
class Room {
    protected int numBeds;
    public Room(int numBeds) { this.numBeds = numBeds; }
}

// Subclass for Calculation
class ResortBooking extends Room {
    private String category;
    private String cuisine;

    public ResortBooking(int numBeds, String category, String cuisine) {
        super(numBeds);
        this.category = category;
        this.cuisine = cuisine;
    }

    public int calculateTotal() {
        int roomPrice = 0;
        // Simple if-else for Room Price
        if (category.equals("Eco")) { roomPrice = 100; }
        else if (category.equals("Premium")) { roomPrice = 200; }
        else { roomPrice = 350; }

        int cuisinePrice = 0;
        // Simple if-else for Cuisine Price
        if (cuisine.equals("Italian ($50)")) { cuisinePrice = 50; }
        else if (cuisine.equals("Continental ($70)")) { cuisinePrice = 70; }
        else { cuisinePrice = 30; } // Local

        return (numBeds * roomPrice) + cuisinePrice;
    }
}
public class gui {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Resort & Spa Manager");
        frame.setSize(450, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 1. Create the Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // --- TAB 1: ROOM BOOKING ---
        JPanel roomPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        JComboBox<Integer> bedCombo = new JComboBox<>(new Integer[]{1, 2, 3});
        JRadioButton eco = new JRadioButton("Eco", true);
        JRadioButton premium = new JRadioButton("Premium");
        JRadioButton business = new JRadioButton("Business");
        ButtonGroup group = new ButtonGroup();
        group.add(eco); group.add(premium); group.add(business);

        JButton nextTabBtn = new JButton("Next: Select Cuisine ➜");
        
        roomPanel.add(new JLabel("Number of Beds:"));
        roomPanel.add(bedCombo);
        roomPanel.add(new JLabel("Room Category:"));
        roomPanel.add(eco); roomPanel.add(premium); roomPanel.add(business);
        roomPanel.add(nextTabBtn);

        // --- TAB 2: CUISINE SELECTOR ---
        JPanel cuisinePanel = new JPanel(new GridLayout(0, 1, 10, 10));
        String[] cuisines = {"Local ($30)", "Italian ($50)", "Continental ($70)"};
        JComboBox<String> cuisineCombo = new JComboBox<>(cuisines);
        
        JButton finalBillBtn = new JButton("Generate Final Bill");
        JTextArea billOutput = new JTextArea();
        billOutput.setEditable(false);

        cuisinePanel.add(new JLabel("Select your Cuisine:"));
        cuisinePanel.add(cuisineCombo);
        cuisinePanel.add(finalBillBtn);
        cuisinePanel.add(billOutput);

        // Add panels to tabs
        tabbedPane.addTab("Room Selection", roomPanel);
        tabbedPane.addTab("Cuisine Selection", cuisinePanel);

        // --- INTERACTION LOGIC ---
        
        // Button to switch tabs
        nextTabBtn.addActionListener(e -> tabbedPane.setSelectedIndex(1));

        // Final Calculation Button
        finalBillBtn.addActionListener(e -> {
            int beds = (int) bedCombo.getSelectedItem();
            String type;
            
            // Simple if-else as requested
            if (eco.isSelected()) {
                type = "Eco";
            } else if (premium.isSelected()) {
                type = "Premium";
            } else {
                type = "Business";
            }

            String selectedCuisine = (String) cuisineCombo.getSelectedItem();
            
            // Call the inheritance class
            ResortBooking booking = new ResortBooking(beds, type, selectedCuisine);
            
            billOutput.setText("--- FINAL INVOICE ---\n" +
                               "Room: " + type + " (" + beds + " beds)\n" +
                               "Cuisine: " + selectedCuisine + "\n" +
                               "Total Amount: $" + booking.calculateTotal());
        });

        frame.add(tabbedPane);
        frame.setVisible(true);
    }
}