import java.sql.Timestamp;

public class Bill {
    private int id;
    private String billNumber;
    private String customerName;
    private String customerPhone;
    private double totalAmount;
    private double gstAmount;
    private double finalAmount;
    private String paymentStatus;
    private String orderStatus;
    private String date;
    private String paymentMethod;
    private int userId; // Add this field

    // Default constructor
    public Bill() {
    }

    // Parameterized constructor (optional)
    public Bill(int id, String billNumber, String customerName, String customerPhone, 
                double totalAmount, double gstAmount, double finalAmount, 
                String paymentStatus, String orderStatus, String date, String paymentMethod, int userId) {
        this.id = id;
        this.billNumber = billNumber;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.totalAmount = totalAmount;
        this.gstAmount = gstAmount;
        this.finalAmount = finalAmount;
        this.paymentStatus = paymentStatus;
        this.orderStatus = orderStatus;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.userId = userId;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getGstAmount() { return gstAmount; }
    public void setGstAmount(double gstAmount) { this.gstAmount = gstAmount; }

    public double getFinalAmount() { return finalAmount; }
    public void setFinalAmount(double finalAmount) { this.finalAmount = finalAmount; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}