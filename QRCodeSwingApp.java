import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class QRCodeSwingApp extends JFrame {
    private static Map<String, String> qrDatabase = new HashMap<>();
    private JTextField qrInputField;
    private JTextArea logArea;
    private JTextField qrDataField;

    public QRCodeSwingApp() {
        setTitle("QR Code App");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel for inputs
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("QR Code Manager"));

        // Input fields
        panel.add(new JLabel("QR Code:"));
        qrInputField = new JTextField();
        panel.add(qrInputField);

        panel.add(new JLabel("QR Data:"));
        qrDataField = new JTextField();
        panel.add(qrDataField);

        // Buttons
        JButton addButton = new JButton("Add QR");
        JButton scanButton = new JButton("Scan QR");
        panel.add(addButton);
        panel.add(scanButton);

        // Log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);

        // Layout
        setLayout(new BorderLayout(10, 10));
        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Button actions
        addButton.addActionListener(e -> addQR());
        scanButton.addActionListener(e -> scanQR());

        // Preload some QR codes
        qrDatabase.put("QR123", "Product: Book, Price: 200");
        qrDatabase.put("QR456", "Product: Laptop, Price: 50000");
        log("Preloaded QRs: QR123, QR456");
    }

    private void addQR() {
        String code = qrInputField.getText().trim();
        String data = qrDataField.getText().trim();
        if (!code.isEmpty() && !data.isEmpty()) {
            qrDatabase.put(code, data);
            log("✅ QR Added: " + code + " -> " + data);
            qrInputField.setText("");
            qrDataField.setText("");
        } else {
            log("⚠️ Please enter both QR code and data!");
        }
    }

    private void scanQR() {
        String code = qrInputField.getText().trim();
        if (qrDatabase.containsKey(code)) {
            log("✅ Valid QR found! Data: " + qrDatabase.get(code));
        } else {
            log("❌ Fake QR! No match found for: " + code);
        }
        qrInputField.setText("");
    }

    private void log(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QRCodeSwingApp().setVisible(true));
    }
}
