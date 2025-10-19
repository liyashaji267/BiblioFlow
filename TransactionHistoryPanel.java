import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class TransactionHistoryPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable transactionTable;
    private BillDAO billDAO;

    public TransactionHistoryPanel() {
        this.billDAO = new BillDAO();
        initializeUI();
        refresh();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ---------- TITLE ----------
        JLabel titleLabel = new JLabel("Transaction History", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 24));
        titleLabel.setForeground(new Color(80, 50, 40));
        add(titleLabel, BorderLayout.NORTH);

        // ---------- TABLE ----------
        String[] columns = {
            "Transaction ID", "Bill Number", "Customer", "Payment Method", 
            "Amount", "Status", "Date & Time"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionTable = new JTable(tableModel);
        transactionTable.setRowHeight(28);
        transactionTable.setFillsViewportHeight(true);
        transactionTable.setAutoCreateRowSorter(true);

        // Custom renderer for status and selected row
        transactionTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (column == 5) { // Status column
                    String status = value.toString();
                    if ("SUCCESS".equalsIgnoreCase(status)) {
                        c.setBackground(new Color(200, 255, 200));
                        c.setForeground(Color.BLACK);
                    } else if ("FAILED".equalsIgnoreCase(status)) {
                        c.setBackground(new Color(255, 200, 200));
                        c.setForeground(Color.BLACK);
                    } else if ("PENDING".equalsIgnoreCase(status)) {
                        c.setBackground(new Color(255, 255, 200));
                        c.setForeground(Color.BLACK);
                    } else {
                        c.setBackground(new Color(245, 235, 220));
                        c.setForeground(new Color(80, 50, 40));
                    }
                } else {
                    c.setBackground(new Color(245, 235, 220));
                    c.setForeground(new Color(80, 50, 40));
                }

                if (isSelected) {
                    c.setBackground(new Color(150, 90, 60));
                    c.setForeground(Color.WHITE);
                }

                return c;
            }
        });

        // Header styling
        JTableHeader header = transactionTable.getTableHeader();
        header.setBackground(new Color(150, 90, 60));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Georgia", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(150, 90, 60)), "All Transactions", 
            TitledBorder.LEADING, TitledBorder.TOP, new Font("Georgia", Font.BOLD, 14), new Color(80, 50, 40)
        ));

        // ---------- CONTROL PANEL ----------
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlPanel.setBackground(new Color(245, 235, 220));

        JButton refreshBtn = createStyledButton("Refresh", new Color(150, 90, 60));
        refreshBtn.addActionListener(e -> refresh());

        JButton exportBtn = createStyledButton("Export to CSV", new Color(110, 70, 50));
        exportBtn.addActionListener(e -> exportToCSV());

        controlPanel.add(refreshBtn);
        controlPanel.add(exportBtn);

        add(scrollPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Georgia", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 35));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        return button;
    }

    public void refresh() {
        tableModel.setRowCount(0);
        List<Transaction> transactions = billDAO.getTransactionHistory();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        for (Transaction t : transactions) {
            tableModel.addRow(new Object[]{
                t.getId(),
                t.getBillNumber(),
                t.getCustomerName(),
                t.getPaymentMethod(),
                String.format("₹%.2f", t.getAmount()),
                t.getStatus(),
                dateFormat.format(t.getTransactionDate())
            });
        }
    }

    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Transaction History");
        fileChooser.setSelectedFile(new File("transaction_history.csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(file)) {
                // Write header
                writer.println("Transaction ID,Bill Number,Customer,Payment Method,Amount,Status,Date Time");

                // Write data
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        if (j > 0) sb.append(",");
                        String value = tableModel.getValueAt(i, j).toString();
                        if (value.contains(",") || value.contains("\"")) {
                            value = "\"" + value.replace("\"", "\"\"") + "\"";
                        }
                        sb.append(value);
                    }
                    writer.println(sb.toString());
                }

                JOptionPane.showMessageDialog(this, 
                    "Transaction history exported successfully to: " + file.getAbsolutePath(),
                    "Export Successful", 
                    JOptionPane.INFORMATION_MESSAGE);

            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error exporting file: " + e.getMessage(),
                    "Export Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
