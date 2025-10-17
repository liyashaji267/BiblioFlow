import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    // Save order and return generated id (-1 on failure)
    public int saveOrder(SubstoreOrder order) throws SQLException {
        // We omit created_at column so DB default CURRENT_TIMESTAMP is used
        String sql = "INSERT INTO orders (order_number, customer_name, customer_phone, total_amount, order_status, items_summary, user_id) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, order.getOrderNumber());
            ps.setString(2, order.getCustomerName());
            ps.setString(3, order.getCustomerPhone());
            ps.setDouble(4, order.getTotalAmount());
            ps.setString(5, order.getOrderStatus());
            ps.setString(6, order.getItemsSummary());
            ps.setObject(7, null); // user_id (null if not provided)

            int rows = ps.executeUpdate();
            if (rows == 0) return -1;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public List<SubstoreOrder> getAllOrders() throws SQLException {
        String sql = "SELECT id, order_number, customer_name, customer_phone, total_amount, order_status, items_summary, created_at FROM orders ORDER BY created_at DESC";
        List<SubstoreOrder> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SubstoreOrder o = new SubstoreOrder();
                o.setId(rs.getInt("id"));
                o.setOrderNumber(rs.getString("order_number"));
                o.setCustomerName(rs.getString("customer_name"));
                o.setCustomerPhone(rs.getString("customer_phone"));
                o.setTotalAmount(rs.getDouble("total_amount"));
                o.setOrderStatus(rs.getString("order_status"));
                o.setItemsSummary(rs.getString("items_summary"));
                Timestamp ts = rs.getTimestamp("created_at");
                if (ts != null) o.setCreatedAt(ts.getTime());
                list.add(o);
            }
        }
        return list;
    }

    public boolean updateOrderStatus(int orderId, String status) throws SQLException {
        String sql = "UPDATE orders SET order_status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        }
    }

    // Save OTP and generated timestamp (otp_generated_at)
    public boolean saveOrderOtp(int orderId, String otp) throws SQLException {
        String sql = "UPDATE orders SET otp = ?, otp_generated_at = NOW() WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, otp);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        }
    }

    // Verify OTP and set verified timestamp (otp_verified_at) when match
    public boolean verifyOtp(int orderId, String otp) throws SQLException {
        String sql = "SELECT otp FROM orders WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String stored = rs.getString("otp");
                    if (stored != null && stored.equals(otp)) {
                        String updateSql = "UPDATE orders SET otp_verified_at = NOW() WHERE id = ?";
                        try (PreparedStatement ps2 = conn.prepareStatement(updateSql)) {
                            ps2.setInt(1, orderId);
                            ps2.executeUpdate();
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
