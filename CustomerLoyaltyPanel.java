import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.Map;

public class CustomerLoyaltyPanel extends JPanel {
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private CustomerLoyaltyService loyaltyService;
    private BillDAO billDAO;

    public CustomerLoyaltyPanel() {
        this.billDAO = new BillDAO();
        this.loyaltyService = new CustomerLoyaltyService(billDAO);
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        // Header
        JLabel headerLabel = new JLabel("Customer Loyalty Program", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 26));
        headerLabel.setForeground(new Color(33, 150, 243));
        add(headerLabel, BorderLayout.NORTH);

        // Customer table
        String[] columns = {"Customer Name", "Total Spent", "Purchases", "Loyalty Points", "Tier"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only table
            }
        };
        customerTable = new JTable(tableModel);
        customerTable.setFillsViewportHeight(true);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerTable.setRowHeight(28);
        customerTable.setFont(new Font("Arial", Font.PLAIN, 14));
        customerTable.setGridColor(Color.LIGHT_GRAY);
        customerTable.setSelectionBackground(new Color(33, 150, 243));
        customerTable.setSelectionForeground(Color.WHITE);
        customerTable.setBackground(Color.WHITE);
        customerTable.setForeground(Color.BLACK);

        // Table header
        JTableHeader header = customerTable.getTableHeader();
        header.setBackground(new Color(33, 150, 243));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Customers"));
        add(scrollPane, BorderLayout.CENTER);

        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        controlPanel.setBackground(Color.WHITE);

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

        add(controlPanel, BorderLayout.SOUTH);

        // Load initial data
        refreshData();
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }

    private void refreshData() {
        tableModel.setRowCount(0);
        loyaltyService = new CustomerLoyaltyService(billDAO);

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
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow >= 0) {
            String customerName = (String) tableModel.getValueAt(selectedRow, 0);
            CustomerLoyaltyService.CustomerLoyalty loyalty = loyaltyService.getCustomerLoyalty(customerName);

            if (loyalty != null) {
                java.util.List<Bill> customerBills = billDAO.getCustomerBills(customerName);

                StringBuilder details = new StringBuilder();
                details.append(loyalty.toString()).append("\n\n");
                details.append("Recent Purchases:\n");

                int count = 0;
                for (Bill bill : customerBills) {
                    if (count >= 10) break;
                    details.append(String.format("• %s - ₹%.2f (%s)\n",
                            bill.getBillNumber(), bill.getFinalAmount(), bill.getDate()));
                    count++;
                }

                JTextArea textArea = new JTextArea(details.toString());
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(450, 350));

                JOptionPane.showMessageDialog(this, scrollPane,
                        "Customer Details: " + customerName, JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a customer first!");
        }
    }

    private void checkCustomerDiscount() {
        String customerName = JOptionPane.showInputDialog(this, "Enter customer name:");
        if (customerName != null && !customerName.trim().isEmpty()) {
            double sampleAmount = 1000.0;
            double discount = loyaltyService.calculateDiscount(customerName, sampleAmount);
            String tier = loyaltyService.getCustomerTier(customerName);

            CustomerLoyaltyService.CustomerLoyalty loyalty = loyaltyService.getCustomerLoyalty(customerName);

            if (discount > 0) {
                JOptionPane.showMessageDialog(this,
                        String.format("Customer: %s\nTier: %s\nDiscount on ₹%.2f purchase: ₹%.2f (%.1f%%)",
                                customerName, tier, sampleAmount, discount, (discount / sampleAmount) * 100),
                        "Discount Calculation", JOptionPane.INFORMATION_MESSAGE);
            } else {
                double remainingForRegular = 1000 - (loyalty != null ? loyalty.getTotalSpent() : 0);
                JOptionPane.showMessageDialog(this,
                        String.format("Customer: %s\nTier: %s\nNo discount available yet.\nSpend ₹%.2f more to become Regular customer.",
                                customerName, tier, Math.max(0, remainingForRegular)),
                        "Discount Calculation", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
