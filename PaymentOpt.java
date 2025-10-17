// PaymentOpt.java
import javax.swing.*;
import java.awt.*;

public class PaymentOpt extends JFrame {
    private double totalAmount;
    private Runnable onPaymentSuccess;
    private String customerName;
    private String customerPhone;

    // Updated constructor to match your usage
    public PaymentOpt(double totalAmount, Runnable onPaymentSuccess) {
        this(totalAmount, onPaymentSuccess, "", "");
    }

    public PaymentOpt(double totalAmount, Runnable onPaymentSuccess, String customerName, String customerPhone) {
        this.totalAmount = totalAmount;
        this.onPaymentSuccess = onPaymentSuccess;
        this.customerName = customerName;
        this.customerPhone = customerPhone;

        setTitle("Payment Options - BiblioFlow");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 248, 255));

        // Header
        JLabel amountLabel = new JLabel("Total Amount: ₹" + String.format("%.2f", totalAmount), SwingConstants.CENTER);
        amountLabel.setFont(new Font("Arial", Font.BOLD, 20));
        amountLabel.setForeground(new Color(70, 130, 180));
        panel.add(amountLabel, BorderLayout.NORTH);

        // Payment Options
        JPanel options = new JPanel(new GridLayout(4, 1, 15, 15));
        options.setBackground(new Color(240, 248, 255));

        JButton upiBtn = createStyledButton("Pay via UPI", new Color(76, 175, 80));
        JButton netBtn = createStyledButton("Pay via Net Banking", new Color(33, 150, 243));
        JButton cashBtn = createStyledButton("Pay via Cash", new Color(255, 152, 0));
        JButton cardBtn = createStyledButton("Pay via Card", new Color(156, 39, 176));

        options.add(upiBtn);
        options.add(netBtn);
        options.add(cashBtn);
        options.add(cardBtn);
        
        JScrollPane optionsScroll = new JScrollPane(options);
        panel.add(optionsScroll, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBackground(new Color(240, 248, 255));

        JButton printBtn = createStyledButton("Print Bill", new Color(46, 125, 50));
        printBtn.setEnabled(false);

        JButton closeBtn = createStyledButton("Close", new Color(244, 67, 54));

        bottomPanel.add(printBtn);
        bottomPanel.add(closeBtn);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // Payment handlers
        upiBtn.addActionListener(e -> handleUPIPayment(printBtn));
        netBtn.addActionListener(e -> handleNetBankingPayment(printBtn));
        cashBtn.addActionListener(e -> handleCashPayment(printBtn));
        cardBtn.addActionListener(e -> handleCardPayment(printBtn));

        // Print bill
        printBtn.addActionListener(e -> {
            printBill();
            this.dispose();
        });

        closeBtn.addActionListener(e -> this.dispose());

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

    private void handleUPIPayment(JButton printBtn) {
        JDialog qrDialog = new JDialog(this, "Scan QR Code", true);
        qrDialog.setSize(400, 500);
        qrDialog.setLocationRelativeTo(this);

        JPanel qrPanel = new JPanel(new BorderLayout());
        qrPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Load image as an Icon
        ImageIcon qrIcon = new ImageIcon("D:\\java project new\\JAVA Project\\BiblioFlow\\imgs\\qr.jpg");

        // Optional: scale the image to fit the label
        Image img = qrIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        qrIcon = new ImageIcon(img);

        // Create JLabel with the image
        JLabel qrLabel = new JLabel(qrIcon, SwingConstants.CENTER);
        qrLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        qrLabel.setPreferredSize(new Dimension(300, 300));

        JLabel instruction = new JLabel("Scan the QR code with your UPI app", SwingConstants.CENTER);
        instruction.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton confirmBtn = new JButton("Payment Done");
        confirmBtn.setBackground(new Color(76, 175, 80));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.addActionListener(e -> {
            completePayment("UPI", printBtn);
            qrDialog.dispose();
        });

        qrPanel.add(qrLabel, BorderLayout.CENTER);
        qrPanel.add(instruction, BorderLayout.NORTH);
        qrPanel.add(confirmBtn, BorderLayout.SOUTH);

        qrDialog.add(qrPanel);
        qrDialog.setVisible(true);
    }

    private void handleNetBankingPayment(JButton printBtn) {
        JDialog netBankingDialog = new JDialog(this, "Net Banking", true);
        netBankingDialog.setSize(400, 300);
        netBankingDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea procedure = new JTextArea(
            "Net Banking Procedure:\n\n" +
            "1. Select your bank\n" +
            "2. Enter your credentials\n" +
            "3. Authorize payment\n" +
            "4. Wait for confirmation\n\n" +
            "Click 'Payment Done' after completing the process."
        );
        procedure.setEditable(false);
        procedure.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton confirmBtn = new JButton("Payment Done");
        confirmBtn.addActionListener(e -> {
            completePayment("Net Banking", printBtn);
            netBankingDialog.dispose();
        });

        panel.add(new JScrollPane(procedure), BorderLayout.CENTER);
        panel.add(confirmBtn, BorderLayout.SOUTH);

        netBankingDialog.add(panel);
        netBankingDialog.setVisible(true);
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

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
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

        JButton confirmBtn = new JButton("Process Payment");
        confirmBtn.addActionListener(e -> {
            completePayment("Card", printBtn);
            cardDialog.dispose();
        });

        cardDialog.add(panel, BorderLayout.CENTER);
        cardDialog.add(confirmBtn, BorderLayout.SOUTH);
        cardDialog.setVisible(true);
    }

    private void completePayment(String method, JButton printBtn) {
        JOptionPane.showMessageDialog(this,
            method + " Payment Successful!\nAmount Paid: ₹" + String.format("%.2f", totalAmount),
            "Payment Success", JOptionPane.INFORMATION_MESSAGE);

        printBtn.setEnabled(true);

        if (onPaymentSuccess != null) {
            onPaymentSuccess.run();
        }
    }

    private void printBill() {
        BillGenerator billGenerator = new BillGenerator(totalAmount, customerName, customerPhone);
        billGenerator.generateBill();
        
        JOptionPane.showMessageDialog(this, "🖨 Bill printed successfully!");
    }
}