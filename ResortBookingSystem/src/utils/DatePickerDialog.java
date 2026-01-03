package utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * A simple modal dialog for selecting a date from a calendar view.
 */
public class DatePickerDialog extends JDialog {
    private LocalDate selectedDate;
    private YearMonth currentYearMonth;
    private JLabel monthLabel;
    private JPanel dayPanel;
    private boolean confirmed = false;

    public DatePickerDialog(Frame parent, LocalDate initialDate) {
        super(parent, "Select Date", true);
        this.currentYearMonth = YearMonth.from(initialDate != null ? initialDate : LocalDate.now());
        this.selectedDate = initialDate;

        initializeUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().setBackground(Color.WHITE);

        // Header: Month Navigation
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JButton prevButton = createNavButton("<");
        prevButton.addActionListener(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            updateCalendar();
        });

        JButton nextButton = createNavButton(">");
        nextButton.addActionListener(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            updateCalendar();
        });

        monthLabel = new JLabel("", JLabel.CENTER);
        monthLabel.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 16));
        monthLabel.setForeground(UITheme.PRIMARY);

        headerPanel.add(prevButton, BorderLayout.WEST);
        headerPanel.add(monthLabel, BorderLayout.CENTER);
        headerPanel.add(nextButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Body: Calendar Grid
        dayPanel = new JPanel(new GridLayout(0, 7, 5, 5));
        dayPanel.setOpaque(false);
        add(dayPanel, BorderLayout.CENTER);

        updateCalendar();
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void updateCalendar() {
        dayPanel.removeAll();
        monthLabel.setText(currentYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));

        // Day Headers
        String[] days = { "Su", "Mo", "Tu", "We", "Th", "Fr", "Sa" };
        for (String d : days) {
            JLabel label = new JLabel(d, JLabel.CENTER);
            label.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 12));
            label.setForeground(Color.GRAY);
            dayPanel.add(label);
        }

        // Days
        LocalDate firstDay = currentYearMonth.atDay(1);
        int emptySlots = firstDay.getDayOfWeek().getValue() % 7; // Sunday=0 for this grid logic

        // LocalDate.getDayOfWeek() returns 1 (Monday) to 7 (Sunday).
        // If 1st is Monday(1), emptySlots should be 1.
        // If 1st is Sunday(7), emptySlots should be 0.
        // If 1st is Tuesday(2), emptySlots should be 2.
        // Wait, standard calendar implies Sun Mon Tue Wed Thu Fri Sat
        // If 1st is Monday, it's the 2nd column (index 1), so 1 empty slot. Correct.
        // If 1st is Sunday, it's the 1st column (index 0), so 0 empty slots. 7 % 7 = 0.
        // Correct.

        for (int i = 0; i < emptySlots; i++) {
            dayPanel.add(new JLabel(""));
        }

        int daysInMonth = currentYearMonth.lengthOfMonth();
        for (int i = 1; i <= daysInMonth; i++) {
            int day = i;
            LocalDate date = currentYearMonth.atDay(day);
            JButton dayBtn = new JButton(String.valueOf(day));
            dayBtn.setFocusPainted(false);
            dayBtn.setBorderPainted(false);
            dayBtn.setBackground(Color.WHITE);
            dayBtn.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 12));

            if (date.equals(selectedDate)) {
                dayBtn.setBackground(UITheme.PRIMARY);
                dayBtn.setForeground(Color.WHITE);
                dayBtn.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 12));
            } else if (date.equals(LocalDate.now())) {
                dayBtn.setForeground(UITheme.PRIMARY);
                dayBtn.setBorder(BorderFactory.createLineBorder(UITheme.PRIMARY));
            }

            // Disable past dates
            if (date.isBefore(LocalDate.now())) {
                dayBtn.setEnabled(false);
                dayBtn.setForeground(Color.LIGHT_GRAY);
            } else {
                dayBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                dayBtn.addActionListener(e -> {
                    selectedDate = date;
                    confirmed = true;
                    dispose();
                });
            }

            dayPanel.add(dayBtn);
        }

        dayPanel.revalidate();
        dayPanel.repaint();
        pack();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }
}
