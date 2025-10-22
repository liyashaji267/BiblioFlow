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

        JTextArea textArea = new JTextArea(billContent);
        textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 14));
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(400, 500));

        JOptionPane.showMessageDialog(null, scrollPane, 
            "BiblioFlow - Bill", JOptionPane.INFORMATION_MESSAGE);
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
