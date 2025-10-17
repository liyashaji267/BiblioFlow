import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.text.ParseException;

public class SalesReportApp extends JFrame {

    private JButton reportBtn;
    private BillDAO billDAO = new BillDAO();
    private java.util.List<Sale> salesList = new java.util.ArrayList<>();

    // Inner class for sales items
    class Sale {
        String bookName;
        int quantity;
        double price;
        java.util.Date date;

        public Sale(String bookName, int quantity, double price, java.util.Date date) {
            this.bookName = bookName;
            this.quantity = quantity;
            this.price = price;
            this.date = date;
        }
    }

    public SalesReportApp() {
        setTitle("Bookstore Sales Report (From Bill History)");
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        reportBtn = new JButton("Generate Report");
        add(reportBtn);

        reportBtn.addActionListener(e -> loadSalesFromDBAndGenerateReport());

        setVisible(true);
    }

    private void loadSalesFromDBAndGenerateReport() {
        salesList.clear();
        java.util.List<Bill> bills = billDAO.getAllBills();
        for (Bill bill : bills) {
            int billId = bill.getId();   
            java.util.List<BillItem> items = billDAO.getBillItemsByBillId(billId);
            for (BillItem item : items) {
                double unitPrice = bill.getFinalAmount() / items.size(); // crude approx
                try {
                    java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse(bill.getDate());
                    salesList.add(new Sale(item.getBookName(), item.getQuantity(), unitPrice, date));
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            }
        }

        generateReport();
    }

    private void generateReport() {
        String[] options = {"Daily", "Weekly", "Monthly"};
        int choice = JOptionPane.showOptionDialog(this,
                "Select report type:",
                "Report Type",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == -1) return;

        Calendar cal = Calendar.getInstance();
        double totalRevenue = 0;
        int totalBooks = 0;

        for (Sale sale : salesList) {
            cal.setTime(sale.date);
            Calendar now = Calendar.getInstance();
            boolean include = false;

            switch (choice) {
                case 0: include = sameDay(sale.date, now.getTime()); break;
                case 1: include = sameWeek(sale.date, now.getTime()); break;
                case 2: include = sameMonth(sale.date, now.getTime()); break;
            }

            if (include) {
                totalRevenue += sale.price * sale.quantity;
                totalBooks += sale.quantity;
            }
        }

        JOptionPane.showMessageDialog(this,
                "Report (" + options[choice] + "):\n" +
                        "Total Books Sold: " + totalBooks + "\n" +
                        "Total Revenue: ₹" + totalRevenue);
    }

    private boolean sameDay(java.util.Date d1, java.util.Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1); c2.setTime(d2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    private boolean sameWeek(java.util.Date d1, java.util.Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1); c2.setTime(d2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.WEEK_OF_YEAR) == c2.get(Calendar.WEEK_OF_YEAR);
    }

    private boolean sameMonth(java.util.Date d1, java.util.Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1); c2.setTime(d2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH);
    }

    public static void main(String[] args) {
        new SalesReportApp();
    }
}
