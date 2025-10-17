import javax.swing.*;
import java.awt.*;
import java.sql.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.*;

public class ProfitLossApp extends JFrame {
    private JButton showGraphBtn;
    private BillDAO billDAO = new BillDAO();

    public ProfitLossApp() {
        setTitle("Bookstore Profit & Loss Tracker");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        setLocationRelativeTo(null);

        showGraphBtn = new JButton("Show Profit & Loss Report");
        add(showGraphBtn);

        showGraphBtn.addActionListener(e -> generateReport());

        setVisible(true);
    }

    private void generateReport() {
        ProfitReport report = getProfitReport();

        JOptionPane.showMessageDialog(this,
                String.format("Revenue: ₹%.2f\nCost: ₹%.2f\nProfit: ₹%.2f (%.2f%%)", 
                              report.getRevenue(), report.getCost(), report.getProfit(), report.getProfitPercent()));

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(report.getRevenue(), "Amount", "Revenue");
        dataset.addValue(report.getCost(), "Amount", "Cost");
        dataset.addValue(report.getProfit(), "Amount", "Profit");

        JFreeChart chart = ChartFactory.createBarChart(
                "Profit & Loss Report",
                "Category",
                "Amount (₹)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(chart);
        JFrame chartFrame = new JFrame("Profit & Loss Graph");
        chartFrame.setSize(600, 400);
        chartFrame.add(chartPanel);
        chartFrame.setLocationRelativeTo(null);
        chartFrame.setVisible(true);
    }

    // Inner class for storing profit info
    public class ProfitReport {
        private double revenue, cost, profit, profitPercent;

        public ProfitReport(double revenue, double cost, double profit, double profitPercent) {
            this.revenue = revenue;
            this.cost = cost;
            this.profit = profit;
            this.profitPercent = profitPercent;
        }

        public double getRevenue() { return revenue; }
        public double getCost() { return cost; }
        public double getProfit() { return profit; }
        public double getProfitPercent() { return profitPercent; }
    }

    private ProfitReport getProfitReport() {
        double totalRevenue = 0, totalCost = 0;

        String sql = """
            SELECT bi.quantity, bi.unit_price, b.cost_price
            FROM bill_items bi
            JOIN books b ON bi.book_id = b.id
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int qty = rs.getInt("quantity");
                double price = rs.getDouble("unit_price");
                double cost = rs.getDouble("cost_price");

                totalRevenue += price * qty;
                totalCost += cost * qty;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        double profit = totalRevenue - totalCost;
        double profitPercent = totalRevenue == 0 ? 0 : (profit / totalRevenue) * 100;

        return new ProfitReport(totalRevenue, totalCost, profit, profitPercent);
    }

    public static void main(String[] args) {
        new ProfitLossApp();
    }
}
