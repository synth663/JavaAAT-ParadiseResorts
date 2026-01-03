package views.customer;

import dao.InvoiceDAO;
import models.Invoice;
import utils.UITheme;
import views.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel for viewing customer invoices.
 */
public class InvoiceViewPanel extends JPanel {
    private MainFrame mainFrame;
    private InvoiceDAO invoiceDAO;
    private JTable invoiceTable;
    private DefaultTableModel tableModel;
    private JTextArea invoiceDetailArea;

    public InvoiceViewPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.invoiceDAO = new InvoiceDAO();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("My Invoices");
        titleLabel.setFont(new Font(UITheme.getFontFamily(), Font.BOLD, 22));
        titleLabel.setForeground(new Color(44, 62, 80));

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadInvoices());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(refreshBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Split pane for table and details
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);

        // Invoice table
        String[] columns = { "Invoice #", "Resort", "Total", "Date" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        invoiceTable = new JTable(tableModel);
        invoiceTable.setFont(new Font(UITheme.getFontFamily(), Font.PLAIN, 13));
        invoiceTable.setRowHeight(30);
        invoiceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showInvoiceDetails();
            }
        });

        JScrollPane tableScroll = new JScrollPane(invoiceTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Invoice List"));
        splitPane.setLeftComponent(tableScroll);

        // Invoice details
        invoiceDetailArea = new JTextArea();
        invoiceDetailArea.setEditable(false);
        invoiceDetailArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        invoiceDetailArea.setBackground(Color.WHITE);
        invoiceDetailArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane detailScroll = new JScrollPane(invoiceDetailArea);
        detailScroll.setBorder(BorderFactory.createTitledBorder("Invoice Details"));
        splitPane.setRightComponent(detailScroll);

        add(splitPane, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setOpaque(false);

        JButton backBtn = new JButton("← Back to Browse");
        backBtn.addActionListener(e -> mainFrame.showPanel("BROWSE"));
        bottomPanel.add(backBtn);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void loadInvoices() {
        tableModel.setRowCount(0);
        invoiceDetailArea.setText("");

        List<Invoice> invoices = invoiceDAO.findByUser(mainFrame.getCurrentUser().getId());
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Invoice inv : invoices) {
            tableModel.addRow(new Object[] {
                    inv.getInvoiceNumber(),
                    inv.getResortName(),
                    String.format("$%.2f", inv.getTotalAmount()),
                    inv.getCreatedAt() != null ? inv.getCreatedAt().format(fmt) : ""
            });
        }

        if (invoices.isEmpty()) {
            invoiceDetailArea.setText("No invoices found.\n\nMake a booking to generate an invoice.");
        }
    }

    private void showInvoiceDetails() {
        int selectedRow = invoiceTable.getSelectedRow();
        if (selectedRow < 0)
            return;

        String invoiceNumber = (String) tableModel.getValueAt(selectedRow, 0);
        List<Invoice> invoices = invoiceDAO.findByUser(mainFrame.getCurrentUser().getId());

        for (Invoice inv : invoices) {
            if (inv.getInvoiceNumber().equals(invoiceNumber)) {
                displayInvoice(inv);
                break;
            }
        }
    }

    private void displayInvoice(Invoice inv) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("         INVOICE                       \n");
        sb.append("═══════════════════════════════════════\n\n");
        sb.append("Invoice #: ").append(inv.getInvoiceNumber()).append("\n");
        sb.append("Date:      ").append(inv.getCreatedAt() != null ? inv.getCreatedAt().format(fmt) : "N/A")
                .append("\n");
        sb.append("Resort:    ").append(inv.getResortName()).append("\n\n");
        sb.append("───────────────────────────────────────\n");
        sb.append("CHARGES:\n");
        sb.append("───────────────────────────────────────\n");
        sb.append(String.format("Room Charges:    $%10.2f\n", inv.getRoomCharges()));
        sb.append(String.format("Food Charges:    $%10.2f\n", inv.getFoodCharges()));
        sb.append(String.format("Taxes:           $%10.2f\n", inv.getTaxes()));
        sb.append("───────────────────────────────────────\n");
        sb.append(String.format("TOTAL:           $%10.2f\n", inv.getTotalAmount()));
        sb.append("═══════════════════════════════════════\n");

        invoiceDetailArea.setText(sb.toString());
        invoiceDetailArea.setCaretPosition(0);
    }
}
