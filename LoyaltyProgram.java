import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class LoyaltyProgram extends JFrame {
    private HashMap<String, Customer> customers = new HashMap<>();
    private JTextField nameField, purchaseField, redeemField;
    private JButton addBtn, purchaseBtn, redeemBtn, viewBtn;

    public LoyaltyProgram() {
        setTitle("Bookstore Loyalty Points System");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());

        // Add Customer
        add(new JLabel("Customer Name:"));
        nameField = new JTextField(20);
        add(nameField);
        addBtn = new JButton("Add Customer");
        add(addBtn);

        // Add Purchase
        add(new JLabel("Purchase Amount ($):"));
        purchaseField = new JTextField(10);
        add(purchaseField);
        purchaseBtn = new JButton("Add Purchase");
        add(purchaseBtn);

        // Redeem Points
        add(new JLabel("Redeem Points:"));
        redeemField = new JTextField(10);
        add(redeemField);
        redeemBtn = new JButton("Redeem Points");
        add(redeemBtn);

        // View Customer Info
        viewBtn = new JButton("View Customer Info");
        add(viewBtn);

        // Button actions
        addBtn.addActionListener(e -> addCustomer());
        purchaseBtn.addActionListener(e -> addPurchase());
        redeemBtn.addActionListener(e -> redeemPoints());
        viewBtn.addActionListener(e -> viewCustomer());

        setVisible(true);
    }

    private void addCustomer() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a customer name!");
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
            double amount = Double.parseDouble(purchaseField.getText());
            customers.get(name).addPurchase(amount);
            JOptionPane.showMessageDialog(this, "Purchase added. Points updated!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter a valid amount!");
        }
        purchaseField.setText("");
    }

    private void redeemPoints() {
        String name = nameField.getText().trim();
        if (!customers.containsKey(name)) {
            JOptionPane.showMessageDialog(this, "Customer not found!");
            return;
        }
        try {
            int pointsToRedeem = Integer.parseInt(redeemField.getText());
            double discount = customers.get(name).redeemPoints(pointsToRedeem);
            if (discount == 0) {
                JOptionPane.showMessageDialog(this, "Not enough points to redeem!");
            } else {
                JOptionPane.showMessageDialog(this, "Points redeemed! Discount: $" + discount);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter a valid number of points!");
        }
        redeemField.setText("");
    }

    private void viewCustomer() {
        String name = nameField.getText().trim();
        if (!customers.containsKey(name)) {
            JOptionPane.showMessageDialog(this, "Customer not found!");
        } else {
            JOptionPane.showMessageDialog(this, customers.get(name).toString());
        }
    }

    // Inner Customer class
    static class Customer {
        String name;
        double totalSpent;
        int points;

        public Customer(String name) {
            this.name = name;
            this.totalSpent = 0;
            this.points = 0;
        }

        public void addPurchase(double amount) {
            totalSpent += amount;
            points += (int)(amount / 10); // 1 point for every $10 spent
        }

        public double redeemPoints(int pointsToRedeem) {
            if (pointsToRedeem > points) {
                return 0;
            }
            points -= pointsToRedeem;
            return pointsToRedeem; // Assume 1 point = $1 discount
        }

        @Override
        public String toString() {
            return "Name: " + name + ", Points: " + points + ", Total Spent: $" + totalSpent;
        }
    }
}
