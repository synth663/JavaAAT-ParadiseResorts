import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ResortBookingApp extends JFrame {
    // UI Components
    private JTextField nameField;
    private JComboBox<String> roomTypeCombo;
    private JSpinner staySpinner;
    private JTextArea displayArea;
    private JButton bookButton, clearButton;

    public ResortBookingApp() {
        // Window Setup
        setTitle("Paradise Reach Resort Booking");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // 1. Header Section
        JLabel header = new JLabel("Resort Reservation System", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 22));
        header.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(header, BorderLayout.NORTH);

        // 2. Input Form Section
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        formPanel.add(new JLabel("Guest Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Room Type:"));
        String[] rooms = {"Standard ($100)", "Deluxe ($200)", "Luxury Suite ($500)"};
        roomTypeCombo = new JComboBox<>(rooms);
        formPanel.add(roomTypeCombo);

        formPanel.add(new JLabel("Nights:"));
        staySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 30, 1));
        formPanel.add(staySpinner);

        bookButton = new JButton("Confirm Booking");
        clearButton = new JButton("Clear");
        formPanel.add(bookButton);
        formPanel.add(clearButton);

        add(formPanel, BorderLayout.CENTER);

        // 3. Display Section
        displayArea = new JTextArea(8, 30);
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(new JScrollPane(displayArea), BorderLayout.SOUTH);

        // Event Handling
        bookButton.addActionListener(e -> processBooking());
        clearButton.addActionListener(e -> {
            nameField.setText("");
            displayArea.setText("");
        });
    }

    private void processBooking() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a guest name.");
            return;
        }

        String room = (String) roomTypeCombo.getSelectedItem();
        int nights = (int) staySpinner.getValue();
        int rate = room.contains("Standard") ? 100 : room.contains("Deluxe") ? 200 : 500;
        int totalCost = rate * nights;

        String receipt = String.format(
            "--- BOOKING CONFIRMED ---\nGuest: %s\nRoom: %s\nStay: %d night(s)\nTotal Cost: $%d\n-------------------------",
            name, room, nights, totalCost
        );

        displayArea.setText(receipt);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ResortBookingApp().setVisible(true));
    }
}




