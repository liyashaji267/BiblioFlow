import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class LoyaltyAndDiscountUI extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Customer Loyalty
    private JTable loyaltyTable;
    private DefaultTableModel loyaltyTableModel;
    private CustomerLoyaltyService loyaltyService;
    private BillDAO billDAO;

    // Regular Customer Discount
    private HashMap<String, DiscountCustomer> discountCustomers = new HashMap<>();
    private JTable discountTable;
    private DefaultTableModel discountTableModel;

    public LoyaltyAndDiscountUI() {
        setTitle("BiblioFlow - Loyalty & Discount");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        billDAO = new BillDAO();
        loyaltyService = new CustomerLoyaltyService(billDAO);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add both panels
        mainPanel.add(createLoyaltyPanel(), "Loyalty");
        mainPanel.add(createDiscountPanel(), "Discount");

        add(mainPanel);
    }

    // ------------------- LOYALTY PANEL -------------------
    private JPanel createLoyaltyPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
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
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Customer Loyalty Program", SwingConstants.CENTER);
        header.setFont(new Font("Georgia", Font.BOLD, 32));
        header.setForeground(new Color(33, 150, 243));
        header.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        panel.add(header, BorderLayout.NORTH);

        String[] columns = {"Customer Name", "Total Spent", "Purchases", "Loyalty Points", "Tier"};
        loyaltyTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        loyaltyTable = new JTable(loyaltyTableModel);
        styleTable(loyaltyTable);

        JScrollPane scrollPane = new JScrollPane(loyaltyTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Customers"));
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        controlPanel.setOpaque(false);

        JButton refreshBtn = createStyledButton("Refresh", new Color(76, 175, 80));
        JButton viewDetailsBtn = createStyledButton("View Details", new Color(33, 150, 243));
        JButton checkDiscountBtn = createStyledButton("Check Discount", new Color(255, 193, 7));

        refreshBtn.addActionListener(e -> refreshLoyaltyData());
        viewDetailsBtn.addActionListener(e -> viewCustomerDetails());
        checkDiscountBtn.addActionListener(e -> checkCustomerDiscount());

        controlPanel.add(refreshBtn);
        controlPanel.add(viewDetailsBtn);
        controlPanel.add(checkDiscountBtn);

        panel.add(controlPanel, BorderLayout.SOUTH);

        refreshLoyaltyData();
        return panel;
    }

    private void refreshLoyaltyData() {
        loyaltyTableModel.setRowCount(0);
        Map<String, CustomerLoyaltyService.CustomerLoyalty> customers = loyaltyService.getAllCustomers();
        for (CustomerLoyaltyService.CustomerLoyalty loyalty : customers.values()) {
            loyaltyTableModel.addRow(new Object[]{
                    loyalty.getCustomerName(),
                    String.format("₹%.2f", loyalty.getTotalSpent()),
                    loyalty.getPurchaseCount(),
                    loyalty.getTotalPoints(),
                    loyalty.getTier()
            });
        }
    }

    private void viewCustomerDetails() {
        int row = loyaltyTable.getSelectedRow();
        if (row >= 0) {
            String customerName = (String) loyaltyTableModel.getValueAt(row, 0);
            CustomerLoyaltyService.CustomerLoyalty loyalty = loyaltyService.getCustomerLoyalty(customerName);
            JOptionPane.showMessageDialog(this, loyalty.toString(), "Customer Details", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a customer!");
        }
    }

    private void checkCustomerDiscount() {
        String customerName = JOptionPane.showInputDialog(this, "Enter customer name:");
        if (customerName != null && !customerName.trim().isEmpty()) {
            double sampleAmount = 1000.0;
            double discount = loyaltyService.calculateDiscount(customerName, sampleAmount);
            String tier = loyaltyService.getCustomerTier(customerName);
            JOptionPane.showMessageDialog(this,
                    String.format("Customer: %s\nTier: %s\nDiscount on ₹%.2f: ₹%.2f", customerName, tier, sampleAmount, discount),
                    "Discount Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ------------------- REGULAR CUSTOMER DISCOUNT PANEL -------------------
    private JPanel createDiscountPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
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
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Regular Customer Discount System", SwingConstants.CENTER);
        header.setFont(new Font("Georgia", Font.BOLD, 32));
        header.setForeground(new Color(156, 39, 176));
        header.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        panel.add(header, BorderLayout.NORTH);

        String[] columns = {"Customer Name", "Total Spent", "Regular Customer"};
        discountTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        discountTable = new JTable(discountTableModel);
        styleTable(discountTable);

        JScrollPane scrollPane = new JScrollPane(discountTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Customers"));
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        controlPanel.setOpaque(false);

        JButton addCustomerBtn = createStyledButton("Add Customer", new Color(76, 175, 80));
        JButton addPurchaseBtn = createStyledButton("Add Purchase", new Color(33, 150, 243));
        JButton viewBtn = createStyledButton("View Customer", new Color(255, 193, 7));

        addCustomerBtn.addActionListener(e -> addDiscountCustomer());
        addPurchaseBtn.addActionListener(e -> addPurchaseToDiscountCustomer());
        viewBtn.addActionListener(e -> viewDiscountCustomer());

        controlPanel.add(addCustomerBtn);
        controlPanel.add(addPurchaseBtn);
        controlPanel.add(viewBtn);

        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void addDiscountCustomer() {
        String name = JOptionPane.showInputDialog(this, "Enter customer name:");
        if (name != null && !name.trim().isEmpty()) {
            if (!discountCustomers.containsKey(name)) {
                discountCustomers.put(name, new DiscountCustomer(name));
                JOptionPane.showMessageDialog(this, "Customer added!");
            } else {
                JOptionPane.showMessageDialog(this, "Customer already exists!");
            }
            refreshDiscountTable();
        }
    }

    private void addPurchaseToDiscountCustomer() {
        String name = JOptionPane.showInputDialog(this, "Enter customer name:");
        if (name != null && !name.trim().isEmpty() && discountCustomers.containsKey(name)) {
            String amountStr = JOptionPane.showInputDialog(this, "Enter purchase amount:");
            try {
                double amount = Double.parseDouble(amountStr);
                DiscountCustomer c = discountCustomers.get(name);
                c.applyPurchase(amount);
                JOptionPane.showMessageDialog(this, String.format("Final Amount after discount: ₹%.2f", c.lastPurchaseAmount));
                refreshDiscountTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Customer not found!");
        }
    }

    private void viewDiscountCustomer() {
        String name = JOptionPane.showInputDialog(this, "Enter customer name:");
        if (name != null && !name.trim().isEmpty() && discountCustomers.containsKey(name)) {
            DiscountCustomer c = discountCustomers.get(name);
            JOptionPane.showMessageDialog(this, c.toString(), "Customer Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Customer not found!");
        }
    }

    private void refreshDiscountTable() {
        discountTableModel.setRowCount(0);
        for (DiscountCustomer c : discountCustomers.values()) {
            discountTableModel.addRow(new Object[]{
                    c.name, String.format("₹%.2f", c.totalSpent), c.regular ? "Yes" : "No"
            });
        }
    }

    // ------------------- HELPERS -------------------
    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(33, 150, 243));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 14));
    }

    // ------------------- INNER CLASS FOR REGULAR CUSTOMER -------------------
    static class DiscountCustomer {
        String name;
        double totalSpent = 0;
        boolean regular = false;
        double lastPurchaseAmount = 0;

        private static final double REGULAR_THRESHOLD = 500.0;
        private static final double DISCOUNT_RATE = 0.10;

        public DiscountCustomer(String name) { this.name = name; }

        public double applyPurchase(double amount) {
            lastPurchaseAmount = regular ? amount * (1 - DISCOUNT_RATE) : amount;
            totalSpent += lastPurchaseAmount;
            if (!regular && totalSpent >= REGULAR_THRESHOLD) {
                regular = true;
                JOptionPane.showMessageDialog(null,
                        name + " is now a Regular Customer! 10% discount unlocked 🎉");
            }
            return lastPurchaseAmount;
        }

        public String toString() {
            return "Name: " + name +
                    "\nTotal Spent: ₹" + String.format("%.2f", totalSpent) +
                    "\nRegular Customer: " + (regular ? "Yes" : "No");
        }
    }

    // ------------------- MAIN -------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoyaltyAndDiscountUI().setVisible(true));
    }
}
