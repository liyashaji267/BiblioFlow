import javax.swing.*;
import javax.swing.border.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StoreReports extends JPanel {
    private BookDAO bookDAO;
    private BillDAO billDAO;
    private JTabbedPane tabbedPane;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");

    public StoreReports() {
        this.bookDAO = new BookDAO();
        this.billDAO = new BillDAO();
        initializeUI();
        loadChartData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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

        // Header with beautiful styling
        JLabel headerLabel = new JLabel("Store Reports & Analytics", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Georgia", Font.BOLD, 36));
        headerLabel.setForeground(new Color(80, 50, 40));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        // Create tabbed pane with custom styling
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Georgia", Font.BOLD, 16));
        tabbedPane.setBackground(new Color(250, 240, 230));
        tabbedPane.setForeground(new Color(80, 50, 40));
        
        // Remove default border and set custom one
        tabbedPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 90, 60), 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Custom tab renderer
        UIManager.put("TabbedPane.background", new Color(250, 240, 230));
        UIManager.put("TabbedPane.foreground", new Color(80, 50, 40));
        UIManager.put("TabbedPane.selected", new Color(205, 155, 125));

        // Sales Report Tab
        JPanel salesPanel = createSalesChartPanel();
        tabbedPane.addTab("📊 Sales Report", salesPanel);

        // Profit/Loss Tab
        JPanel profitPanel = createProfitLossChartPanel();
        tabbedPane.addTab("💰 Profit/Loss", profitPanel);

        // Inventory Status Tab
        JPanel inventoryPanel = createInventoryChartPanel();
        tabbedPane.addTab("📚 Inventory Status", inventoryPanel);

        // Customer Analytics Tab
        JPanel customerPanel = createCustomerAnalyticsPanel();
        tabbedPane.addTab("👥 Customer Analytics", customerPanel);

        // Refresh button with custom styling
        JButton refreshBtn = createStyledButton("Refresh Data", new Color(150, 90, 60));
        refreshBtn.addActionListener(e -> loadChartData());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buttonPanel.add(refreshBtn);

        // Add all components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Georgia", Font.BOLD, 16));
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

    private JPanel createChartContainer(JFreeChart chart) {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 90, 60), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 500));
        chartPanel.setBackground(new Color(250, 240, 230));
        
        // Customize chart appearance
        chart.setBackgroundPaint(new Color(250, 240, 230));
        chart.getPlot().setBackgroundPaint(new Color(255, 250, 245));
        
        container.add(chartPanel, BorderLayout.CENTER);
        return container;
    }

    private JPanel createSalesChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultCategoryDataset salesDataset = createSalesDataset();

        JFreeChart salesChart = ChartFactory.createLineChart(
            "Monthly Sales Report",
            "Month",
            "Sales Amount (₹)",
            salesDataset
        );
        salesChart.getTitle().setFont(new Font("Georgia", Font.BOLD, 20));
        salesChart.getTitle().setPaint(new Color(80, 50, 40));

        panel.add(createChartContainer(salesChart), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createProfitLossChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultCategoryDataset profitDataset = createProfitLossDataset();

        JFreeChart profitChart = ChartFactory.createBarChart(
            "Monthly Profit/Loss Analysis",
            "Month",
            "Amount (₹)",
            profitDataset
        );
        profitChart.getTitle().setFont(new Font("Georgia", Font.BOLD, 20));
        profitChart.getTitle().setPaint(new Color(80, 50, 40));

        panel.add(createChartContainer(profitChart), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createInventoryChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultPieDataset inventoryDataset = createInventoryDataset();

        JFreeChart inventoryChart = ChartFactory.createPieChart(
            "Inventory Distribution by Genre",
            inventoryDataset,
            true, true, false
        );
        inventoryChart.getTitle().setFont(new Font("Georgia", Font.BOLD, 20));
        inventoryChart.getTitle().setPaint(new Color(80, 50, 40));

        panel.add(createChartContainer(inventoryChart), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCustomerAnalyticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultCategoryDataset customerDataset = createCustomerDataset();

        JFreeChart customerChart = ChartFactory.createBarChart(
            "Customer Purchase Frequency",
            "Purchase Count",
            "Number of Customers",
            customerDataset
        );
        customerChart.getTitle().setFont(new Font("Georgia", Font.BOLD, 20));
        customerChart.getTitle().setPaint(new Color(80, 50, 40));

        panel.add(createChartContainer(customerChart), BorderLayout.CENTER);
        return panel;
    }

    private DefaultCategoryDataset createSalesDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        try {
            // Get sales data from database
            List<Bill> bills = billDAO.getAllBills();
            Map<String, Double> monthlySales = new HashMap<>();
            
            // Group sales by month
            for (Bill bill : bills) {
                String month = getMonthFromDateString(bill.getDate());
                double amount = bill.getFinalAmount();
                monthlySales.put(month, monthlySales.getOrDefault(month, 0.0) + amount);
            }
            
            // Add data to dataset
            String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                              "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            
            for (String month : months) {
                double sales = monthlySales.getOrDefault(month, 0.0);
                dataset.addValue(sales, "Sales", month);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to sample data if database fails
            dataset.addValue(15000, "Sales", "Jan");
            dataset.addValue(18000, "Sales", "Feb");
            dataset.addValue(22000, "Sales", "Mar");
            dataset.addValue(19000, "Sales", "Apr");
            dataset.addValue(25000, "Sales", "May");
            dataset.addValue(28000, "Sales", "Jun");
            dataset.addValue(32000, "Sales", "Jul");
            dataset.addValue(30000, "Sales", "Aug");
            dataset.addValue(35000, "Sales", "Sep");
            dataset.addValue(38000, "Sales", "Oct");
            dataset.addValue(42000, "Sales", "Nov");
            dataset.addValue(45000, "Sales", "Dec");
        }
        
        return dataset;
    }

    private DefaultCategoryDataset createProfitLossDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        try {
            // Calculate profit/loss (this is simplified - you need proper cost calculation)
            List<Bill> bills = billDAO.getAllBills();
            Map<String, Double> monthlyProfit = new HashMap<>();
            
            // Simplified profit calculation: 30% of sales as profit
            for (Bill bill : bills) {
                String month = getMonthFromDateString(bill.getDate());
                double profit = bill.getFinalAmount() * 0.3; // 30% profit margin
                monthlyProfit.put(month, monthlyProfit.getOrDefault(month, 0.0) + profit);
            }
            
            String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                              "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            for (String month : months) {
                double profit = monthlyProfit.getOrDefault(month, 0.0);
                dataset.addValue(profit, "Profit", month);
                dataset.addValue(profit * 0.3, "Expenses", month); // Sample expenses
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback sample data
            dataset.addValue(4500, "Profit", "Jan");
            dataset.addValue(5400, "Profit", "Feb");
            dataset.addValue(6600, "Profit", "Mar");
            dataset.addValue(5700, "Profit", "Apr");
            dataset.addValue(7500, "Profit", "May");
            dataset.addValue(8400, "Profit", "Jun");
            dataset.addValue(9600, "Profit", "Jul");
            dataset.addValue(9000, "Profit", "Aug");
            dataset.addValue(10500, "Profit", "Sep");
            dataset.addValue(11400, "Profit", "Oct");
            dataset.addValue(12600, "Profit", "Nov");
            dataset.addValue(13500, "Profit", "Dec");
            
            dataset.addValue(1350, "Expenses", "Jan");
            dataset.addValue(1620, "Expenses", "Feb");
            dataset.addValue(1980, "Expenses", "Mar");
            dataset.addValue(1710, "Expenses", "Apr");
            dataset.addValue(2250, "Expenses", "May");
            dataset.addValue(2520, "Expenses", "Jun");
            dataset.addValue(2880, "Expenses", "Jul");
            dataset.addValue(2700, "Expenses", "Aug");
            dataset.addValue(3150, "Expenses", "Sep");
            dataset.addValue(3420, "Expenses", "Oct");
            dataset.addValue(3780, "Expenses", "Nov");
            dataset.addValue(4050, "Expenses", "Dec");
        }
        
        return dataset;
    }

    private DefaultPieDataset createInventoryDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        try {
            List<Book> books = bookDAO.getAllBooks();
            Map<String, Integer> genreCount = new HashMap<>();
            
            for (Book book : books) {
                String genre = book.getGenre() != null ? book.getGenre() : "Unknown";
                genreCount.put(genre, genreCount.getOrDefault(genre, 0) + 1);
            }
            
            for (Map.Entry<String, Integer> entry : genreCount.entrySet()) {
                dataset.setValue(entry.getKey(), entry.getValue());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback sample data
            dataset.setValue("Fiction", 45);
            dataset.setValue("Non-Fiction", 30);
            dataset.setValue("Science", 25);
            dataset.setValue("Technology", 20);
            dataset.setValue("Literature", 15);
            dataset.setValue("Children", 10);
        }
        
        return dataset;
    }

    private DefaultCategoryDataset createCustomerDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        try {
            // This is a simplified version - you might need a more complex query
            List<Bill> bills = billDAO.getAllBills();
            Map<String, Integer> customerPurchaseCount = new HashMap<>();
            
            for (Bill bill : bills) {
                String customer = bill.getCustomerName();
                if (customer != null && !customer.trim().isEmpty()) {
                    customerPurchaseCount.put(customer, 
                        customerPurchaseCount.getOrDefault(customer, 0) + 1);
                }
            }
            
            // Group by purchase frequency
            int freq1 = 0, freq2 = 0, freq3 = 0, freq4 = 0, freq5 = 0;
            for (int count : customerPurchaseCount.values()) {
                if (count == 1) freq1++;
                else if (count == 2) freq2++;
                else if (count == 3) freq3++;
                else if (count == 4) freq4++;
                else freq5++;
            }
            
            dataset.addValue(freq1, "Customers", "1 Purchase");
            dataset.addValue(freq2, "Customers", "2 Purchases");
            dataset.addValue(freq3, "Customers", "3 Purchases");
            dataset.addValue(freq4, "Customers", "4 Purchases");
            dataset.addValue(freq5, "Customers", "5+ Purchases");
            
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback sample data
            dataset.addValue(25, "Customers", "1 Purchase");
            dataset.addValue(15, "Customers", "2 Purchases");
            dataset.addValue(8, "Customers", "3 Purchases");
            dataset.addValue(5, "Customers", "4 Purchases");
            dataset.addValue(3, "Customers", "5+ Purchases");
        }
        
        return dataset;
    }

    private String getMonthFromDateString(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return "Unknown";
        }
        
        try {
            // Try to parse the date string (adjust the format based on your Bill date format)
            Date date = dateFormat.parse(dateString);
            return monthFormat.format(date);
        } catch (Exception e) {
            // If parsing fails, try to extract month from common date formats
            try {
                // Try different date formats
                if (dateString.contains("-")) {
                    String[] parts = dateString.split("-");
                    if (parts.length >= 2) {
                        int monthNum = Integer.parseInt(parts[1]);
                        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                                          "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                        if (monthNum >= 1 && monthNum <= 12) {
                            return months[monthNum - 1];
                        }
                    }
                }
            } catch (Exception ex) {
                // If all parsing fails, return unknown
            }
            return "Unknown";
        }
    }

    private void loadChartData() {
        try {
            // Force chart refresh by recreating datasets
            tabbedPane.setComponentAt(0, createSalesChartPanel());
            tabbedPane.setComponentAt(1, createProfitLossChartPanel());
            tabbedPane.setComponentAt(2, createInventoryChartPanel());
            tabbedPane.setComponentAt(3, createCustomerAnalyticsPanel());
            
            JOptionPane.showMessageDialog(this, "Charts refreshed with latest data!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error refreshing charts: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}