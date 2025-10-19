import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.Map;

public class LoyaltyProgramUI extends JFrame {
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private CustomerLoyaltyService loyaltyService;
    private BillDAO billDAO;

    public LoyaltyProgramUI() {
        this.billDAO = new BillDAO();
        this.loyaltyService = new CustomerLoyaltyService(billDAO);

        setTitle("BiblioFlow - Loyalty Program");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        // Main background panel with gradient
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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel headerLabel = new JLabel("Customer Loyalty Program", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Georgia", Font.BOLD, 32));
        headerLabel.setForeground(new Color(33, 150, 243));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Table panel
        String[] columns = {"Customer Name", "Total Spent", "Purchases", "Loyalty Points", "Tier"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        customerTable = new JTable(tableModel);
        customerTable.setFillsViewportHeight(true);
        customerTable.setRowHeight(28);
        customerTable.setFont(new Font("Arial", Font.PLAIN, 14));
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader tableHeader = customerTable.getTableHeader();
        tableHeader.setBackground(new Color(33, 150, 243));
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Customers"));
        scrollPane.setBackground(new Color(250, 240, 230));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Control buttons panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        controlPanel.setOpaque(false);

        JButton refreshBtn = new JButton("Refresh");
        JButton viewDetailsBtn = new JButton("View Details");
        JButton checkDiscountBtn = new JButton("Check Discount");

        styleButton(refreshBtn, new Color(76, 175, 80));
        styleButton(viewDetailsBtn, new Color(33, 150, 243));
        styleButton(checkDiscountBtn, new Color(255, 193, 7));

        refreshBtn.addActionListener(e -> refreshData());
        viewDetailsBtn.addActionListener(e -> viewCustomerDetails());
        checkDiscountBtn.addActionListener(e -> checkCustomerDiscount());

        controlPanel.add(refreshBtn);
        controlPanel.add(viewDetailsBtn);
        controlPanel.add(checkDiscountBtn);

        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        add(mainPanel);

        refreshData();
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void refreshData() {
        tableModel.setRowCount(0);
        Map<String, CustomerLoyaltyService.CustomerLoyalty> customers = loyaltyService.getAllCustomers();
        for (CustomerLoyaltyService.CustomerLoyalty loyalty : customers.values()) {
            tableModel.addRow(new Object[]{
                    loyalty.getCustomerName(),
                    String.format("₹%.2f", loyalty.getTotalSpent()),
                    loyalty.getPurchaseCount(),
                    loyalty.getTotalPoints(),
                    loyalty.getTier()
            });
        }
    }

    private void viewCustomerDetails() {
        int row = customerTable.getSelectedRow();
        if (row >= 0) {
            String customerName = (String) tableModel.getValueAt(row, 0);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoyaltyProgramUI().setVisible(true));
    }
}
