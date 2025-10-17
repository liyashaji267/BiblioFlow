import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SubstorePaymentOpt extends JDialog {
    private double totalAmount;
    private Runnable onPaymentSuccess;
    private String customerName;
    private String customerPhone;

    public SubstorePaymentOpt(double totalAmount, Runnable onPaymentSuccess, 
                             String customerName, String customerPhone) {
        super((Frame)null, "Substore Payment", true);
        this.totalAmount = totalAmount;
        this.onPaymentSuccess = onPaymentSuccess;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        setSize(450, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 248, 255));

        // Header with customer info
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        infoPanel.setBackground(new Color(240, 248, 255));
        
        JLabel amountLabel = new JLabel("Total: ₹" + String.format("%.2f", totalAmount));
        amountLabel.setFont(new Font("Arial", Font.BOLD, 18));
        amountLabel.setForeground(new Color(70, 130, 180));
        
        JLabel nameLabel = new JLabel("Customer: " + (customerName.isEmpty() ? "Not provided" : customerName));
        JLabel phoneLabel = new JLabel("Phone: " + (customerPhone.isEmpty() ? "Not provided" : customerPhone));
        
        infoPanel.add(amountLabel);
        infoPanel.add(nameLabel);
        infoPanel.add(phoneLabel);
        panel.add(infoPanel, BorderLayout.NORTH);

        // Payment options
        JPanel options = new JPanel(new GridLayout(4, 1, 15, 15));
        options.setBackground(new Color(240, 248, 255));

        JButton upi = createStyledButton("Pay via UPI", new Color(76, 175, 80));
        JButton net = createStyledButton("Pay via Net Banking", new Color(33, 150, 243));
        JButton card = createStyledButton("Pay via Card", new Color(156, 39, 176));
        JButton cod = createStyledButton("Cash on Delivery", new Color(255, 152, 0));

        options.add(upi);
        options.add(net);
        options.add(card);
        options.add(cod);
        panel.add(options, BorderLayout.CENTER);

        // Action listener for all buttons
        ActionListener paymentHandler = e -> {
            String method = ((JButton)e.getSource()).getText().replace("Pay via ", "");
            handlePayment(method);
        };

        upi.addActionListener(paymentHandler);
        net.addActionListener(paymentHandler);
        card.addActionListener(paymentHandler);
        cod.addActionListener(paymentHandler);

        add(panel);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    private void handlePayment(String method) {
        if ("Cash on Delivery".equals(method)) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Confirm Cash on Delivery order?\nAmount: ₹" + String.format("%.2f", totalAmount) +
                "\nCustomer: " + customerName + "\nPhone: " + customerPhone,
                "COD Confirmation", JOptionPane.YES_NO_OPTION);
            
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        } else {
            // Simulate payment processing
            JOptionPane.showMessageDialog(this, 
                "Processing " + method + " payment...\nAmount: ₹" + String.format("%.2f", totalAmount),
                "Payment Processing", JOptionPane.INFORMATION_MESSAGE);
        }

        // Payment successful
        JOptionPane.showMessageDialog(this, 
            "Payment successful via " + method + "!\nAmount: ₹" + String.format("%.2f", totalAmount),
            "Payment Success", JOptionPane.INFORMATION_MESSAGE);

        if (onPaymentSuccess != null) {
            onPaymentSuccess.run();
        }
        dispose();
    }
}