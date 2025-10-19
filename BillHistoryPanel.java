import javax.swing.*;
import javax.swing.table.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class BillHistoryPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTable itemsTable;
    private DefaultTableModel itemsTableModel;
    private BillDAO billDAO = new BillDAO();
    private List<Bill> bills;
    private JButton refreshBtn;
    private JButton exportBtn;

    public BillHistoryPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(250, 240, 230));
        
        // Main background with gradient
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(250, 240, 230),
                        0, getHeight(), new Color(230, 200, 180)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setOpaque(false);
        setOpaque(false);

        // Header with beautiful styling
        JLabel headerLabel = new JLabel("📋 Bill History", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Georgia", Font.BOLD, 32));
        headerLabel.setForeground(new Color(80, 50, 40));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        initMainTable();
        initItemsTable();

        // Control buttons panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        controlPanel.setOpaque(false);
        
        refreshBtn = createStyledButton("Refresh Bills", new Color(150, 90, 60));
        refreshBtn.addActionListener(e -> refresh());
        
        exportBtn = createStyledButton("Export to CSV", new Color(110, 70, 50));
        exportBtn.addActionListener(e -> exportToCSV());
        
        controlPanel.add(refreshBtn);
        controlPanel.add(exportBtn);

        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        add(mainPanel);

        refresh();
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Georgia", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
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

    private void initMainTable() {
        String[] columns = {"Bill ID", "Bill Number", "Customer", "Amount", "Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // All cells read-only
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class; // Bill ID
                if (columnIndex == 3) return Double.class;  // Amount
                return String.class;
            }
        };

        table = new JTable(tableModel);
        styleMainTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(150, 90, 60)), 
                "All Bills",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Georgia", Font.BOLD, 16),
                new Color(80, 50, 40)
            ),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        scrollPane.getViewport().setBackground(new Color(250, 240, 230));
        
        add(scrollPane, BorderLayout.CENTER);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.getSelectedRow();
                if (row >= 0 && bills != null && row < bills.size()) {
                    int billId = bills.get(row).getId();
                    showBillItems(billId);
                }
            }
        });
    }

    private void styleMainTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(32);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(200, 180, 160));
        table.setBackground(new Color(255, 250, 245));
        table.setForeground(new Color(80, 50, 40));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        // Custom renderer for status column
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Color code status column
                if (column == 5) { // Status column
                    String status = value.toString();
                    if ("PAID".equalsIgnoreCase(status) || "COMPLETED".equalsIgnoreCase(status)) {
                        c.setBackground(new Color(200, 255, 200));
                        c.setForeground(Color.BLACK);
                    } else if ("PENDING".equalsIgnoreCase(status)) {
                        c.setBackground(new Color(255, 255, 200));
                        c.setForeground(Color.BLACK);
                    } else if ("FAILED".equalsIgnoreCase(status) || "CANCELLED".equalsIgnoreCase(status)) {
                        c.setBackground(new Color(255, 200, 200));
                        c.setForeground(Color.BLACK);
                    } else {
                        c.setBackground(new Color(255, 250, 245));
                        c.setForeground(new Color(80, 50, 40));
                    }
                } else {
                    c.setBackground(new Color(255, 250, 245));
                    c.setForeground(new Color(80, 50, 40));
                }

                if (isSelected) {
                    c.setBackground(new Color(150, 90, 60));
                    c.setForeground(Color.WHITE);
                }

                return c;
            }
        });

        // Simple alignment using fully qualified names
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Bill ID
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // Bill Number
        table.getColumnModel().getColumn(2).setCellRenderer(leftRenderer);   // Customer
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);  // Amount
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Date
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Status

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Georgia", Font.BOLD, 14));
        header.setBackground(new Color(150, 90, 60));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
    }

    private void initItemsTable() {
        String[] itemColumns = {"Book Name", "Quantity", "Price", "Total"};
        itemsTableModel = new DefaultTableModel(itemColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) return Integer.class; // Quantity
                if (columnIndex == 2 || columnIndex == 3) return Double.class; // Price, Total
                return String.class;
            }
        };

        itemsTable = new JTable(itemsTableModel);
        styleItemsTable(itemsTable);
    }

    private void styleItemsTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(26);
        table.setGridColor(new Color(200, 180, 160));
        table.setBackground(new Color(255, 250, 245));
        table.setForeground(new Color(80, 50, 40));
        
        // Simple alignment using fully qualified names
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);  // Book Name
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // Quantity
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer); // Price
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer); // Total

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Georgia", Font.BOLD, 13));
        header.setBackground(new Color(150, 90, 60));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
    }

    private void showBillItems(int billId) {
        try {
            List<BillItem> items = billDAO.getBillItemsByBillId(billId);
            itemsTableModel.setRowCount(0);

            double totalAmount = 0;
            for (BillItem item : items) {
                double itemTotal = item.getUnitPrice() * item.getQuantity();
                totalAmount += itemTotal;
                itemsTableModel.addRow(new Object[]{
                    item.getBookName(), 
                    item.getQuantity(),
                    item.getUnitPrice(),
                    itemTotal
                });
            }

            // Create a detailed view panel
            JPanel detailsPanel = new JPanel(new BorderLayout(10, 10));
            detailsPanel.setBackground(new Color(250, 240, 230));
            detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Bill summary
            Bill bill = bills.stream()
                .filter(b -> b.getId() == billId)
                .findFirst()
                .orElse(null);

            if (bill != null) {
                JPanel summaryPanel = new JPanel(new GridLayout(0, 2, 10, 5));
                summaryPanel.setBackground(new Color(250, 240, 230));
                summaryPanel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(150, 90, 60)),
                    "Bill Summary",
                    javax.swing.border.TitledBorder.LEFT,
                    javax.swing.border.TitledBorder.TOP,
                    new Font("Georgia", Font.BOLD, 14),
                    new Color(80, 50, 40)
                ));

                summaryPanel.add(createSummaryLabel("Bill Number:"));
                summaryPanel.add(createSummaryValue(bill.getBillNumber()));
                summaryPanel.add(createSummaryLabel("Customer:"));
                summaryPanel.add(createSummaryValue(bill.getCustomerName()));
                summaryPanel.add(createSummaryLabel("Date:"));
                summaryPanel.add(createSummaryValue(bill.getDate()));
                summaryPanel.add(createSummaryLabel("Status:"));
                summaryPanel.add(createSummaryValue(bill.getPaymentStatus()));
                summaryPanel.add(createSummaryLabel("Total Amount:"));
                summaryPanel.add(createSummaryValue(String.format("₹%.2f", totalAmount)));

                detailsPanel.add(summaryPanel, BorderLayout.NORTH);
            }

            // Items table
            JScrollPane itemsScroll = new JScrollPane(itemsTable);
            itemsScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(150, 90, 60)),
                "Items Purchased",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Georgia", Font.BOLD, 14),
                new Color(80, 50, 40)
            ));
            itemsScroll.setPreferredSize(new Dimension(500, 250));

            detailsPanel.add(itemsScroll, BorderLayout.CENTER);

            JOptionPane.showMessageDialog(this,
                    detailsPanel,
                    "Bill Details - " + (bill != null ? bill.getBillNumber() : "Unknown"),
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading bill items: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel createSummaryLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Georgia", Font.BOLD, 12));
        label.setForeground(new Color(80, 50, 40));
        return label;
    }

    private JLabel createSummaryValue(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setForeground(new Color(80, 50, 40));
        return label;
    }

    public void refresh() {
        tableModel.setRowCount(0);
        try {
            bills = billDAO.getAllBills();
            if (bills != null) {
                for (Bill b : bills) {
                    tableModel.addRow(new Object[]{
                            b.getId(),
                            b.getBillNumber(),
                            b.getCustomerName(),
                            b.getFinalAmount(),
                            b.getDate(),
                            b.getPaymentStatus()
                    });
                }

                if (!bills.isEmpty()) {
                    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
                    table.setRowSorter(sorter);
                    List<SortKey> sortKeys = new ArrayList<>();
                    sortKeys.add(new SortKey(0, SortOrder.DESCENDING)); // Sort by Bill ID
                    sorter.setSortKeys(sortKeys);
                    
                    JOptionPane.showMessageDialog(this, 
                        "Loaded " + bills.size() + " bills successfully!", 
                        "Refresh Complete", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "No bills found in the database.", 
                        "Information", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to load bills from database.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading bill history: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Bill History");
        fileChooser.setSelectedFile(new java.io.File("bill_history.csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                // Write header
                writer.println("Bill ID,Bill Number,Customer,Amount,Date,Status");
                
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
                    "Bill history exported successfully to: " + file.getAbsolutePath(),
                    "Export Successful", 
                    JOptionPane.INFORMATION_MESSAGE);

            } catch (java.io.FileNotFoundException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error exporting file: " + e.getMessage(),
                    "Export Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}