import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class TopSellingBooksApp extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private BillDAO billDAO = new BillDAO();

    public TopSellingBooksApp() {
        setLayout(new BorderLayout());
        String[] columns = {"Book Name", "Total Quantity Sold"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadTopSellingBooks();
    }

    private void loadTopSellingBooks() {
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
    }
}
