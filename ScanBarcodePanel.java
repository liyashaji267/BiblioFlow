import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScanBarcodePanel extends JPanel {

    private Webcam webcam;
    private WebcamPanel webcamPanel;
    private ExecutorService executor;
    private boolean scanning = false;

    private JTable billingTable;
    private JTextArea logArea;
    private JLabel totalLabel;
    private double cartTotal = 0.0;

    private JButton startButton;
    private JButton stopButton;

    private String lastScannedCode = null;
    private long lastScanTime = 0;


    public ScanBarcodePanel(JTable billingTable, JTextArea logArea, JLabel totalLabel) {
        this.billingTable = billingTable;
        this.logArea = logArea;
        this.totalLabel = totalLabel;

        setLayout(new BorderLayout(5, 5));

        // Setup webcam panel
        setupWebcam();

        // Button panel
        startButton = new JButton("Start Scanning");
        stopButton = new JButton("Stop Scanning");
        stopButton.setEnabled(true);

        // Start Button
        startButton.setBackground(new Color(46, 125, 50)); // Darker green
        startButton.setForeground(Color.WHITE);
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        startButton.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        startButton.setFocusPainted(false);

        // Stop Button
        stopButton.setBackground(new Color(198, 40, 40)); // Darker red
        stopButton.setForeground(Color.WHITE);
        stopButton.setFont(new Font("Arial", Font.BOLD, 14));
        stopButton.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        stopButton.setFocusPainted(true);

        startButton.addActionListener(e -> startScanning());
        stopButton.addActionListener(e -> stopScanning());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupWebcam() {
        webcam = Webcam.getDefault();
        if (webcam != null) {
            webcam.setViewSize(WebcamResolution.VGA.getSize());
            webcamPanel = new WebcamPanel(webcam, false);           // Auto start set to false
            webcamPanel.setFPSDisplayed(true);
            webcamPanel.setDisplayDebugInfo(true);
            webcamPanel.setImageSizeDisplayed(true);
            webcamPanel.setMirrored(false);                          // Set mirrored to false for correct orientation

            add(webcamPanel, BorderLayout.CENTER);
            revalidate();
            repaint();

        } else {
            JOptionPane.showMessageDialog(this, "No webcam detected!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void startScanning() {
        if (webcam == null || scanning) return;

        webcam.open();
        scanning = true;
        startButton.setEnabled(true);
        webcamPanel.start();             //  Start the webcam panel explicitly
        stopButton.setEnabled(true);
        log("Scanning started...");

        executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            while (scanning && !Thread.currentThread().isInterrupted()) {
                try {
                    BufferedImage image = webcam.getImage();
                    if (image != null) {
                        String barcode = decodeBarcode(image);
                        if (barcode != null && !barcode.isEmpty()) {
                            handleScannedBarcode(barcode);
                        }
                    }
                    Thread.sleep(30); // Adjust delay as needed
                } catch (Exception e) {
                    log("Scan error: " + e.getMessage());
                }
            }
        });
    }

    public void stopScanning() {
        scanning = false;
        if (executor != null) executor.shutdown();
        if (webcam != null) webcam.close();
        startButton.setEnabled(true);
        stopButton.setEnabled(true);
        log("Scanning stopped.");
    }
    

    private void handleScannedBarcode(String barcode) {
        long now = System.currentTimeMillis();

        // Prevent duplicates within 2 seconds
        if (barcode.equals(lastScannedCode) && (now - lastScanTime < 2000)) {
            return;
        }

        lastScannedCode = barcode;
        lastScanTime = now;

        SwingUtilities.invokeLater(() -> addBookToBilling(barcode));
    }



    private String decodeBarcode(BufferedImage image) {
        try {
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Map<DecodeHintType, Object> hints = new HashMap<>();
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            Result result = new MultiFormatReader().decode(bitmap, hints);
            return result.getText();
        } catch (NotFoundException e) {
            return null;
        } catch (Exception e) {
            log("Decode error: " + e.getMessage());
            return null;
        }
    }

    private void addBookToBilling(String isbn) {
        try (Connection conn = BarcodeDB.getConnection()) {
            String sql = "SELECT title, price FROM books_details WHERE isbn=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, isbn);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String title = rs.getString("title");
                double price = rs.getDouble("price");

                DefaultTableModel model = (DefaultTableModel) billingTable.getModel();

                boolean found = false;
                for (int i = 0; i < model.getRowCount(); i++) {
                    if (isbn.equals(model.getValueAt(i, 0))) {
                        int qty = (int) model.getValueAt(i, 3) + 1;
                        model.setValueAt(qty, i, 3);
                        model.setValueAt(price * qty, i, 4);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    Object[] row = {isbn, title, price, 1, price};
                    model.addRow(row);
                }

                cartTotal = 0.0;
                for (int i = 0; i < model.getRowCount(); i++) {
                    cartTotal += (double) model.getValueAt(i, 4);
                }
                totalLabel.setText("Total: ₹" + cartTotal);

                log("Added: " + title);
            } else {
                log("Book not found: " + isbn);
            }

        } catch (Exception e) {
            log("DB error: " + e.getMessage());
        }
    }

    private void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}
