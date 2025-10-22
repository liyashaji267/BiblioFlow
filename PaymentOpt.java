import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;

public class PaymentOpt extends JFrame {
    private double totalAmount;
    private Runnable onPaymentSuccess;
    private String customerName;
    private String customerPhone;

    public PaymentOpt(double totalAmount, Runnable onPaymentSuccess) {
        this(totalAmount, onPaymentSuccess, "", "");
    }

    public PaymentOpt(double totalAmount, Runnable onPaymentSuccess, String customerName, String customerPhone) {
        this.totalAmount = totalAmount;
        this.onPaymentSuccess = onPaymentSuccess;
        this.customerName = customerName;
        this.customerPhone = customerPhone;

        setTitle("Payment Options - BiblioFlow");
        setSize(500, 700); // Increased height for PayPal button
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new BorderLayout(15, 15)) {
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

        // Header
        JLabel amountLabel = new JLabel("Total Amount: ₹" + String.format("%.2f", totalAmount), SwingConstants.CENTER);
        amountLabel.setFont(new Font("Georgia", Font.BOLD, 22));
        amountLabel.setForeground(new Color(80, 50, 40));

        if (customerName != null && !customerName.trim().isEmpty()) {
            applyLoyaltyDiscount(customerName, amountLabel);
        }

        panel.add(amountLabel, BorderLayout.NORTH);

        // Payment options
        JPanel options = new JPanel(new GridLayout(5, 1, 15, 15)); // Changed to 5 rows for PayPal
        options.setOpaque(false);

        JButton upiBtn = createStyledButton("Pay via UPI", new Color(150, 90, 60));
        JButton netBtn = createStyledButton("Pay via Net Banking", new Color(120, 70, 50));
        JButton cashBtn = createStyledButton("Pay via Cash", new Color(160, 110, 80));
        JButton cardBtn = createStyledButton("Pay via Card", new Color(110, 70, 50));
        JButton paypalBtn = createStyledButton("Pay with PayPal", new Color(0, 48, 135)); // PayPal blue

        options.add(upiBtn);
        options.add(netBtn);
        options.add(cashBtn);
        options.add(cardBtn);
        options.add(paypalBtn);

        panel.add(options, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        bottomPanel.setOpaque(false);

        JButton printBtn = createStyledButton("Print Bill", new Color(120, 70, 50));
        printBtn.setEnabled(false);
        JButton closeBtn = createStyledButton("Close", new Color(180, 80, 60));

        bottomPanel.add(printBtn);
        bottomPanel.add(closeBtn);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // Action listeners
        upiBtn.addActionListener(e -> handleUPIPayment(printBtn));
        netBtn.addActionListener(e -> handleNetBankingPayment(printBtn));
        cashBtn.addActionListener(e -> handleCashPayment(printBtn));
        cardBtn.addActionListener(e -> handleCardPayment(printBtn));
        paypalBtn.addActionListener(e -> handlePayPalPayment(printBtn));
        printBtn.addActionListener(e -> {
            printBill();
            this.dispose();
        });
        closeBtn.addActionListener(e -> this.dispose());

        add(panel);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Georgia", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 50));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

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

    // -------------------------
    // PAYMENT HANDLERS
    // -------------------------

    private void handleUPIPayment(JButton printBtn) {
        JDialog qrDialog = new JDialog(this, "Scan QR Code", true);
        qrDialog.setSize(400, 500);
        qrDialog.setLocationRelativeTo(this);

        JPanel qrPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(250, 240, 230));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        qrPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        ImageIcon qrIcon = new ImageIcon("D:\\java project new\\JAVA Project\\BiblioFlow\\imgs\\qr.jpg");
        Image img = qrIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        qrIcon = new ImageIcon(img);

        JLabel qrLabel = new JLabel(qrIcon, SwingConstants.CENTER);
        qrLabel.setBorder(BorderFactory.createLineBorder(new Color(150, 90, 60), 2));
        qrLabel.setPreferredSize(new Dimension(300, 300));

        JLabel instruction = new JLabel("Scan the QR code with your UPI app", SwingConstants.CENTER);
        instruction.setFont(new Font("Georgia", Font.PLAIN, 14));
        instruction.setForeground(new Color(80, 50, 40));

        JButton confirmBtn = createStyledButton("Payment Done", new Color(150, 90, 60));
        confirmBtn.addActionListener(e -> {
            completePayment("UPI", printBtn);
            qrDialog.dispose();
        });

        qrPanel.add(instruction, BorderLayout.NORTH);
        qrPanel.add(qrLabel, BorderLayout.CENTER);
        qrPanel.add(confirmBtn, BorderLayout.SOUTH);

        qrDialog.add(qrPanel);
        qrDialog.setVisible(true);
    }

    private void handleNetBankingPayment(JButton printBtn) {
        JDialog netDialog = new JDialog(this, "Net Banking", true);
        netDialog.setSize(400, 300);
        netDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(250, 240, 230));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea procedure = new JTextArea(
                "Net Banking Procedure:\n\n" +
                        "1. Select your bank\n" +
                        "2. Enter your credentials\n" +
                        "3. Authorize payment\n" +
                        "4. Wait for confirmation\n\n" +
                        "Click 'Payment Done' after completing the process."
        );
        procedure.setFont(new Font("Georgia", Font.PLAIN, 14));
        procedure.setEditable(false);
        procedure.setBackground(new Color(250, 240, 230));
        procedure.setForeground(new Color(80, 50, 40));

        JButton confirmBtn = createStyledButton("Payment Done", new Color(120, 70, 50));
        confirmBtn.addActionListener(e -> {
            completePayment("Net Banking", printBtn);
            netDialog.dispose();
        });

        panel.add(new JScrollPane(procedure), BorderLayout.CENTER);
        panel.add(confirmBtn, BorderLayout.SOUTH);
        netDialog.add(panel);
        netDialog.setVisible(true);
    }

    private void handleCashPayment(JButton printBtn) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Confirm cash payment of ₹" + String.format("%.2f", totalAmount) + "?",
                "Cash Payment", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            completePayment("Cash", printBtn);
        }
    }

    private void handleCardPayment(JButton printBtn) {
        JDialog cardDialog = new JDialog(this, "Card Payment", true);
        cardDialog.setSize(400, 300);
        cardDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(250, 240, 230));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Card Number:"));
        JTextField cardField = new JTextField();
        panel.add(cardField);

        panel.add(new JLabel("Expiry Date:"));
        JTextField expiryField = new JTextField();
        panel.add(expiryField);

        panel.add(new JLabel("CVV:"));
        JPasswordField cvvField = new JPasswordField();
        panel.add(cvvField);

        panel.add(new JLabel("Card Holder:"));
        JTextField holderField = new JTextField();
        panel.add(holderField);

        JButton confirmBtn = createStyledButton("Process Payment", new Color(150, 90, 60));
        confirmBtn.addActionListener(e -> {
            completePayment("Card", printBtn);
            cardDialog.dispose();
        });

        cardDialog.add(panel, BorderLayout.CENTER);
        cardDialog.add(confirmBtn, BorderLayout.SOUTH);
        cardDialog.setVisible(true);
    }

    private void handlePayPalPayment(JButton printBtn) {
        if (!PayPalClient.isConfigured()) {
            JOptionPane.showMessageDialog(this,
                    "PayPal is not configured properly.\nPlease check your .env file with CLIENT_ID and CLIENT_SECRET.",
                    "PayPal Configuration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Start local server for callbacks
        try {
            LocalServer.startServer();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to start local server for PayPal callbacks.",
                    "Server Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Show PayPal processing dialog
        JDialog paypalDialog = new JDialog(this, "PayPal Payment", true);
        paypalDialog.setSize(450, 400);
        paypalDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10)) {
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

        // PayPal logo
        JLabel paypalLogo = new JLabel("PayPal", SwingConstants.CENTER);
        paypalLogo.setFont(new Font("Arial", Font.BOLD, 28));
        paypalLogo.setForeground(new Color(0, 48, 135));

        // Instructions
        JTextArea instructions = new JTextArea(
                "PayPal Payment Process:\n\n" +
                "1. Click 'Open PayPal' to proceed to PayPal\n" +
                "2. Log in to your PayPal account\n" +
                "3. Review and confirm your payment\n" +
                "4. You will be redirected back to this application\n" +
                "5. Click 'Payment Complete' after successful payment\n\n" +
                "Amount: $" + String.format("%.2f", totalAmount / 75.0) + " (₹" + String.format("%.2f", totalAmount) + ")"
        );
        instructions.setFont(new Font("Arial", Font.PLAIN, 14));
        instructions.setEditable(false);
        instructions.setBackground(new Color(250, 240, 230));
        instructions.setForeground(new Color(80, 50, 40));
        instructions.setLineWrap(true);
        instructions.setWrapStyleWord(true);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);

        JButton openPayPalBtn = createStyledButton("Open PayPal", new Color(0, 48, 135));
        openPayPalBtn.setPreferredSize(new Dimension(150, 40));
        
        JButton confirmBtn = createStyledButton("Payment Complete", new Color(0, 48, 135));
        confirmBtn.setPreferredSize(new Dimension(150, 40));
        confirmBtn.setEnabled(false);
        
        JButton cancelBtn = createStyledButton("Cancel", new Color(180, 80, 60));
        cancelBtn.setPreferredSize(new Dimension(150, 40));

        openPayPalBtn.addActionListener(e -> {
            try {
                // Open browser for PayPal payment
                String returnUrl = "http://localhost:8000/paypal-return";
                String cancelUrl = "http://localhost:8000/paypal-cancel";
                
                // In a real implementation, you would create a PayPal order here
                // For demo purposes, we'll just open a browser
                java.awt.Desktop.getDesktop().browse(
                    new java.net.URI("https://www.sandbox.paypal.com/checkoutnow?token=demo_token")
                );
                
                openPayPalBtn.setEnabled(false);
                confirmBtn.setEnabled(true);
                JOptionPane.showMessageDialog(paypalDialog,
                    "Browser opened for PayPal payment.\nPlease complete the payment in your browser and return here to click 'Payment Complete'.",
                    "PayPal Payment",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(paypalDialog,
                    "Failed to open browser: " + ex.getMessage(),
                    "Browser Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        confirmBtn.addActionListener(e -> {
            completePayment("PayPal", printBtn);
            paypalDialog.dispose();
            LocalServer.stopServer();
        });

        cancelBtn.addActionListener(e -> {
            paypalDialog.dispose();
            LocalServer.stopServer();
        });

        buttonPanel.add(openPayPalBtn);
        buttonPanel.add(confirmBtn);
        buttonPanel.add(cancelBtn);

        panel.add(paypalLogo, BorderLayout.NORTH);
        panel.add(new JScrollPane(instructions), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        paypalDialog.add(panel);
        paypalDialog.setVisible(true);
    }

    // -------------------------
    // LOGIC METHODS (unchanged)
    // -------------------------
    private void completePayment(String method, JButton printBtn) {
        if (customerName != null && !customerName.trim().isEmpty()) {
            CustomerLoyaltyService loyaltyService = new CustomerLoyaltyService(new BillDAO());
            int pointsEarned = loyaltyService.calculateLoyaltyPoints(customerName, totalAmount);
            loyaltyService.updateCustomerAfterPurchase(customerName, totalAmount);

            CustomerLoyaltyService.CustomerLoyalty loyalty = loyaltyService.getCustomerLoyalty(customerName);
            String message;
            if (loyalty != null) {
                message = String.format("%s Payment Successful!\nAmount Paid: ₹%.2f\nLoyalty Points Earned: %d\nTotal Points: %d\nCustomer Tier: %s",
                        method, totalAmount, pointsEarned, loyalty.getTotalPoints(), loyalty.getTier());
            } else {
                message = String.format("%s Payment Successful!\nAmount Paid: ₹%.2f", method, totalAmount);
            }
            JOptionPane.showMessageDialog(this, message, "Payment Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, method + " Payment Successful!\nAmount Paid: ₹" + String.format("%.2f", totalAmount),
                    "Payment Success", JOptionPane.INFORMATION_MESSAGE);
        }
        printBtn.setEnabled(true);
        if (onPaymentSuccess != null) onPaymentSuccess.run();
    }

    private void printBill() {
        BillGenerator billGenerator = new BillGenerator(totalAmount, customerName, customerPhone);
        billGenerator.generateBill();
        JOptionPane.showMessageDialog(this, "🖨 Bill printed successfully!");
    }

    private void applyLoyaltyDiscount(String customerName, JLabel amountLabel) {
        if (customerName == null || customerName.trim().isEmpty()) return;
        CustomerLoyaltyService loyaltyService = new CustomerLoyaltyService(new BillDAO());
        double discount = loyaltyService.calculateDiscount(customerName, totalAmount);
        if (discount > 0) {
            double discountedAmount = totalAmount - discount;
            String customerTier = loyaltyService.getCustomerTier(customerName);
            JOptionPane.showMessageDialog(this, String.format("🎉 Loyalty Discount Applied!\nCustomer Tier: %s\nOriginal Amount: ₹%.2f\nDiscount: ₹%.2f\nFinal Amount: ₹%.2f",
                    customerTier, totalAmount, discount, discountedAmount), "Loyalty Discount", JOptionPane.INFORMATION_MESSAGE);
            totalAmount = discountedAmount;
            amountLabel.setText("Total Amount: ₹" + String.format("%.2f", totalAmount));
        }
    }
}