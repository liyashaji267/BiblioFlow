// PayPalSimulator.java
import javax.swing.*;

public class PayPalSimulator {
    public static void simulatePayPalPayment(JDialog parent, double amount, String customerName, Runnable onSuccess) {
        int response = JOptionPane.showConfirmDialog(parent,
            "Simulate PayPal payment of ₹" + String.format("%.2f", amount) + 
            " for customer: " + customerName + "\n\nClick OK to simulate successful payment.",
            "PayPal Payment Simulation",
            JOptionPane.OK_CANCEL_OPTION);
            
        if (response == JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(parent, "PayPal payment successful!");
            onSuccess.run();
        }
    }
}