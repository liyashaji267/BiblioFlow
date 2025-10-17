import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * StoreReports.java
 * Acts as a unified reports dashboard for all report-related panels:
 * - Top Selling Books
 * - Sales Report
 * - Profit/Loss Tracker
 * - Export Reports (Excel/PDF)
 */
public class StoreReports extends JPanel {
    private CardLayout cardLayout;
    private JPanel contentPanel;

    public StoreReports() {
        setLayout(new BorderLayout());

        // ---------- TOP NAVIGATION BUTTONS ----------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton topSellingBtn = new JButton("Top Selling Books");
        JButton salesReportBtn = new JButton("Sales Report");
        JButton profitLossBtn = new JButton("Profit & Loss");
        JButton exportReportBtn = new JButton("Export Reports");

        buttonPanel.add(topSellingBtn);
        buttonPanel.add(salesReportBtn);
        buttonPanel.add(profitLossBtn);
        buttonPanel.add(exportReportBtn);

        add(buttonPanel, BorderLayout.NORTH);

        // ---------- CONTENT AREA ----------
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Create instances of each report panel
        JPanel topSellingPanel = new TopSellingBooksApp();
        JPanel salesPanel = wrapFramePanel(new SalesReportApp());
        JPanel profitLossPanel = wrapFramePanel(new ProfitLossApp());
        JPanel exportPanel = wrapFramePanel(new ExportReportsApp());

        // Add panels to the card layout
        contentPanel.add(topSellingPanel, "TOP_SELLING");
        contentPanel.add(salesPanel, "SALES");
        contentPanel.add(profitLossPanel, "PROFIT_LOSS");
        contentPanel.add(exportPanel, "EXPORT");

        add(contentPanel, BorderLayout.CENTER);

        // ---------- BUTTON ACTIONS ----------
        topSellingBtn.addActionListener(e -> cardLayout.show(contentPanel, "TOP_SELLING"));
        salesReportBtn.addActionListener(e -> cardLayout.show(contentPanel, "SALES"));
        profitLossBtn.addActionListener(e -> cardLayout.show(contentPanel, "PROFIT_LOSS"));
        exportReportBtn.addActionListener(e -> cardLayout.show(contentPanel, "EXPORT"));
    }

    /**
     * Wrap JFrame-based apps into a JPanel so they can be embedded.
     */
    private JPanel wrapFramePanel(JFrame frameApp) {
        // Extract content from JFrame into a JPanel wrapper
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(frameApp.getContentPane(), BorderLayout.CENTER);
        frameApp.dispose(); // Close original frame
        return panel;
    }

    // ---------- For testing standalone ----------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("📊 Store Reports Dashboard");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);
            frame.add(new StoreReports());
            frame.setVisible(true);
        });
    }
}
