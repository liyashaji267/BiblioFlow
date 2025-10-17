import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class BillHistoryPanel extends JPanel {
    private JTable table;
    private BillDAO billDAO = new BillDAO();
    private DefaultTableModel tableModel;
    private JTable itemsTable;
    private DefaultTableModel itemsTableModel;
    private List<Bill> bills;


    private void initItemsTable() {
        String[] itemColumns = {"Book Name", "Quantity"};
        itemsTableModel = new DefaultTableModel(itemColumns, 0);
        itemsTable = new JTable(itemsTableModel);
    }

    public BillHistoryPanel() {
        setLayout(new BorderLayout());
        
        // Create table model
        String[] columns = {"Bill No", "Customer", "Amount", "Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        
        add(new JScrollPane(table), BorderLayout.CENTER);
        refresh();

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    int billId = (int) bills.get(row).getId(); // Assuming Bill has getId()
                    showBillItems(billId);
                }
            }
        });

    }

    private void showBillItems(int billId) {
        List<BillItem> items = billDAO.getBillItemsByBillId(billId); // implement this in BillDAO
        itemsTableModel.setRowCount(0);
        for (BillItem item : items) {
            itemsTableModel.addRow(new Object[]{item.getBookName(), item.getQuantity()});
        }
    
        // show in a dialog
        JOptionPane.showMessageDialog(this, new JScrollPane(itemsTable), 
            "Bill Details", JOptionPane.INFORMATION_MESSAGE);
    }


    public void refresh() {
    tableModel.setRowCount(0);
    List<Bill> bills = billDAO.getAllBills();
    bills = billDAO.getAllBills(); // store for later use

    for (Bill b : bills) {
        tableModel.addRow(new Object[]{
            b.getBillNumber(),
            b.getCustomerName(),
            "₹" + b.getFinalAmount(),
            b.getDate(),
            b.getPaymentStatus()
        });
    }
}


}