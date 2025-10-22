public class BillItem {
    private String bookName;
    private int quantity;
    private double unitPrice;

    public BillItem(String bookName, int quantity) {
        this.bookName = bookName;
        this.quantity = quantity;
        this.unitPrice = 0.0;
    }

    public BillItem(String bookName, int quantity, double unitPrice) {
        this.bookName = bookName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // Getters and setters
    public String getBookName() { return bookName; }
    public void setBookName(String bookName) { this.bookName = bookName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public double getPrice() { return unitPrice; }
}