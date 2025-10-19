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
        upi.addActionListener(e -> handleUPIPayment());
        net.addActionListener(e -> handlePayment("Net Banking"));
        card.addActionListener(e -> handleCardPayment());
        cod.addActionListener(e -> handlePayment("Cash on Delivery"));

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

    private void handleUPIPayment() {
        JDialog qrDialog = new JDialog(this, "Scan UPI QR Code", true);
        qrDialog.setSize(400, 500);
        qrDialog.setLocationRelativeTo(this);

        JPanel qrPanel = new JPanel(new BorderLayout(10, 10)) {
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
        qrPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Load QR code image
        ImageIcon qrIcon = loadQRImage();
        JLabel qrLabel = new JLabel(qrIcon, SwingConstants.CENTER);
        qrLabel.setBorder(BorderFactory.createLineBorder(new Color(150, 90, 60), 2));
        qrLabel.setPreferredSize(new Dimension(300, 300));

        // Instructions
        JTextArea instructions = new JTextArea(
                "UPI Payment Instructions:\n\n" +
                "1. Open your UPI app (Google Pay, PhonePe, Paytm, etc.)\n" +
                "2. Tap on 'Scan QR Code'\n" +
                "3. Point your camera at this QR code\n" +
                "4. Enter amount: ₹" + String.format("%.2f", totalAmount) + "\n" +
                "5. Confirm payment\n" +
                "6. Click 'Payment Done' below after successful payment"
        );
        instructions.setFont(new Font("Arial", Font.PLAIN, 12));
        instructions.setEditable(false);
        instructions.setBackground(new Color(250, 240, 230));
        instructions.setForeground(new Color(80, 50, 40));
        instructions.setLineWrap(true);
        instructions.setWrapStyleWord(true);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);

        JButton confirmBtn = createStyledButton("Payment Done", new Color(150, 90, 60));
        confirmBtn.setPreferredSize(new Dimension(150, 40));
        confirmBtn.addActionListener(e -> {
            completePayment("UPI");
            qrDialog.dispose();
        });

        JButton cancelBtn = createStyledButton("Cancel", new Color(180, 80, 60));
        cancelBtn.setPreferredSize(new Dimension(150, 40));
        cancelBtn.addActionListener(e -> qrDialog.dispose());

        buttonPanel.add(confirmBtn);
        buttonPanel.add(cancelBtn);

        qrPanel.add(new JScrollPane(instructions), BorderLayout.NORTH);
        qrPanel.add(qrLabel, BorderLayout.CENTER);
        qrPanel.add(buttonPanel, BorderLayout.SOUTH);

        qrDialog.add(qrPanel);
        qrDialog.setVisible(true);
    }

    private ImageIcon loadQRImage() {
        try {
            // Try to load QR code image from file
            ImageIcon originalIcon = new ImageIcon("D:\\java project new\\JAVA Project\\BiblioFlow\\imgs\\qr.jpg");
            if (originalIcon.getIconWidth() > 0) {
                Image scaledImage = originalIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            System.out.println("QR image not found, generating placeholder");
        }

        // Create a placeholder QR code if image not found
        return createPlaceholderQR();
    }

    private ImageIcon createPlaceholderQR() {
        // Create a simple placeholder QR code
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(300, 300, java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // Background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, 300, 300);
        
        // QR pattern (simplified)
        g2d.setColor(Color.BLACK);
        
        // Outer border
        g2d.fillRect(50, 50, 200, 200);
        
        // Inner white space
        g2d.setColor(Color.WHITE);
        g2d.fillRect(60, 60, 180, 180);
        
        // QR pattern blocks
        g2d.setColor(Color.BLACK);
        g2d.fillRect(70, 70, 40, 40);
        g2d.fillRect(190, 70, 40, 40);
        g2d.fillRect(70, 190, 40, 40);
        
        // Text
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("UPI QR CODE", 100, 160);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Amount: ₹" + String.format("%.2f", totalAmount), 110, 180);
        
        g2d.dispose();
        return new ImageIcon(image);
    }

    private void handleCardPayment() {
        JDialog cardDialog = new JDialog(this, "Card Payment", true);
        cardDialog.setSize(400, 350);
        cardDialog.setLocationRelativeTo(this);

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

        // Card form
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setOpaque(false);

        formPanel.add(new JLabel("Card Number:"));
        JTextField cardField = new JTextField();
        formPanel.add(cardField);

        formPanel.add(new JLabel("Expiry Date (MM/YY):"));
        JTextField expiryField = new JTextField();
        formPanel.add(expiryField);

        formPanel.add(new JLabel("CVV:"));
        JPasswordField cvvField = new JPasswordField();
        formPanel.add(cvvField);

        formPanel.add(new JLabel("Card Holder:"));
        JTextField holderField = new JTextField();
        formPanel.add(holderField);

        formPanel.add(new JLabel("Amount:"));
        JLabel amountLabel = new JLabel("₹" + String.format("%.2f", totalAmount));
        amountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(amountLabel);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);

        JButton payBtn = createStyledButton("Process Payment", new Color(150, 90, 60));
        payBtn.setPreferredSize(new Dimension(150, 40));
        payBtn.addActionListener(e -> {
            if (validateCardDetails(cardField.getText(), expiryField.getText(), 
                                  new String(cvvField.getPassword()), holderField.getText())) {
                completePayment("Card");
                cardDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(cardDialog, 
                    "Please fill all card details correctly!", 
                    "Invalid Details", JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton cancelBtn = createStyledButton("Cancel", new Color(180, 80, 60));
        cancelBtn.setPreferredSize(new Dimension(150, 40));
        cancelBtn.addActionListener(e -> cardDialog.dispose());

        buttonPanel.add(payBtn);
        buttonPanel.add(cancelBtn);

        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        cardDialog.add(panel);
        cardDialog.setVisible(true);
    }

    private boolean validateCardDetails(String cardNumber, String expiry, String cvv, String holder) {
        return !cardNumber.trim().isEmpty() && 
               !expiry.trim().isEmpty() && 
               !cvv.trim().isEmpty() && 
               !holder.trim().isEmpty() &&
               cardNumber.replaceAll("\\s", "").length() >= 15 &&
               cvv.length() >= 3;
    }

    private void handlePayment(String method) {
        if ("Cash on Delivery".equals(method)) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Confirm Cash on Delivery order?\n\n" +
                    "Amount: ₹" + String.format("%.2f", totalAmount) +
                    "\nCustomer: " + (customerName.isEmpty() ? "Not provided" : customerName) + 
                    "\nPhone: " + (customerPhone.isEmpty() ? "Not provided" : customerPhone),
                    "COD Confirmation", JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) return;
        } else {
            // Simulate payment processing
            JOptionPane.showMessageDialog(this,
                    "Processing " + method + " payment...\nAmount: ₹" + String.format("%.2f", totalAmount),
                    "Payment Processing", JOptionPane.INFORMATION_MESSAGE);
        }

        completePayment(method);
    }

    private void completePayment(String method) {
        // Payment success
        JOptionPane.showMessageDialog(this,
                "✅ Payment successful via " + method + "!\n\n" +
                "Amount: ₹" + String.format("%.2f", totalAmount) +
                "\nCustomer: " + (customerName.isEmpty() ? "Not provided" : customerName) +
                "\nThank you for your purchase!",
                "Payment Success", JOptionPane.INFORMATION_MESSAGE);

        if (onPaymentSuccess != null) onPaymentSuccess.run();
        dispose();
    }
}