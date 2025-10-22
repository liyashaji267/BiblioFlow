import javax.swing.*;
import java.awt.*;

public class PaymentSimulator {

    public static void simulatePayment(Window parent, double amount, String method, Runnable onSuccess) {
        try {
            String fakeId = "SIM-" + System.currentTimeMillis();
            String methodSpecificMessage = getMethodSpecificMessage(method, amount);
            
            int confirm = JOptionPane.showConfirmDialog(parent,
                    methodSpecificMessage + "\nAmount: ₹" + String.format("%.2f", amount),
                    method + " Payment", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Show processing message with method-specific text
                JOptionPane.showMessageDialog(parent,
                        getProcessingMessage(method),
                        "Processing " + method + " Payment", JOptionPane.INFORMATION_MESSAGE);
                
                // Simulate processing time
                Thread.sleep(1500);

                // Success message
                JOptionPane.showMessageDialog(parent,
                        "✅ " + method + " payment successful!\nTransaction ID: " + fakeId + 
                        "\nAmount: ₹" + String.format("%.2f", amount),
                        "Payment Success", JOptionPane.INFORMATION_MESSAGE);

                if (onSuccess != null) onSuccess.run();
            } else {
                JOptionPane.showMessageDialog(parent, 
                    method + " payment cancelled.", 
                    "Payment Cancelled", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, 
                method + " payment failed: " + e.getMessage(), 
                "Payment Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static String getMethodSpecificMessage(String method, double amount) {
        switch (method.toUpperCase()) {
            case "PAYPAL":
                return "Simulate PayPal payment\nEmail required for transaction";
            case "UPI":
                return "Simulate UPI payment\nScan QR code or enter UPI ID";
            case "CARD":
                return "Simulate Card payment\nEnter card details (test mode)";
            case "CASH":
                return "Cash payment\nPlease collect: ₹" + String.format("%.2f", amount);
            default:
                return "Simulate " + method + " payment";
        }
    }
    
    private static String getProcessingMessage(String method) {
        switch (method.toUpperCase()) {
            case "PAYPAL":
                return "Redirecting to PayPal...\nPlease wait while we process your payment";
            case "UPI":
                return "Waiting for UPI approval...\nPlease check your UPI app";
            case "CARD":
                return "Processing card payment...\nVerifying transaction details";
            case "CASH":
                return "Processing cash payment...\nCounting currency";
            default:
                return "Processing " + method + " payment...\nThis may take a few seconds";
        }
    }
}