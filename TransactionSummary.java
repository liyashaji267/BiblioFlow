import java.sql.Date;

public class TransactionSummary {
    private Date date;
    private int transactionCount;
    private double totalAmount;

    // Constructors
    public TransactionSummary() {}

    // Getters and Setters
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public int getTransactionCount() { return transactionCount; }
    public void setTransactionCount(int transactionCount) { this.transactionCount = transactionCount; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
}