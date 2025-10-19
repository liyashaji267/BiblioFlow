import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    private Connection connection;

    public TransactionDAO(Connection connection) {
        this.connection = connection;
    }

    public TransactionDAO() {
        try {
            this.connection = DBUtil.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            this.connection = null; // prevent null pointer later
        }
    }

    // Create a new transaction record
    public boolean createTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (bill_id, payment_method, amount, status) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, transaction.getBillId());
            stmt.setString(2, transaction.getPaymentMethod());
            stmt.setDouble(3, transaction.getAmount());
            stmt.setString(4, transaction.getStatus());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        transaction.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get all transactions with bill details
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, b.bill_number, b.customer_name " +
                    "FROM transactions t " +
                    "JOIN bills b ON t.bill_id = b.id " +
                    "ORDER BY t.transaction_date DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Transaction transaction = extractTransactionFromResultSet(rs);
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    // Get transactions by date range
    public List<Transaction> getTransactionsByDateRange(Date startDate, Date endDate) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, b.bill_number, b.customer_name " +
                    "FROM transactions t " +
                    "JOIN bills b ON t.bill_id = b.id " +
                    "WHERE DATE(t.transaction_date) BETWEEN ? AND ? " +
                    "ORDER BY t.transaction_date DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Transaction transaction = extractTransactionFromResultSet(rs);
                    transactions.add(transaction);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    // Get transactions by payment method
    public List<Transaction> getTransactionsByPaymentMethod(String paymentMethod) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, b.bill_number, b.customer_name " +
                    "FROM transactions t " +
                    "JOIN bills b ON t.bill_id = b.id " +
                    "WHERE t.payment_method = ? " +
                    "ORDER BY t.transaction_date DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, paymentMethod);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Transaction transaction = extractTransactionFromResultSet(rs);
                    transactions.add(transaction);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    // Get transaction by ID
    public Transaction getTransactionById(int id) {
        String sql = "SELECT t.*, b.bill_number, b.customer_name " +
                    "FROM transactions t " +
                    "JOIN bills b ON t.bill_id = b.id " +
                    "WHERE t.id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractTransactionFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get total sales by payment method
    public double getTotalSalesByPaymentMethod(String paymentMethod) {
        String sql = "SELECT SUM(amount) as total FROM transactions WHERE payment_method = ? AND status = 'SUCCESS'";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, paymentMethod);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // Get daily transaction summary
    public List<TransactionSummary> getDailyTransactionSummary() {
        List<TransactionSummary> summaries = new ArrayList<>();
        String sql = "SELECT DATE(transaction_date) as transaction_day, " +
                    "COUNT(*) as transaction_count, " +
                    "SUM(amount) as total_amount " +
                    "FROM transactions " +
                    "WHERE status = 'SUCCESS' " +
                    "GROUP BY DATE(transaction_date) " +
                    "ORDER BY transaction_day DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                TransactionSummary summary = new TransactionSummary();
                summary.setDate(rs.getDate("transaction_day"));
                summary.setTransactionCount(rs.getInt("transaction_count"));
                summary.setTotalAmount(rs.getDouble("total_amount"));
                summaries.add(summary);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return summaries;
    }

    // Helper method to extract Transaction from ResultSet
    private Transaction extractTransactionFromResultSet(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(rs.getInt("id"));
        transaction.setBillId(rs.getInt("bill_id"));
        transaction.setBillNumber(rs.getString("bill_number"));
        transaction.setCustomerName(rs.getString("customer_name"));
        transaction.setPaymentMethod(rs.getString("payment_method"));
        transaction.setAmount(rs.getDouble("amount"));
        transaction.setStatus(rs.getString("status"));
        transaction.setTransactionDate(rs.getTimestamp("transaction_date"));
        return transaction;
    }

    // Update transaction status
    public boolean updateTransactionStatus(int transactionId, String status) {
        String sql = "UPDATE transactions SET status = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, transactionId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete transaction (for admin purposes)
    public boolean deleteTransaction(int transactionId) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, transactionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}