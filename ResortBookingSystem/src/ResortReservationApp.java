import database.DatabaseManager;
import utils.UITheme;
import views.LoginFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Main entry point for the Resort Reservation System.
 */
public class ResortReservationApp {
    public static void main(String[] args) {
        // Apply global theme first
        UITheme.applyGlobalTheme();

        // Set Nimbus Look and Feel for modern appearance
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }

            // Apply custom UI defaults after Nimbus
            Font regularFont = UITheme.body();
            Font boldFont = UITheme.bodyBold();

            UIManager.put("defaultFont", regularFont);
            UIManager.put("Button.font", boldFont);
            UIManager.put("Label.font", regularFont);
            UIManager.put("TextField.font", regularFont);
            UIManager.put("PasswordField.font", regularFont);
            UIManager.put("TextArea.font", new Font(regularFont.getFamily(), Font.PLAIN, 13));
            UIManager.put("ComboBox.font", regularFont);
            UIManager.put("Table.font", regularFont);
            UIManager.put("TableHeader.font", boldFont);
            UIManager.put("TabbedPane.font", boldFont);
            UIManager.put("Menu.font", boldFont);
            UIManager.put("MenuItem.font", regularFont);
            UIManager.put("TitledBorder.font", boldFont);
            UIManager.put("OptionPane.messageFont", regularFont);
            UIManager.put("OptionPane.buttonFont", boldFont);

        } catch (Exception e) {
            // Fall back to default
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                // Ignore
            }
        }

        // Initialize database
        System.out.println("Initializing database...");
        DatabaseManager.getInstance();
        System.out.println("Database initialized successfully!");

        // Launch the login frame on EDT
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
