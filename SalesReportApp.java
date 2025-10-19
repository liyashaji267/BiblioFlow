import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.text.ParseException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class SalesReportApp extends JFrame {

    private BillDAO billDAO = new BillDAO();
    private java.util.List<Sale> salesList = new ArrayList<>();

    private JTable salesTable;
    private DefaultTableModel tableModel;
    private JButton generateBtn, exportBtn, chartBtn;
    private JComboBox<String> filterCombo;

    class Sale {
        String bookName;
        int quantity;
        double price;
        Date date;

        public Sale(String bookName, int quantity, double price, Date date) {
            this.bookName = bookName;
            this.quantity = quantity;
            this.price = price;
            this.date = date;
        }
    }

    public SalesReportApp() {
        setTitle("BiblioFlow - Sales Report");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main panel with gradient background
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)) {
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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setContentPane(mainPanel);

        // Header
        JLabel headerLabel = new JLabel("📊 Sales Report Dashboard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Georgia", Font.BOLD, 32));
        headerLabel.setForeground(new Color(80, 50, 40));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Top controls panel with styling
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 90, 60), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel filterLabel = new JLabel("Filter Period:");
        filterLabel.setFont(new Font("Georgia", Font.BOLD, 16));
        filterLabel.setForeground(new Color(80, 50, 40));

        filterCombo = new JComboBox<>(new String[]{"Daily", "Weekly", "Monthly"});
        styleComboBox(filterCombo);

        generateBtn = createStyledButton("Generate Report", new Color(76, 175, 80));
        exportBtn = createStyledButton("Export CSV", new Color(33, 150, 243));
        chartBtn = createStyledButton("Show Chart", new Color(255, 193, 7));

        exportBtn.setEnabled(false);
        chartBtn.setEnabled(false);

        topPanel.add(filterLabel);
        topPanel.add(filterCombo);
        topPanel.add(generateBtn);
        topPanel.add(exportBtn);
        topPanel.add(chartBtn);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Table with styling
        String[] columns = {"Date", "Book", "Quantity", "Unit Price", "Total"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 2) return Integer.class; // Quantity
                if (column == 3 || column == 4) return Double.class; // Prices
                return String.class;
            }
        };
        
        salesTable = new JTable(tableModel);
        styleTable(salesTable);
        
        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(150, 90, 60)), 
                "Sales Data",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Georgia", Font.BOLD, 16),
                new Color(80, 50, 40)
            ),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        scrollPane.getViewport().setBackground(new Color(250, 240, 230));
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Actions
        generateBtn.addActionListener(e -> loadAndDisplaySales());
        exportBtn.addActionListener(e -> exportToCSV());
        chartBtn.addActionListener(e -> showSalesChart());

        setVisible(true);
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(new Font("Arial", Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        combo.setForeground(new Color(80, 50, 40));
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 90, 60), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        combo.setPreferredSize(new Dimension(120, 35));
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Georgia", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
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

    private void styleTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setSelectionBackground(new Color(150, 90, 60));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(200, 180, 160));
        table.setBackground(new Color(255, 250, 245));
        table.setForeground(new Color(80, 50, 40));
        
        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Georgia", Font.BOLD, 14));
        header.setBackground(new Color(150, 90, 60));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
        
        // Simple alignment using existing renderers
        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        javax.swing.table.DefaultTableCellRenderer rightRenderer = new javax.swing.table.DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Quantity
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);  // Unit Price
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);  // Total
    }

    private void loadAndDisplaySales() {
        salesList.clear();
        tableModel.setRowCount(0);

        try {
            java.util.List<Bill> bills = billDAO.getAllBills();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            for (Bill bill : bills) {
                int billId = bill.getId();
                java.util.List<BillItem> items = billDAO.getBillItemsByBillId(billId);
                for (BillItem item : items) {
                    double unitPrice = item.getPrice();
                    try {
                        Date date = sdf.parse(bill.getDate());
                        salesList.add(new Sale(item.getBookName(), item.getQuantity(), unitPrice, date));
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            displayFilteredSales();
            JOptionPane.showMessageDialog(this, "Report generated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading sales data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayFilteredSales() {
        tableModel.setRowCount(0);
        String filter = (String) filterCombo.getSelectedItem();
        Calendar now = Calendar.getInstance();
        double totalRevenue = 0;
        int totalBooks = 0;
        SimpleDateFormat sdfDisplay = new SimpleDateFormat("dd-MM-yyyy");

        for (Sale sale : salesList) {
            boolean include = false;
            Calendar saleCal = Calendar.getInstance();
            saleCal.setTime(sale.date);

            switch (filter) {
                case "Daily": include = sameDay(sale.date, now.getTime()); break;
                case "Weekly": include = sameWeek(sale.date, now.getTime()); break;
                case "Monthly": include = sameMonth(sale.date, now.getTime()); break;
            }

            if (include) {
                double total = sale.price * sale.quantity;
                tableModel.addRow(new Object[]{
                        sdfDisplay.format(sale.date),
                        sale.bookName,
                        sale.quantity,
                        sale.price,
                        total
                });
                totalRevenue += total;
                totalBooks += sale.quantity;
            }
        }

        // Totals row
        if (tableModel.getRowCount() > 0) {
            tableModel.addRow(new Object[]{"", "TOTAL", totalBooks, "", totalRevenue});
        } else {
            tableModel.addRow(new Object[]{"No data found", "", "", "", ""});
        }
        
        exportBtn.setEnabled(tableModel.getRowCount() > 1);
        chartBtn.setEnabled(tableModel.getRowCount() > 1);
    }

    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("biblioflow_sales_report.csv"));
        fileChooser.setDialogTitle("Export Sales Report");
        
        int option = fileChooser.showSaveDialog(this);
        if(option == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter pw = new PrintWriter(fileChooser.getSelectedFile())) {
                // Write header
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    if (i > 0) pw.print(",");
                    pw.print("\"" + tableModel.getColumnName(i) + "\"");
                }
                pw.println();

                // Write data (skip the last total row for cleaner export)
                for (int row = 0; row < tableModel.getRowCount() - 1; row++) {
                    for (int col = 0; col < tableModel.getColumnCount(); col++) {
                        if (col > 0) pw.print(",");
                        Object value = tableModel.getValueAt(row, col);
                        if (value instanceof String) {
                            pw.print("\"" + value + "\"");
                        } else {
                            pw.print(value);
                        }
                    }
                    pw.println();
                }
                JOptionPane.showMessageDialog(this, "Report exported successfully!", "Export Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Export failed: " + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showSalesChart() {
        String filter = (String) filterCombo.getSelectedItem();
        Map<String, Double> chartData = new LinkedHashMap<>();
        SimpleDateFormat sdfKey;

        switch (filter) {
            case "Daily": sdfKey = new SimpleDateFormat("dd-MM-yyyy"); break;
            case "Weekly": sdfKey = new SimpleDateFormat("ww-yyyy"); break;
            default: sdfKey = new SimpleDateFormat("MMM yyyy"); break;
        }

        for (Sale sale : salesList) {
            String key = sdfKey.format(sale.date);
            double total = sale.price * sale.quantity;
            chartData.put(key, chartData.getOrDefault(key, 0.0) + total);
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Double> entry : chartData.entrySet()) {
            dataset.addValue(entry.getValue(), "Revenue", entry.getKey());
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "BiblioFlow Sales Revenue - " + filter,
                filter + " Period",
                "Revenue (₹)",
                dataset
        );

        // Style the chart
        barChart.getTitle().setFont(new Font("Georgia", Font.BOLD, 20));
        barChart.setBackgroundPaint(new Color(250, 240, 230));
        barChart.getPlot().setBackgroundPaint(new Color(255, 250, 245));

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(800, 500));
        
        JFrame chartFrame = new JFrame("BiblioFlow Sales Chart");
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.add(chartPanel);
        chartFrame.pack();
        chartFrame.setLocationRelativeTo(this);
        chartFrame.setVisible(true);
    }

    private boolean sameDay(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance(); c1.setTime(d1);
        Calendar c2 = Calendar.getInstance(); c2.setTime(d2);
        return c1.get(Calendar.YEAR)==c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR)==c2.get(Calendar.DAY_OF_YEAR);
    }

    private boolean sameWeek(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance(); c1.setTime(d1);
        Calendar c2 = Calendar.getInstance(); c2.setTime(d2);
        return c1.get(Calendar.YEAR)==c2.get(Calendar.YEAR) &&
                c1.get(Calendar.WEEK_OF_YEAR)==c2.get(Calendar.WEEK_OF_YEAR);
    }

    private boolean sameMonth(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance(); c1.setTime(d1);
        Calendar c2 = Calendar.getInstance(); c2.setTime(d2);
        return c1.get(Calendar.YEAR)==c2.get(Calendar.YEAR) &&
                c1.get(Calendar.MONTH)==c2.get(Calendar.MONTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SalesReportApp();
        });
    }
}