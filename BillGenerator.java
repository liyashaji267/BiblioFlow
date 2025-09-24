// BillGenerator.java
import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BillGenerator {
    private double totalAmount;
    private String customerName;
    private String customerPhone;
    private Date billDate;

    public BillGenerator(double totalAmount, String customerName, String customerPhone) {
        this.totalAmount = totalAmount;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.billDate = new Date();
    }

    public void generateBill() {
        String billContent = generateBillContent();
        JOptionPane.showMessageDialog(null, 
            "<html><pre>" + billContent + "</pre></html>", 
            "BiblioFlow - Bill", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private String generateBillContent() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        StringBuilder bill = new StringBuilder();
        
        bill.append("================================\n");
        bill.append("       BIBLIOFLOW BOOKSTORE     \n");
        bill.append("================================\n");
        bill.append("Date: ").append(sdf.format(billDate)).append("\n");
        bill.append("Bill No: BFL").append(System.currentTimeMillis()).append("\n");
        bill.append("--------------------------------\n");
        
        if (customerName != null && !customerName.trim().isEmpty()) {
            bill.append("Customer: ").append(customerName).append("\n");
        }
        if (customerPhone != null && !customerPhone.trim().isEmpty()) {
            bill.append("Phone: ").append(customerPhone).append("\n");
        }
        
        bill.append("--------------------------------\n");
        bill.append("Total Amount: ₹").append(String.format("%.2f", totalAmount)).append("\n");
        
        // Calculate GST (18%)
        double gst = totalAmount * 0.18;
        bill.append("GST (18%): ₹").append(String.format("%.2f", gst)).append("\n");
        
        double finalAmount = totalAmount + gst;
        bill.append("Final Amount: ₹").append(String.format("%.2f", finalAmount)).append("\n");
        bill.append("================================\n");
        bill.append("     Thank You for Shopping!    \n");
        bill.append("================================\n");
        
        return bill.toString();
    }
}