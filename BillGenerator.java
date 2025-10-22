import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BillGenerator {
    private double totalAmount;
    private String customerName;
    private String customerPhone;
    private Date purchaseDate;
    private String paymentMethod;
    private List<Book> cartItems;
    private int userId;
    
    // GST rates for books (5% in India)
    private static final double CGST_RATE = 2.5;
    private static final double SGST_RATE = 2.5;
    
    private BillDAO billDAO;
    private CustomerLoyaltyService loyaltyService;

    public BillGenerator(double totalAmount, String customerName, String customerPhone) {
        this(totalAmount, customerName, customerPhone, "Cash", null, 1);
    }

    public BillGenerator(double totalAmount, String customerName, String customerPhone, String paymentMethod) {
        this(totalAmount, customerName, customerPhone, paymentMethod, null, 1);
    }

    public BillGenerator(double totalAmount, String customerName, String customerPhone, 
                        String paymentMethod, List<Book> cartItems, int userId) {
        this.totalAmount = totalAmount;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.paymentMethod = paymentMethod;
        this.cartItems = cartItems;
        this.userId = userId;
        this.purchaseDate = new Date();
        this.billDAO = new BillDAO();
        this.loyaltyService = new CustomerLoyaltyService(billDAO);
    }

    public String generateBill() {
        StringBuilder bill = new StringBuilder();
        
        // Calculate GST components (5% total)
        double gstAmount = totalAmount * 0.05;
        double finalAmount = totalAmount + gstAmount;
        
        // Calculate loyalty information
        int loyaltyPoints = 0;
        String customerTier = "New Customer";
        double discountApplied = 0.0;
        
        if (customerName != null && !customerName.trim().isEmpty()) {
            loyaltyPoints = loyaltyService.calculateLoyaltyPoints(customerName, totalAmount);
            customerTier = loyaltyService.getCustomerTier(customerName);
            discountApplied = loyaltyService.calculateDiscount(customerName, totalAmount);
        }

        // Bill Header
        bill.append("========================================\n");
        bill.append("           BIBLIOFLOW BOOKSTORE         \n");
        bill.append("========================================\n");
        bill.append("Date: ").append(new SimpleDateFormat("dd/MM/yyyy").format(purchaseDate)).append("\n");
        bill.append("Time: ").append(new SimpleDateFormat("HH:mm:ss").format(purchaseDate)).append("\n");
        bill.append("Bill No: BFLW").append(String.format("%06d", System.currentTimeMillis() % 1000000)).append("\n");
        bill.append("----------------------------------------\n");
        
        // Customer Information
        if (customerName != null && !customerName.trim().isEmpty()) {
            bill.append("Customer: ").append(customerName).append("\n");
            bill.append("Customer Tier: ").append(customerTier).append("\n");
        } else {
            bill.append("Customer: Walk-in Customer\n");
        }
        bill.append("----------------------------------------\n");
        
        // Itemized Breakdown
        bill.append("ITEM BREAKDOWN:\n");
        bill.append("----------------------------------------\n");
        
        if (cartItems != null && !cartItems.isEmpty()) {
            for (Book book : cartItems) {
                String title = book.getTitle().length() > 25 ? book.getTitle().substring(0, 22) + "..." : book.getTitle();
                bill.append(String.format("%-25s %2d x ₹%6.2f\n", 
                    title, book.getCartQuantity(), book.getPrice()));
                double itemTotal = book.getPrice() * book.getCartQuantity();
                bill.append(String.format("%25s ₹%9.2f\n", "", itemTotal));
            }
            bill.append("----------------------------------------\n");
        }
        
        bill.append(String.format("%-30s ₹%9.2f\n", "Subtotal", totalAmount));
        
        if (discountApplied > 0) {
            bill.append(String.format("%-30s ₹%9.2f\n", "Loyalty Discount (-)", discountApplied));
        }
        
        bill.append(String.format("%-30s ₹%9.2f\n", "GST (5%)", gstAmount));
        bill.append("----------------------------------------\n");
        bill.append(String.format("%-30s ₹%9.2f\n", "GRAND TOTAL", finalAmount));
        bill.append("----------------------------------------\n");
        bill.append("Payment Method: ").append(paymentMethod).append("\n");
        bill.append("Amount Paid: ₹").append(String.format("%.2f", finalAmount)).append("\n");
        bill.append("Change: ₹0.00\n");
        bill.append("========================================\n");
        
        // GST Summary
        bill.append("GST SUMMARY:\n");
        bill.append(String.format("%-20s ₹%9.2f\n", "Taxable Amount", totalAmount));
        bill.append(String.format("%-20s ₹%9.2f\n", "CGST (2.5%)", gstAmount/2));
        bill.append(String.format("%-20s ₹%9.2f\n", "SGST (2.5%)", gstAmount/2));
        bill.append(String.format("%-20s ₹%9.2f\n", "Total GST", gstAmount));
        bill.append("========================================\n");
        
        // Loyalty Information
        if (customerName != null && !customerName.trim().isEmpty()) {
            bill.append("LOYALTY INFORMATION:\n");
            bill.append("Points Earned: ").append(loyaltyPoints).append(" pts\n");
            
            CustomerLoyaltyService.CustomerLoyalty loyalty = loyaltyService.getCustomerLoyalty(customerName);
            if (loyalty != null) {
                bill.append("Total Points: ").append(loyalty.getTotalPoints() + loyaltyPoints).append(" pts\n");
            }
            
            bill.append("Current Tier: ").append(customerTier).append("\n");
            
            if (discountApplied > 0) {
                bill.append("Discount Saved: ₹").append(String.format("%.2f", discountApplied)).append("\n");
            }
            bill.append("========================================\n");
        }
        
        // Footer
        bill.append("\n");
        bill.append("THANK YOU FOR SHOPPING WITH US!\n");
        bill.append("Visit us again at BiblioFlow\n");
        bill.append("Contact: support@biblioflow.com\n");
        bill.append("Phone: +91-9876543210\n");
        bill.append("** Books are non-refundable **\n");
        bill.append("** GST Invoice **\n");
        
        // Save bill to file
        billDAO.saveBillToFile(bill.toString(), customerName);
        
        return bill.toString();
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public void setCartItems(List<Book> cartItems) {
        this.cartItems = cartItems;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
}