import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {

    public boolean createBill(List<Book> cartItems, double totalAmount, String customerName, String customerPhone, int userId) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // Generate unique bill number
            String billNumber = "BFL" + System.currentTimeMillis();

            // Calculate GST and final amount
            double gstAmount = totalAmount * 0.18;
            double finalAmount = totalAmount + gstAmount;

            // 1️⃣ Insert into bills table
            String billSql = "INSERT INTO bills (bill_number, customer_name, customer_phone, total_amount, gst_amount, final_amount, payment_method, payment_status, order_status, user_id) " +
                             "VALUES (?, ?, ?, ?, ?, ?, 'Cash', 'Paid', 'Delivered', ?)";

            PreparedStatement billStmt = conn.prepareStatement(billSql, Statement.RETURN_GENERATED_KEYS);
            billStmt.setString(1, billNumber);
            billStmt.setString(2, customerName);
            billStmt.setString(3, customerPhone);
            billStmt.setDouble(4, totalAmount);
            billStmt.setDouble(5, gstAmount);
            billStmt.setDouble(6, finalAmount);
            billStmt.setInt(7, userId);

            int billRows = billStmt.executeUpdate();
            if (billRows == 0) {
                conn.rollback();
                return false;
            }

            // 2️⃣ Get generated bill ID
            ResultSet rs = billStmt.getGeneratedKeys();
            int billId = 0;
            if (rs.next()) {
                billId = rs.getInt(1);
            } else {
                conn.rollback();
                return false;
            }

            // 3️⃣ Insert each cart item into bill_items & reduce stock
            String itemSql = "INSERT INTO bill_items (bill_id, book_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
            PreparedStatement itemStmt = conn.prepareStatement(itemSql);

            String stockSql = "UPDATE books SET stock_quantity = stock_quantity - ? WHERE id = ?";
            PreparedStatement stockStmt = conn.prepareStatement(stockSql);

            for (Book book : cartItems) {
                int qty = book.getCartQuantity();  // Number of copies customer is buying

                // Insert into bill_items
                itemStmt.setInt(1, billId);
                itemStmt.setInt(2, book.getId());
                itemStmt.setInt(3, qty);
                itemStmt.setDouble(4, book.getPrice());
                itemStmt.addBatch();

                // Update stock in books table
                stockStmt.setInt(1, qty);
                stockStmt.setInt(2, book.getId());
                stockStmt.addBatch();
            }

            itemStmt.executeBatch();
            stockStmt.executeBatch();

            // 4️⃣ Commit transaction
            conn.commit();
            return true;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Bill> getAllBills() {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT id, bill_number, customer_name, customer_phone, total_amount, gst_amount, final_amount, payment_status, order_status, created_at " +
                     "FROM bills ORDER BY created_at DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Bill bill = new Bill();
                bill.setId(rs.getInt("id"));
                bill.setBillNumber(rs.getString("bill_number"));
                bill.setCustomerName(rs.getString("customer_name"));
                bill.setCustomerPhone(rs.getString("customer_phone"));
                bill.setTotalAmount(rs.getDouble("total_amount"));
                bill.setGstAmount(rs.getDouble("gst_amount"));
                bill.setFinalAmount(rs.getDouble("final_amount"));
                bill.setPaymentStatus(rs.getString("payment_status"));
                bill.setOrderStatus(rs.getString("order_status"));
                bill.setDate(rs.getString("created_at")); // assuming column exists
                bills.add(bill);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bills;
    }

    public List<BillItem> getAllBillItems() {
        List<BillItem> list = new ArrayList<>();

        String sql = """
            SELECT b.title AS book_name, SUM(bi.quantity) AS total_quantity
            FROM bill_items bi
            JOIN books b ON bi.book_id = b.id
            GROUP BY b.title
            ORDER BY total_quantity DESC
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String bookName = rs.getString("book_name");
                int quantity = rs.getInt("total_quantity");
                list.add(new BillItem(bookName, quantity));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<BillItem> getBillItemsByBillId(int billId) {
        List<BillItem> items = new ArrayList<>();
        String sql = "SELECT b.title AS book_name, bi.quantity " +
                     "FROM bill_items bi " +
                     "JOIN books b ON bi.book_id = b.id " +
                     "WHERE bi.bill_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String bookName = rs.getString("book_name");
                    int quantity = rs.getInt("quantity");
                    items.add(new BillItem(bookName, quantity));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public List<TopSellingItem> getTopSellingBooks() {
    List<TopSellingItem> list = new ArrayList<>();
    String sql = """
        SELECT b.title AS book_name, SUM(bi.quantity) AS total_quantity, SUM(bi.quantity * bi.unit_price) AS total_revenue
        FROM bill_items bi
        JOIN books b ON bi.book_id = b.id
        GROUP BY b.title
        ORDER BY total_quantity DESC
    """;

    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            String name = rs.getString("book_name");
            int qty = rs.getInt("total_quantity");
            double revenue = rs.getDouble("total_revenue");
            list.add(new TopSellingItem(name, qty, revenue));
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return list;
}


public List<SalesSummary> getSalesSummary(String period) {
    String sql = "";
    switch (period) {
        case "daily":
            sql = "SELECT DATE(created_at) AS period, SUM(final_amount) AS revenue, SUM(total_books) AS books_sold " +
                  "FROM bills GROUP BY DATE(created_at)";
            break;
        case "weekly":
            sql = "SELECT YEAR(created_at) AS yr, WEEK(created_at) AS wk, SUM(final_amount) AS revenue, SUM(total_books) AS books_sold " +
                  "FROM bills GROUP BY YEAR(created_at), WEEK(created_at)";
            break;
        case "monthly":
            sql = "SELECT YEAR(created_at) AS yr, MONTH(created_at) AS mn, SUM(final_amount) AS revenue, SUM(total_books) AS books_sold " +
                  "FROM bills GROUP BY YEAR(created_at), MONTH(created_at)";
            break;
    }
    List<SalesSummary> list = new ArrayList<>();
    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            double revenue = rs.getDouble("revenue");
            int books = rs.getInt("books_sold");
            String timePeriod = period.equals("daily") ? rs.getString("period") :
                                period.equals("weekly") ? "Week " + rs.getInt("wk") + ", " + rs.getInt("yr") :
                                                          rs.getInt("mn") + "/" + rs.getInt("yr");
            list.add(new SalesSummary(timePeriod, books, revenue));
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return list;
}

public Bill getBillByNumber(String billNumber) {
    String sql = "SELECT id, bill_number, customer_name, customer_phone, total_amount, gst_amount, final_amount, payment_status, order_status, created_at " +
                 "FROM bills WHERE bill_number = ?";

    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, billNumber);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Bill bill = new Bill();
                bill.setId(rs.getInt("id"));
                bill.setBillNumber(rs.getString("bill_number"));
                bill.setCustomerName(rs.getString("customer_name"));
                bill.setCustomerPhone(rs.getString("customer_phone"));
                bill.setTotalAmount(rs.getDouble("total_amount"));
                bill.setGstAmount(rs.getDouble("gst_amount"));
                bill.setFinalAmount(rs.getDouble("final_amount"));
                bill.setPaymentStatus(rs.getString("payment_status"));
                bill.setOrderStatus(rs.getString("order_status"));
                bill.setDate(rs.getString("created_at"));
                return bill;
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return null; // not found
}

        class TopSellingItem {
            String bookName;
            int quantity;
            double revenue;
            public TopSellingItem(String bookName, int quantity, double revenue) {
                this.bookName = bookName;
                this.quantity = quantity;
                this.revenue = revenue;
            }
        }

        class SalesSummary {
            String period;
            int booksSold;
            double revenue;
            public SalesSummary(String period, int booksSold, double revenue) {
                this.period = period;
                this.booksSold = booksSold;
                this.revenue = revenue;
            }
        }

}
