import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class RegularCustomerDiscount extends JFrame {
    private HashMap<String, Customer> customers = new HashMap<>();
    private JTextField nameField, amountField;
    private JButton addCustomerBtn, purchaseBtn, viewBtn;

    public RegularCustomerDiscount() {
        setTitle("Regular Customer Discount System");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());

        // --- Add Customer ---
        add(new JLabel("Customer Name:"));
        nameField = new JTextField(20);
        add(nameField);
        addCustomerBtn = new JButton("Add Customer");
        add(addCustomerBtn);

        // --- Purchase Amount ---
        add(new JLabel("Purchase Amount ($):"));
        amountField = new JTextField(10);
        add(amountField);
        purchaseBtn = new JButton("Add Purchase");
        add(purchaseBtn);

        // --- View Customer Info ---
        viewBtn = new JButton("View Customer Info");
        add(viewBtn);

        // --- Button Actions ---
        addCustomerBtn.addActionListener(e -> addCustomer());
        purchaseBtn.addActionListener(e -> addPurchase());
        viewBtn.addActionListener(e -> viewCustomer());

        setVisible(true);
    }

    private void addCustomer() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter customer name!");
            return;
        }
        if (customers.containsKey(name)) {
            JOptionPane.showMessageDialog(this, "Customer already exists!");
        } else {
            customers.put(name, new Customer(name));
            JOptionPane.showMessageDialog(this, "Customer added successfully!");
        }
        nameField.setText("");
    }

    private void addPurchase() {
        String name = nameField.getText().trim();
        if (!customers.containsKey(name)) {
            JOptionPane.showMessageDialog(this, "Customer not found!");
            return;
        }

        try {
            double amount = Double.parseDouble(amountField.getText());
            Customer c = customers.get(name);
            double finalAmount = c.applyDiscount(amount);

            JOptionPane.showMessageDialog(this,
                String.format("Final Amount after discount: $%.2f", finalAmount));

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter a valid amount!");
        }
        amountField.setText("");
    }

    private void viewCustomer() {
        String name = nameField.getText().trim();
        if (!customers.containsKey(name)) {
            JOptionPane.showMessageDialog(this, "Customer not found!");
        } else {
            JOptionPane.showMessageDialog(this, customers.get(name).toString());
        }
    }

    // --- Inner Class: Customer ---
    static class Customer {
        String name;
        double totalSpent;
        boolean regular;

        // Threshold to become a regular customer
        private static final double REGULAR_THRESHOLD = 500.0;
        private static final double DISCOUNT_RATE = 0.10; // 10% discount

        public Customer(String name) {
            this.name = name;
            this.totalSpent = 0;
            this.regular = false;
        }

        public double applyDiscount(double amount) {
            // If regular, apply discount
            double discountedAmount = regular ? amount - (amount * DISCOUNT_RATE) : amount;
            totalSpent += discountedAmount;

            // Check if they become regular
            if (!regular && totalSpent >= REGULAR_THRESHOLD) {
                regular = true;
                JOptionPane.showMessageDialog(null,
                    name + " is now a Regular Customer! 10% discount unlocked 🎉");
            }

            return discountedAmount;
        }

        @Override
        public String toString() {
            return "Name: " + name +
                   "\nTotal Spent: $" + String.format("%.2f", totalSpent) +
                   "\nRegular Customer: " + (regular ? "Yes" : "No");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RegularCustomerDiscount::new);
    }
}
