import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class CustomerLoyaltyService {
    private BillDAO billDAO;
    private Map<String, CustomerLoyalty> customerLoyaltyMap;
    
    // Loyalty program constants
    private static final double REGULAR_CUSTOMER_THRESHOLD = 1000.0; // ₹1000 spent
    private static final double PREMIUM_CUSTOMER_THRESHOLD = 5000.0; // ₹5000 spent
    private static final double POINTS_PER_RUPEE = 1.0; // 1 point per ₹1 spent
    private static final double REGULAR_DISCOUNT_RATE = 0.05; // 5% discount
    private static final double PREMIUM_DISCOUNT_RATE = 0.10; // 10% discount
    
    public CustomerLoyaltyService(BillDAO billDAO) {
        this.billDAO = billDAO;
        this.customerLoyaltyMap = new HashMap<>();
        loadCustomerData();
    }
    
    private void loadCustomerData() {
        Map<String, Double> customerTotals = billDAO.getCustomerPurchaseHistory();
        for (Map.Entry<String, Double> entry : customerTotals.entrySet()) {
            String customerName = entry.getKey();
            double totalSpent = entry.getValue();
            int purchaseCount = billDAO.getCustomerPurchaseCount(customerName);
            
            CustomerLoyalty loyalty = new CustomerLoyalty(customerName, totalSpent, purchaseCount);
            customerLoyaltyMap.put(customerName, loyalty);
        }
    }
    
    public CustomerLoyalty getCustomerLoyalty(String customerName) {
        return customerLoyaltyMap.get(customerName);
    }
    
    public Map<String, CustomerLoyalty> getAllCustomers() {
        return new HashMap<>(customerLoyaltyMap);
    }
    
    public double calculateDiscount(String customerName, double currentBillAmount) {
        CustomerLoyalty loyalty = getCustomerLoyalty(customerName);
        if (loyalty == null) {
            return 0.0;
        }
        
        if (loyalty.getTotalSpent() >= PREMIUM_CUSTOMER_THRESHOLD) {
            return currentBillAmount * PREMIUM_DISCOUNT_RATE;
        } else if (loyalty.getTotalSpent() >= REGULAR_CUSTOMER_THRESHOLD) {
            return currentBillAmount * REGULAR_DISCOUNT_RATE;
        }
        
        return 0.0;
    }
    
    public int calculateLoyaltyPoints(String customerName, double billAmount) {
        return (int) (billAmount * POINTS_PER_RUPEE);
    }
    
    public String getCustomerTier(String customerName) {
        CustomerLoyalty loyalty = getCustomerLoyalty(customerName);
        if (loyalty == null) return "New Customer";
        
        if (loyalty.getTotalSpent() >= PREMIUM_CUSTOMER_THRESHOLD) {
            return "Premium";
        } else if (loyalty.getTotalSpent() >= REGULAR_CUSTOMER_THRESHOLD) {
            return "Regular";
        } else {
            return "Standard";
        }
    }
    
    public void updateCustomerAfterPurchase(String customerName, double billAmount) {
        CustomerLoyalty loyalty = customerLoyaltyMap.get(customerName);
        if (loyalty == null) {
            loyalty = new CustomerLoyalty(customerName, billAmount, 1);
        } else {
            loyalty.addPurchase(billAmount);
        }
        customerLoyaltyMap.put(customerName, loyalty);
    }
    
    // Customer Loyalty Data Class
    public static class CustomerLoyalty {
        private String customerName;
        private double totalSpent;
        private int purchaseCount;
        private int totalPoints;
        
        public CustomerLoyalty(String customerName, double totalSpent, int purchaseCount) {
            this.customerName = customerName;
            this.totalSpent = totalSpent;
            this.purchaseCount = purchaseCount;
            this.totalPoints = (int) (totalSpent * POINTS_PER_RUPEE);
        }
        
        public void addPurchase(double amount) {
            this.totalSpent += amount;
            this.purchaseCount++;
            this.totalPoints += (int) (amount * POINTS_PER_RUPEE);
        }
        
        // Getters
        public String getCustomerName() { return customerName; }
        public double getTotalSpent() { return totalSpent; }
        public int getPurchaseCount() { return purchaseCount; }
        public int getTotalPoints() { return totalPoints; }
        
        @Override
        public String toString() {
            return String.format("Customer: %s\nTotal Spent: ₹%.2f\nPurchases: %d\nLoyalty Points: %d\nTier: %s",
                customerName, totalSpent, purchaseCount, totalPoints,
                getTier());
        }
        
        public String getTier() {
            if (totalSpent >= PREMIUM_CUSTOMER_THRESHOLD) return "Premium";
            if (totalSpent >= REGULAR_CUSTOMER_THRESHOLD) return "Regular";
            return "Standard";
        }
    }
}