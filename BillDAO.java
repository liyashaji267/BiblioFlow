// BillDAO.java
import java.sql.*;
import java.util.List;

public class BillDAO {
    public boolean createBill(List<Book> cartItems, double totalAmount, String customerName, String customerPhone, int userId) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            
            // Generate bill number
            String billNumber = "BFL" + System.currentTimeMillis();
            
            // Calculate GST and final amount
            double gstAmount = totalAmount * 0.18;
            double finalAmount = totalAmount + gstAmount;
            
            // Insert bill
            String billSql = "INSERT INTO bills (bill_number, customer_name, customer_phone, total_amount, gst_amount, final_amount, payment_method, payment_status, order_status, user_id) " +
                           "VALUES (?, ?, ?, ?, ?, ?, 'Completed', 'Paid', 'Delivered', ?)";
            
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
            
            // For now, just commit and return true
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
}