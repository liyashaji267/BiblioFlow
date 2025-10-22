import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ManualBillEntryPanel extends JPanel {

    private JTable billTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JTextField quantityField;
    private JComboBox<String> searchTypeCombo;
    private JLabel totalLabel;
    private JTextField customerNameField;
    private BookDAO bookDAO;
    private BillDAO billDAO;

    private double currentTotal = 0.0;

    public ManualBillEntryPanel(BookDAO bookDAO, BillDAO billDAO) {
        this.bookDAO = bookDAO;
        this.billDAO = billDAO;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(250, 240, 230)); // cream background

        // Header
        JLabel headerLabel = new JLabel("Manual Bill Entry", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Georgia", Font.BOLD, 26));
        headerLabel.setForeground(new Color(150, 90, 60)); // warm brown
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        add(headerLabel, BorderLayout.NORTH);

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setOpaque(false);

        // Search panel
        JPanel searchPanel = createSearchPanel();
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // Billing table
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Customer details and controls
        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(150, 90, 60), 1),
                "Add Items to Bill", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Georgia", Font.BOLD, 14), new Color(150, 90, 60)
        ));

        JLabel searchTypeLabel = new JLabel("Search By:");
        searchTypeLabel.setFont(new Font("Georgia", Font.PLAIN, 14));
        searchTypeLabel.setForeground(new Color(80, 50, 40));
        panel.add(searchTypeLabel);

        searchTypeCombo = new JComboBox<>(new String[]{"ISBN", "Title"});
        searchTypeCombo.setFont(new Font("Georgia", Font.PLAIN, 14));
        panel.add(searchTypeCombo);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Georgia", Font.PLAIN, 14));
        searchLabel.setForeground(new Color(80, 50, 40));
        panel.add(searchLabel);

        searchField = new JTextField(20);
        searchField.setFont(new Font("Georgia", Font.PLAIN, 14));
        panel.add(searchField);

        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setFont(new Font("Georgia", Font.PLAIN, 14));
        quantityLabel.setForeground(new Color(80, 50, 40));
        panel.add(quantityLabel);

        quantityField = new JTextField(5);
        quantityField.setFont(new Font("Georgia", Font.PLAIN, 14));
        panel.add(quantityField);

        JButton searchBtn = new JButton("Search & Add");
        searchBtn.setFont(new Font("Georgia", Font.BOLD, 14));
        searchBtn.setBackground(new Color(150, 90, 60));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        searchBtn.addActionListener(new SearchButtonListener());
        panel.add(searchBtn);

        searchField.addActionListener(new SearchButtonListener());
        quantityField.addActionListener(new SearchButtonListener());

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(150, 90, 60), 1),
                "Bill Items", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Georgia", Font.BOLD, 14), new Color(150, 90, 60)
        ));

        String[] columns = {"ISBN", "Title", "Author", "Price", "Quantity", "Total"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Quantity editable
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 3 || columnIndex == 5 ? Double.class : String.class;
            }
        };

        billTable = new JTable(tableModel);
        billTable.setFont(new Font("Georgia", Font.PLAIN, 14));
        billTable.setRowHeight(28);
        billTable.setBackground(new Color(255, 250, 245));
        billTable.setForeground(new Color(80, 50, 40));
        billTable.setGridColor(new Color(200, 180, 160));
        billTable.setSelectionBackground(new Color(150, 90, 60));
        billTable.setSelectionForeground(Color.WHITE);
        billTable.setFillsViewportHeight(true);

        JTableHeader header = billTable.getTableHeader();
        header.setFont(new Font("Georgia", Font.BOLD, 14));
        header.setBackground(new Color(150, 90, 60));
        header.setForeground(Color.WHITE);

        tableModel.addTableModelListener(e -> {
            if (e.getColumn() == 4) {
                updateRowTotal(e.getFirstRow());
                calculateTotal();
            }
        });

        JScrollPane scrollPane = new JScrollPane(billTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        totalLabel = new JLabel("Total: ₹0.00", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Georgia", Font.BOLD, 16));
        totalLabel.setForeground(new Color(150, 90, 60));
        totalLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        panel.add(totalLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        // Customer panel
        JPanel customerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        customerPanel.setOpaque(false);
        customerPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(150, 90, 60), 1),
                "Customer Details", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Georgia", Font.BOLD, 14), new Color(150, 90, 60)
        ));

        JLabel customerLabel = new JLabel("Customer Name:");
        customerLabel.setFont(new Font("Georgia", Font.PLAIN, 14));
        customerLabel.setForeground(new Color(80, 50, 40));
        customerPanel.add(customerLabel);

        customerNameField = new JTextField(15);
        customerNameField.setFont(new Font("Georgia", Font.PLAIN, 14));
        customerPanel.add(customerNameField);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setOpaque(false);

        JButton removeBtn = new JButton("Remove Selected");
        removeBtn.setFont(new Font("Georgia", Font.BOLD, 14));
        removeBtn.setBackground(new Color(150, 90, 60));
        removeBtn.setForeground(Color.WHITE);
        removeBtn.addActionListener(e -> removeSelectedItem());

        JButton clearBtn = new JButton("Clear Bill");
        clearBtn.setFont(new Font("Georgia", Font.BOLD, 14));
        clearBtn.setBackground(new Color(180, 100, 60));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.addActionListener(e -> clearBill());

        JButton generateBtn = new JButton("Generate Bill");
        generateBtn.setFont(new Font("Georgia", Font.BOLD, 14));
        generateBtn.setBackground(new Color(120, 70, 40));
        generateBtn.setForeground(Color.WHITE);
        generateBtn.addActionListener(e -> generateBill());

        buttonPanel.add(removeBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(generateBtn);

        panel.add(customerPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private class SearchButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String searchText = searchField.getText().trim();
            String searchType = (String) searchTypeCombo.getSelectedItem();
            String quantityText = quantityField.getText().trim();

            if (searchText.isEmpty()) {
                JOptionPane.showMessageDialog(ManualBillEntryPanel.this, "Please enter " + searchType + "!", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (quantityText.isEmpty()) {
                JOptionPane.showMessageDialog(ManualBillEntryPanel.this, "Please enter quantity!", "Quantity Required", JOptionPane.WARNING_MESSAGE);
                quantityField.requestFocus();
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(quantityText);
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(ManualBillEntryPanel.this, "Quantity must be > 0!", "Invalid Quantity", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(ManualBillEntryPanel.this, "Enter valid number for quantity!", "Invalid Quantity", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Book book = null;
            if ("ISBN".equals(searchType)) {
                book = bookDAO.findBookByISBN(searchText);
            } else {
                List<Book> books = bookDAO.searchBooks(searchText, "title");
                if (!books.isEmpty()) book = books.get(0);
            }

            if (book == null) {
                JOptionPane.showMessageDialog(ManualBillEntryPanel.this, "Book not found!", "Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (book.getStockQuantity() < quantity) {
                JOptionPane.showMessageDialog(ManualBillEntryPanel.this, "Insufficient stock! Available: " + book.getStockQuantity(), "Stock Issue", JOptionPane.WARNING_MESSAGE);
                return;
            }

            addBookToBill(book, quantity);
            searchField.setText("");
            quantityField.setText("");
            searchField.requestFocus();
        }
    }

    private void addBookToBill(Book book, int quantity) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String isbnInTable = (String) tableModel.getValueAt(i, 0);
            if (isbnInTable.equals(book.getIsbn())) {
                int currentQty = Integer.parseInt(tableModel.getValueAt(i, 4).toString());
                int newQty = currentQty + quantity;
                tableModel.setValueAt(newQty, i, 4);
                updateRowTotal(i);
                calculateTotal();
                return;
            }
        }

        double price = book.getPrice();
        double rowTotal = price * quantity;
        Object[] rowData = {book.getIsbn(), book.getTitle(), book.getAuthor(), price, quantity, rowTotal};
        tableModel.addRow(rowData);
        calculateTotal();
    }

    private void updateRowTotal(int row) {
        try {
            double price = (Double) tableModel.getValueAt(row, 3);
            int quantity = Integer.parseInt(tableModel.getValueAt(row, 4).toString());
            double rowTotal = price * quantity;
            tableModel.setValueAt(rowTotal, row, 5);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Please enter valid quantity!", "Invalid Input", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void calculateTotal() {
        currentTotal = 0.0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            double rowTotal = (Double) tableModel.getValueAt(i, 5);
            currentTotal += rowTotal;
        }
        totalLabel.setText(String.format("Total: ₹%.2f", currentTotal));
    }

    private void removeSelectedItem() {
        int selectedRow = billTable.getSelectedRow();
        if (selectedRow >= 0) {
            tableModel.removeRow(selectedRow);
            calculateTotal();
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to remove!", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearBill() {
        int confirm = JOptionPane.showConfirmDialog(this, "Clear the entire bill?", "Confirm Clear", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.setRowCount(0);
            customerNameField.setText("");
            currentTotal = 0.0;
            totalLabel.setText("Total: ₹0.00");
        }
    }

    private void generateBill() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Bill is empty! Add items first.", "Empty Bill", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String customerName = customerNameField.getText().trim();
        if (customerName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter customer name!", "Customer Details Required", JOptionPane.WARNING_MESSAGE);
            customerNameField.requestFocus();
            return;
        }

        List<Book> billBooks = new java.util.ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String isbn = (String) tableModel.getValueAt(i, 0);
            int quantity = Integer.parseInt(tableModel.getValueAt(i, 4).toString());
            Book book = bookDAO.findBookByISBN(isbn);
            if (book != null) {
                book.setCartQuantity(quantity);
                billBooks.add(book);
            }
        }

        PaymentOpt payment = new PaymentOpt(currentTotal, () -> {
            boolean success = billDAO.createBill(billBooks, currentTotal, customerName, "", 1);
            if (success) {
                JOptionPane.showMessageDialog(this, "Bill generated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearBill();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to generate bill!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        payment.setVisible(true);
    }

    public void clearBillData() {
        clearBill();
    }
}
