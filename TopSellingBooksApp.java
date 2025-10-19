import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.*;
import java.util.List;

public class TopSellingBooksApp extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private BillDAO billDAO = new BillDAO();

    public TopSellingBooksApp() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        JLabel headerLabel = new JLabel("Top Selling Books", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(33, 150, 243));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        add(headerLabel, BorderLayout.NORTH);

        String[] columns = {"Book Name", "Total Quantity Sold"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(200, 200, 200));
        table.setBackground(Color.WHITE);
        table.setForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(33, 150, 243));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Book Sales Data"));
        add(scrollPane, BorderLayout.CENTER);

        loadTopSellingBooks();
    }

    private void loadTopSellingBooks() {
        try {
            // Get all bill items from the database
            List<BillItem> billItems = billDAO.getAllBillItems();

            // Count total quantities per book
            Map<String, Integer> salesMap = new HashMap<>();
            for (BillItem item : billItems) {
                String bookName = item.getBookName();
                int quantity = item.getQuantity();
                salesMap.put(bookName, salesMap.getOrDefault(bookName, 0) + quantity);
            }

            // Sort by quantity sold (descending)
            List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(salesMap.entrySet());
            sortedList.sort((a, b) -> b.getValue().compareTo(a.getValue()));

            // Display in table
            tableModel.setRowCount(0);
            for (Map.Entry<String, Integer> entry : sortedList) {
                tableModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading top selling books: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
