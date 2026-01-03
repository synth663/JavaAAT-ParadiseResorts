package utils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.File;

/**
 * Centralized UI Theme utility for consistent modern styling.
 */
public class UITheme {
    // Fonts
    private static Font baseFont;
    private static String fontFamily;

    private static final String FALLBACK_FONT = "DM Sans";

    // Public accessor for font family name
    public static String getFontFamily() {
        return fontFamily != null ? fontFamily : FALLBACK_FONT;
    }

    // Colors - Modern palette
    public static final Color PRIMARY = new Color(37, 99, 235); // Modern blue
    public static final Color PRIMARY_DARK = new Color(29, 78, 216);
    public static final Color SUCCESS = new Color(22, 163, 74); // Modern green
    public static final Color SUCCESS_DARK = new Color(21, 128, 61);
    public static final Color DANGER = new Color(220, 38, 38); // Modern red
    public static final Color WARNING = new Color(245, 158, 11); // Amber

    public static final Color BG_DARK = new Color(30, 41, 59); // Slate 800
    public static final Color BG_MEDIUM = new Color(51, 65, 85); // Slate 700
    public static final Color BG_LIGHT = new Color(241, 245, 249); // Slate 100
    public static final Color BG_WHITE = new Color(255, 255, 255);

    public static final Color TEXT_PRIMARY = new Color(15, 23, 42); // Slate 900
    public static final Color TEXT_SECONDARY = new Color(71, 85, 105); // Slate 600
    public static final Color TEXT_MUTED = new Color(148, 163, 184); // Slate 400
    public static final Color TEXT_WHITE = Color.WHITE;

    public static final Color BORDER = new Color(226, 232, 240); // Slate 200
    public static final Color BORDER_FOCUS = new Color(59, 130, 246);

    // Font sizes
    public static final int SIZE_TITLE = 26;
    public static final int SIZE_HEADING = 20;
    public static final int SIZE_SUBHEADING = 16;
    public static final int SIZE_BODY = 14;
    public static final int SIZE_SMALL = 12;
    public static final int SIZE_CAPTION = 11;

    static {
        initializeFont();
    }

    private static void initializeFont() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        // Try to load DM Sans from fonts/DM_Sans/static directory
        String[] fontFiles = {
                "fonts/DM_Sans/static/DMSans-Regular.ttf",
                "fonts/DM_Sans/static/DMSans-Medium.ttf",
                "fonts/DM_Sans/static/DMSans-Bold.ttf",
                "fonts/DM_Sans/static/DMSans-SemiBold.ttf"
        };

        boolean fontLoaded = false;
        for (String fontPath : fontFiles) {
            try {
                File fontFile = new File(fontPath);
                if (fontFile.exists()) {
                    Font font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
                    ge.registerFont(font);
                    if (baseFont == null) {
                        baseFont = font;
                    }
                    fontLoaded = true;
                    System.out.println("Loaded font: " + fontPath);
                }
            } catch (Exception e) {
                System.err.println("Could not load font: " + fontPath);
            }
        }

        // Use DM Sans if loaded, otherwise fallback to Segoe UI
        if (!fontLoaded || baseFont == null) {
            baseFont = new Font(FALLBACK_FONT, Font.PLAIN, SIZE_BODY);
            fontFamily = FALLBACK_FONT;
            System.out.println("Using fallback font: " + FALLBACK_FONT);
        } else {
            fontFamily = baseFont.getFamily();
            System.out.println("Using DM Sans font family: " + fontFamily);
        }
    }

    // Font getters with different weights
    public static Font getFont(int style, int size) {
        // Use the font family from baseFont (will be DM Sans if loaded, else fallback)
        return new Font(baseFont.getFamily(), style, size);
    }

    public static Font title() {
        return getFont(Font.BOLD, SIZE_TITLE);
    }

    public static Font heading() {
        return getFont(Font.BOLD, SIZE_HEADING);
    }

    public static Font subheading() {
        return getFont(Font.BOLD, SIZE_SUBHEADING);
    }

    public static Font body() {
        return getFont(Font.PLAIN, SIZE_BODY);
    }

    public static Font bodyBold() {
        return getFont(Font.BOLD, SIZE_BODY);
    }

    public static Font small() {
        return getFont(Font.PLAIN, SIZE_SMALL);
    }

    public static Font caption() {
        return getFont(Font.PLAIN, SIZE_CAPTION);
    }

    public static Font label() {
        return getFont(Font.BOLD, SIZE_SMALL);
    }

    // Component styling methods
    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(bodyBold());
        btn.setBackground(PRIMARY);
        btn.setForeground(TEXT_WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 40));
        return btn;
    }

    public static JButton createSuccessButton(String text) {
        JButton btn = createPrimaryButton(text);
        btn.setBackground(SUCCESS);
        return btn;
    }

    public static JButton createDangerButton(String text) {
        JButton btn = createPrimaryButton(text);
        btn.setBackground(DANGER);
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(bodyBold());
        btn.setBackground(BG_LIGHT);
        btn.setForeground(TEXT_PRIMARY);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 40));
        return btn;
    }

    public static JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(body());
        field.setPreferredSize(new Dimension(200, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        return field;
    }

    public static JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(body());
        field.setPreferredSize(new Dimension(200, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        return field;
    }

    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label());
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

    public static JLabel createHeading(String text) {
        JLabel label = new JLabel(text);
        label.setFont(heading());
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    public static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16));
    }

    public static void applyGlobalTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Apply custom fonts to UI defaults
            Font regularFont = body();
            Font boldFont = bodyBold();

            UIManager.put("Button.font", boldFont);
            UIManager.put("Label.font", regularFont);
            UIManager.put("TextField.font", regularFont);
            UIManager.put("PasswordField.font", regularFont);
            UIManager.put("TextArea.font", regularFont);
            UIManager.put("ComboBox.font", regularFont);
            UIManager.put("Table.font", regularFont);
            UIManager.put("TableHeader.font", boldFont);
            UIManager.put("TabbedPane.font", boldFont);
            UIManager.put("Menu.font", boldFont);
            UIManager.put("MenuItem.font", regularFont);
            UIManager.put("TitledBorder.font", boldFont);
            UIManager.put("List.font", regularFont);
            UIManager.put("Tree.font", regularFont);
            UIManager.put("OptionPane.messageFont", regularFont);
            UIManager.put("OptionPane.buttonFont", boldFont);

            // Colors
            UIManager.put("Panel.background", BG_LIGHT);
            UIManager.put("Button.background", PRIMARY);
            UIManager.put("Button.foreground", TEXT_WHITE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
