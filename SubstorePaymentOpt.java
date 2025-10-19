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
        super((Frame) null, "Substore Payment - BiblioFlow", true);
        this.totalAmount = totalAmount;
        this.onPaymentSuccess = onPaymentSuccess;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        setSize(450, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        // ---------- MAIN PANEL WITH GRADIENT ----------
        JPanel panel = new JPanel() {
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
        panel.setLayout(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ---------- HEADER INFO ----------
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        infoPanel.setOpaque(false);

        JLabel amountLabel = new JLabel("Total: ₹" + String.format("%.2f", totalAmount));
        amountLabel.setFont(new Font("Georgia", Font.BOLD, 20));
        amountLabel.setForeground(new Color(80, 50, 40));

        JLabel nameLabel = new JLabel("Customer: " + (customerName.isEmpty() ? "Not provided" : customerName));
        nameLabel.setFont(new Font("Serif", Font.PLAIN, 16));
        nameLabel.setForeground(new Color(90, 60, 50));

        JLabel phoneLabel = new JLabel("Phone: " + (customerPhone.isEmpty() ? "Not provided" : customerPhone));
        phoneLabel.setFont(new Font("Serif", Font.PLAIN, 16));
        phoneLabel.setForeground(new Color(90, 60, 50));

        infoPanel.add(amountLabel);
        infoPanel.add(nameLabel);
        infoPanel.add(phoneLabel);

        panel.add(infoPanel, BorderLayout.NORTH);

        // ---------- PAYMENT OPTIONS ----------
        JPanel options = new JPanel(new GridLayout(4, 1, 15, 15));
        options.setOpaque(false);

        JButton upi = createStyledButton("Pay via UPI", new Color(150, 90, 60));
        JButton net = createStyledButton("Pay via Net Banking", new Color(120, 70, 50));
        JButton card = createStyledButton("Pay via Card", new Color(160, 100, 70));
        JButton cod = createStyledButton("Cash on Delivery", new Color(200, 120, 80));

        options.add(upi);
        options.add(net);
        options.add(card);
        options.add(cod);

        panel.add(options, BorderLayout.CENTER);

        // ---------- BUTTON ACTIONS ----------
        ActionListener paymentHandler = e -> {
            String method = ((JButton) e.getSource()).getText().replace("Pay via ", "");
            handlePayment(method);
        };

        upi.addActionListener(paymentHandler);
        net.addActionListener(paymentHandler);
        card.addActionListener(paymentHandler);
        cod.addActionListener(paymentHandler);

        add(panel);
    }

    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Georgia", Font.BOLD, 16));
        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(90, 60, 40), 1, true),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor);
            }
        });

        return button;
    }

    private void handlePayment(String method) {
        if ("Cash on Delivery".equals(method)) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Confirm Cash on Delivery order?\nAmount: ₹" + String.format("%.2f", totalAmount) +
                            "\nCustomer: " + customerName + "\nPhone: " + customerPhone,
                    "COD Confirmation", JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) return;
        } else {
            // Simulate payment processing
            JOptionPane.showMessageDialog(this,
                    "Processing " + method + " payment...\nAmount: ₹" + String.format("%.2f", totalAmount),
                    "Payment Processing", JOptionPane.INFORMATION_MESSAGE);
        }

        // Payment success
        JOptionPane.showMessageDialog(this,
                "Payment successful via " + method + "!\nAmount: ₹" + String.format("%.2f", totalAmount),
                "Payment Success", JOptionPane.INFORMATION_MESSAGE);

        if (onPaymentSuccess != null) onPaymentSuccess.run();
        dispose();
    }
}
