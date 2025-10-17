public class BillItem {
    private String bookName;
    private int quantity;
    private double price;

    public BillItem(String bookName, int quantity) {
        this.bookName = bookName;
        this.quantity = quantity;
    }

    public String getBookName() {
        return bookName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    private double unitPrice;

    public BillItem(String bookName, int quantity, double unitPrice) {
        this.bookName = bookName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
    public double getUnitPrice() { return unitPrice; }

    public double getPrice() {
        return this.price;
    }

}
