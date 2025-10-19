import javax.swing.*;
import java.awt.Color;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.Component;
import java.awt.Container;

import com.mysql.cj.x.protobuf.MysqlxNotice.Frame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import java.awt.BorderLayout;
import java.awt.CardLayout;

public class MainApplication extends JFrame {
    private User currentUser;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private List<Book> cartItems;
    private double cartTotal;
    
    // Database DAOs
    private BookDAO bookDAO;
    private BillDAO billDAO;
    private OrderDAO orderDAO;

    
    // Reference to cart components for updating
    private JTextArea cartArea;
    private JLabel totalLabel;

    private boolean darkMode;
    private JButton someButton;
    private JPanel substoreBooksPanel;
    private SubstoreCartPanel substorePanel;
    private StoreReports storeReportsPanel;
    
    private static final java.awt.Color TABLE_BG = java.awt.Color.WHITE;
    private static final java.awt.Color TABLE_FG = java.awt.Color.BLACK;
    private static final java.awt.Color TABLE_GRID = java.awt.Color.LIGHT_GRAY;
    private static final java.awt.Color SELECTION_BG = new java.awt.Color(230, 200, 180);
    private static final java.awt.Color SELECTION_FG = java.awt.Color.WHITE;
    private static final java.awt.Color HEADER_BG = new java.awt.Color();
    private static final java.awt.Color HEADER_FG = java.awt.Color.WHITE;

    private DefaultListModel<String> historyListModel = new DefaultListModel<>();
    private JList<String> historyList = new JList<>(historyListModel);
    private JTextArea historyDetails = new JTextArea();

    public MainApplication(User currentUser) {
        this.currentUser = currentUser;
        this.cartItems = new ArrayList<>();
        this.cartTotal = 0.0;
        this.bookDAO = new BookDAO();
        this.billDAO = new BillDAO();
        this.orderDAO = new OrderDAO();
        this.substorePanel = new SubstoreCartPanel();
        
        setTitle("BiblioFlow - Main Application");
        setSize(1000, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        initUI();
    }


    private void initUI() {
    cardLayout = new CardLayout();
    mainPanel = new JPanel(cardLayout);
    
    // Create different panels
    JPanel dashboardPanel = createDashboardPanel();
    JPanel inventoryPanel = createInventoryPanel();
    JPanel billingPanel = createBillingPanel();
    substorePanel = new SubstoreCartPanel();
    JPanel storeReports = new StoreReports();
    
    // Create the new history panels
    JPanel billingHistoryPanel = createBillingHistoryPanel();
    JPanel transactionHistoryPanel = createTransactionHistoryPanel();
     
    mainPanel.add(dashboardPanel, "Dashboard");
    mainPanel.add(inventoryPanel, "Inventory");
    mainPanel.add(billingPanel, "Billing");
    mainPanel.add(billingHistoryPanel, "BillingHistory");
    mainPanel.add(transactionHistoryPanel, "TransactionHistory");
    mainPanel.add(substorePanel, "Substore");
    mainPanel.add(storeReports, "Reports");

    // Create menu bar
    JMenuBar menuBar = createMenuBar();
    setJMenuBar(menuBar);
        
        add(mainPanel);
        // ===== Dark Mode Toggle =====
        DarkLightSwitch darkLightSwitch = new DarkLightSwitch(mainPanel);

        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        topRightPanel.setOpaque(false);
        topRightPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        topRightPanel.add(new JLabel("Dark Mode: "));
        topRightPanel.add(darkLightSwitch.getTogglePanel());

        // Container panel for toggle + mainPanel
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.add(topRightPanel, BorderLayout.NORTH);
        containerPanel.add(mainPanel, BorderLayout.CENTER);

        setContentPane(containerPanel);

    }
    

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(70, 130, 180));
        menuBar.setForeground(Color.WHITE);
        
        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(Color.WHITE);
        
        JMenuItem dashboardItem = new JMenuItem("Dashboard");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenuItem exitItem = new JMenuItem("Exit");
        
        dashboardItem.addActionListener(_ -> cardLayout.show(mainPanel, "Dashboard"));
        logoutItem.addActionListener(_ -> logout());
        exitItem.addActionListener(_ -> System.exit(0));
        
        fileMenu.add(dashboardItem);
        fileMenu.addSeparator();
        fileMenu.add(logoutItem);
        fileMenu.add(exitItem);
        
        // Operations Menu
        JMenu operationsMenu = new JMenu("Operations");
        operationsMenu.setForeground(Color.WHITE);
    
        JMenuItem inventoryItem = new JMenuItem("Inventory Management");
        JMenuItem billingItem = new JMenuItem("Billing");
        JMenuItem billingHistoryItem = new JMenuItem("Billing History");
        JMenuItem transactionHistoryItem = new JMenuItem("Transaction History");
        JMenuItem substoreItem = new JMenuItem("Substore Lookup");
        JMenuItem reportsItem = new JMenuItem("Reports");

        // Add action listeners
        inventoryItem.addActionListener(e -> {
            refreshInventory();
            cardLayout.show(mainPanel, "Inventory");
        });
        billingItem.addActionListener(_ -> cardLayout.show(mainPanel, "Billing"));
        billingHistoryItem.addActionListener(_ -> cardLayout.show(mainPanel, "BillingHistory"));
        transactionHistoryItem.addActionListener(_ -> cardLayout.show(mainPanel, "TransactionHistory"));
        substoreItem.addActionListener(_ -> {
            refreshSubstore();
            cardLayout.show(mainPanel, "Substore");
        });
        reportsItem.addActionListener(e -> cardLayout.show(mainPanel, "Reports"));
    
        // Add items to menu
        operationsMenu.add(inventoryItem);
        operationsMenu.add(billingItem);
        operationsMenu.add(billingHistoryItem);
        operationsMenu.add(transactionHistoryItem);
        operationsMenu.add(substoreItem);
        operationsMenu.add(reportsItem);
    
        menuBar.add(fileMenu);
        menuBar.add(operationsMenu);
        
        // User info label
        JLabel userLabel = new JLabel("Welcome, " + currentUser.getUsername());
        userLabel.setForeground(Color.WHITE);
        userLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(userLabel);
        

        // Inside initUI() or constructor, after adding mainPanel
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        topRightPanel.setOpaque(false); // transparent so it blends with window
        topRightPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        // Create the WhatsApp-style toggle switch
        JPanel toggleSwitch = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int width = 50;
                int height = 25;
                g.setColor(darkMode ? Color.GREEN : Color.LIGHT_GRAY);
                g.fillRoundRect(0, 0, width, height, height, height);
                g.setColor(Color.WHITE);
                int knobX = darkMode ? width - height : 0;
                g.fillOval(knobX, 0, height, height);
            }
        };
        toggleSwitch.setPreferredSize(new Dimension(50, 25));
        toggleSwitch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Optional label next to switch
        JLabel switchLabel = new JLabel("Dark Mode");
        topRightPanel.add(switchLabel);
        topRightPanel.add(toggleSwitch);

        // Place it at the top-right corner above mainPanel
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.add(topRightPanel, BorderLayout.NORTH);
        containerPanel.add(mainPanel, BorderLayout.CENTER);

        // Replace mainPanel in the frame with containerPanel
        add(containerPanel);

        return menuBar;
    }


    
    private JPanel createDashboardPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(Color.WHITE);
    
    // Header
    JLabel header = new JLabel("BiblioFlow Dashboard", SwingConstants.CENTER);
    header.setFont(new Font("Serif", Font.BOLD, 36));
    header.setForeground(new Color(70, 130, 180));
    header.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
    panel.add(header, BorderLayout.NORTH);
    
    // Quick actions panel - UPDATED to 3x2 grid
    JPanel actionsPanel = new JPanel(new GridLayout(3, 2, 20, 20));
    actionsPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
    actionsPanel.setBackground(Color.WHITE);
    
    // Create quick action buttons
    JButton inventoryBtn = createDashboardButton("📚 Inventory Management", "Manage book stock", new Color(76, 175, 80));
    JButton billingBtn = createDashboardButton("💰 Billing", "Process sales", new Color(33, 150, 243));
    JButton billingHistoryBtn = createDashboardButton("📋 Billing History", "View past bills", new Color(156, 39, 176));
    JButton transactionHistoryBtn = createDashboardButton("💳 Transaction History", "View all transactions", new Color(255, 152, 0));
    JButton substoreBtn = createDashboardButton("🏪 Substore", "Check other stores", new Color(156, 39, 176));
    JButton reportsBtn = createDashboardButton("📊 Reports", "View sales & profits", new Color(255, 152, 0));

    // Add action listeners for new buttons
    billingHistoryBtn.addActionListener(_ -> cardLayout.show(mainPanel, "BillingHistory"));
    transactionHistoryBtn.addActionListener(_ -> cardLayout.show(mainPanel, "TransactionHistory"));
    reportsBtn.addActionListener(e -> cardLayout.show(mainPanel, "Reports"));
    
    inventoryBtn.addActionListener(_ -> {
        refreshInventory();
        cardLayout.show(mainPanel, "Inventory");
    });
    billingBtn.addActionListener(_ -> cardLayout.show(mainPanel, "Billing"));
    substoreBtn.addActionListener(_ -> {
        refreshSubstore();
        cardLayout.show(mainPanel, "Substore");
    });
    
    // Add all buttons to panel
    actionsPanel.add(inventoryBtn);
    actionsPanel.add(billingBtn);
    actionsPanel.add(billingHistoryBtn);
    actionsPanel.add(transactionHistoryBtn);
    actionsPanel.add(substoreBtn);
    actionsPanel.add(reportsBtn);
    
    panel.add(actionsPanel, BorderLayout.CENTER);
    
    return panel;
    }

    

    
    // The rest of your existing methods remain the same...
    private JButton createDashboardButton(String title, String subtitle, Color color) {
        JButton button = new JButton("<html><center><b>" + title + "</b><br><small>" + subtitle + "</small></center></html>");
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(200, 100));
        button.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        return button;
    }
    
private JPanel createInventoryPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    
    // Create tabbed pane for Inventory View and Search
    JTabbedPane tabbedPane = new JTabbedPane();
    
    // Inventory View Tab
    JPanel inventoryViewPanel = createInventoryViewPanel();
    tabbedPane.addTab("Inventory View", inventoryViewPanel);
    
    // Search Tab
    SearchPanel searchPanel = new SearchPanel(bookDAO);
    tabbedPane.addTab("Search Books", searchPanel);
    
    panel.add(tabbedPane, BorderLayout.CENTER);
    
    return panel;
}

private JPanel createInventoryViewPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    
    // Header
    JPanel headerPanel = createInventoryHeader();
    
    // Book grid
    JPanel booksPanel = new JPanel(new WrapLayout());
    booksPanel.setBackground(Color.WHITE);
    JScrollPane scrollPane = new JScrollPane(booksPanel);
    scrollPane.setName("inventoryScrollPane"); // Name for identification
    
    panel.add(headerPanel, BorderLayout.NORTH);
    panel.add(scrollPane, BorderLayout.CENTER);
    
    // Load initial data
    refreshInventoryDisplay(booksPanel);
    
    return panel;
}

private JPanel createInventoryHeader() {
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(new Color(70, 130, 180));
    headerPanel.setPreferredSize(new Dimension(1400, 60));
    
    JLabel title = new JLabel("Inventory Management", SwingConstants.CENTER);
    title.setFont(new Font("Arial", Font.BOLD, 24));
    title.setForeground(Color.WHITE);
    
    // Control buttons panel
    JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    controlPanel.setOpaque(false);
    
    JButton addBookBtn = new JButton("Add New Book");
    addBookBtn.setBackground(new Color(76, 175, 80));
    addBookBtn.setForeground(Color.WHITE);
    addBookBtn.addActionListener(e -> showAddBookDialog());
    
    JButton refreshBtn = new JButton("Refresh");
    refreshBtn.setBackground(new Color(70, 130, 180));
    refreshBtn.setForeground(Color.WHITE);
    refreshBtn.addActionListener(e -> refreshInventory());
    
    controlPanel.add(refreshBtn);
    controlPanel.add(addBookBtn);
    
    headerPanel.add(title, BorderLayout.CENTER);
    headerPanel.add(controlPanel, BorderLayout.EAST);
    
    return headerPanel;
}

// Add these methods to your MainApplication class

private void showEditBookDialog(Book book) {
    // Create edit dialog
    JDialog editDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Edit Book", Dialog.ModalityType.APPLICATION_MODAL);
    editDialog.setLayout(new BorderLayout());
    editDialog.setSize(400, 500);
    editDialog.setLocationRelativeTo(this);
    
    JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
    formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    // Form fields
    JTextField titleField = new JTextField(book.getTitle());
    JTextField authorField = new JTextField(book.getAuthor());
    JTextField isbnField = new JTextField(book.getIsbn());
    JTextField publisherField = new JTextField(book.getPublisher());
    JTextField priceField = new JTextField(String.valueOf(book.getPrice()));
    JTextField stockField = new JTextField(String.valueOf(book.getStockQuantity()));
    
    formPanel.add(new JLabel("Title:"));
    formPanel.add(titleField);
    formPanel.add(new JLabel("Author:"));
    formPanel.add(authorField);
    formPanel.add(new JLabel("ISBN:"));
    formPanel.add(isbnField);
    formPanel.add(new JLabel("Publisher:"));
    formPanel.add(publisherField);
    formPanel.add(new JLabel("Price:"));
    formPanel.add(priceField);
    formPanel.add(new JLabel("Stock Quantity:"));
    formPanel.add(stockField);
    
    // Buttons
    JPanel buttonPanel = new JPanel(new FlowLayout());
    JButton saveBtn = new JButton("Save Changes");
    JButton cancelBtn = new JButton("Cancel");
    
    saveBtn.addActionListener(e -> {
        try {
            // Update book object
            book.setTitle(titleField.getText());
            book.setAuthor(authorField.getText());
            book.setIsbn(isbnField.getText());
            book.setPublisher(publisherField.getText());
            book.setPrice(Double.parseDouble(priceField.getText()));
            book.setStockQuantity(Integer.parseInt(stockField.getText()));
            
            // Update in database
            if (bookDAO.updateBook(book)) {
                JOptionPane.showMessageDialog(editDialog, "Book updated successfully!");
                editDialog.dispose();
                refreshInventory();
            } else {
                JOptionPane.showMessageDialog(editDialog, "Failed to update book!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(editDialog, "Please enter valid numbers for price and stock!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    });
    
    cancelBtn.addActionListener(e -> editDialog.dispose());
    
    buttonPanel.add(saveBtn);
    buttonPanel.add(cancelBtn);
    
    editDialog.add(formPanel, BorderLayout.CENTER);
    editDialog.add(buttonPanel, BorderLayout.SOUTH);
    editDialog.setVisible(true);
}

private void deleteBook(Book book) {
    int confirm = JOptionPane.showConfirmDialog(this, 
        "Are you sure you want to delete '" + book.getTitle() + "'?",
        "Confirm Delete", JOptionPane.YES_NO_OPTION);
    
    if (confirm == JOptionPane.YES_OPTION) {
        if (bookDAO.deleteBook(book.getIsbn())) {
            JOptionPane.showMessageDialog(this, "Book deleted successfully!");
            refreshInventory();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete book!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

private void refreshInventory() {
    // Refresh both inventory view and search panel if active
    Component currentPanel = mainPanel.getComponent(1); // Assuming inventory is at index 1
    
    if (currentPanel instanceof JPanel) {
        JTabbedPane tabbedPane = findTabbedPane((JPanel) currentPanel);
        if (tabbedPane != null) {
            int selectedIndex = tabbedPane.getSelectedIndex();
            Component selectedComponent = tabbedPane.getComponentAt(selectedIndex);
            
            if (selectedComponent instanceof JPanel) {
                if (selectedIndex == 0) { // Inventory View tab
                    refreshInventoryView((JPanel) selectedComponent);
                } else if (selectedIndex == 1) { // Search tab
                    refreshSearchPanel((JPanel) selectedComponent);
                }
            }
        }
    }
}

private void refreshInventoryView(JPanel inventoryViewPanel) {
    // Find the books panel within the inventory view
    for (Component comp : inventoryViewPanel.getComponents()) {
        if (comp instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) comp;
            if ("inventoryScrollPane".equals(scrollPane.getName())) {
                JPanel bookContainer = (JPanel) scrollPane.getViewport().getView();
                refreshInventoryDisplay(bookContainer);
                break;
            }
        }
    }
}

private void refreshSearchPanel(JPanel searchPanelContainer) {
    // Find the SearchPanel component and refresh it
    for (Component comp : searchPanelContainer.getComponents()) {
        if (comp instanceof SearchPanel) {
            SearchPanel searchPanel = (SearchPanel) comp;
            searchPanel.refreshSearch();
            break;
        }
    }
}

private void refreshInventoryDisplay(JPanel bookContainer) {
    bookContainer.removeAll();
    
    // Fetch books from DB
    List<Book> books = bookDAO.getAllBooks();
    
    if (books.isEmpty()) {
        JLabel noBooksLabel = new JLabel("No books found in inventory.", SwingConstants.CENTER);
        noBooksLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        bookContainer.add(noBooksLabel);
    } else {
        for (Book book : books) {
            JPanel card = createBookCard(book);
            bookContainer.add(card);
        }
    }
    
    bookContainer.revalidate();
    bookContainer.repaint();
}

private JPanel createBookCard(Book book) {
    JPanel card = new JPanel(new BorderLayout());
    card.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.GRAY, 1),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));
    card.setPreferredSize(new Dimension(280, 150));
    card.setBackground(Color.WHITE);
    
    // Book info
    JLabel title = new JLabel("<html><b>" + book.getTitle() + "</b></html>");
    title.setFont(new Font("Arial", Font.BOLD, 14));
    
    JLabel author = new JLabel("By: " + book.getAuthor());
    JLabel price = new JLabel("₹" + book.getPrice());
    JLabel stock = new JLabel("Stock: " + book.getStockQuantity());
    JLabel isbn = new JLabel("ISBN: " + book.getIsbn());
    
    JPanel infoPanel = new JPanel(new GridLayout(5, 1, 5, 5));
    infoPanel.add(title);
    infoPanel.add(author);
    infoPanel.add(price);
    infoPanel.add(stock);
    infoPanel.add(isbn);
    
    // Action buttons
    JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 0));
    
    JButton editBtn = new JButton("Edit");
    editBtn.setBackground(new Color(255, 193, 7));
    editBtn.addActionListener(e -> showEditBookDialog(book));
    
    JButton deleteBtn = new JButton("Delete");
    deleteBtn.setBackground(new Color(220, 53, 69));
    deleteBtn.setForeground(Color.WHITE);
    deleteBtn.addActionListener(e -> deleteBook(book));
    
    buttonPanel.add(editBtn);
    buttonPanel.add(deleteBtn);
    
    card.add(infoPanel, BorderLayout.CENTER);
    card.add(buttonPanel, BorderLayout.SOUTH);
    
    return card;
}

// Helper method to find tabbed pane in the panel hierarchy
private JTabbedPane findTabbedPane(Container container) {
    for (Component comp : container.getComponents()) {
        if (comp instanceof JTabbedPane) {
            return (JTabbedPane) comp;
        } else if (comp instanceof Container) {
            JTabbedPane found = findTabbedPane((Container) comp);
            if (found != null) {
                return found;
            }
        }
    }
    return null;
}
    
    private JPanel createBillingPanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));

    // --- Header ---
    JLabel title = new JLabel("Billing System", SwingConstants.CENTER);
    title.setFont(new Font("Arial", Font.BOLD, 24));
    panel.add(title, BorderLayout.NORTH);

    // --- Billing table ---
    String[] columns = {"ISBN", "Title", "Price", "Qty", "Total"};
    DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
    JTable billingTable = new JTable(tableModel);
    JScrollPane tableScroll = new JScrollPane(billingTable);
    panel.add(tableScroll, BorderLayout.CENTER);

    // Use the color constants (fixed)
    billingTable.setBackground(TABLE_BG);
    billingTable.setForeground(TABLE_FG);
    billingTable.setGridColor(TABLE_GRID);
    billingTable.setSelectionBackground(SELECTION_BG);
    billingTable.setSelectionForeground(SELECTION_FG);
    billingTable.setFillsViewportHeight(true); // fills empty space

    // Update table header colors
    JTableHeader header = billingTable.getTableHeader();
    header.setBackground(HEADER_BG);
    header.setForeground(HEADER_FG);

    // --- Log area ---
    JTextArea logArea = new JTextArea(5, 30);
    logArea.setEditable(false);
    JScrollPane logScroll = new JScrollPane(logArea);

    // --- Total label ---
    JLabel totalLabel = new JLabel("Total: ₹0.00");
    totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
    totalLabel.setForeground(Color.RED);

    // --- ScanBarcodePanel ---
    ScanBarcodePanel scanPanel = new ScanBarcodePanel(billingTable, logArea, totalLabel);
    scanPanel.setPreferredSize(new Dimension(400, 300));

    JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
    rightPanel.add(scanPanel, BorderLayout.CENTER);
    rightPanel.add(logScroll, BorderLayout.SOUTH);

    panel.add(rightPanel, BorderLayout.EAST);

    // --- Control panel (checkout / clear cart) ---
    JPanel controlPanel = new JPanel(new FlowLayout());

    JButton checkoutBtn = new JButton("Proceed to Payment");
    checkoutBtn.setBackground(new Color(76, 175, 80));
    checkoutBtn.setForeground(Color.WHITE);
    checkoutBtn.addActionListener(e -> proceedToPaymentFromTable(tableModel));

    JButton clearBtn = new JButton("Clear Cart");
    clearBtn.setBackground(Color.RED);
    clearBtn.setForeground(Color.WHITE);
    clearBtn.addActionListener(e -> {
        tableModel.setRowCount(0);
        totalLabel.setText("Total: ₹0.00");
        logArea.setText("");
    });

    JButton removeBtn = new JButton("Remove");
    removeBtn.setBackground(Color.ORANGE);
    removeBtn.setForeground(Color.BLACK);

removeBtn.addActionListener(e -> {
    int selectedRow = billingTable.getSelectedRow();
    if (selectedRow >= 0) {
        int qty = Integer.parseInt(tableModel.getValueAt(selectedRow, 3).toString()); // Qty column
        double price = Double.parseDouble(tableModel.getValueAt(selectedRow, 2).toString()); // Price column

        if (qty > 1) {
            qty--; // reduce quantity by 1
            tableModel.setValueAt(qty, selectedRow, 3);          // Update Qty
            tableModel.setValueAt(price * qty, selectedRow, 4);  // Update Total
        } else {
            tableModel.removeRow(selectedRow); // remove row if qty == 1
        }

        // Recalculate total
        double total = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            double rowTotal = Double.parseDouble(tableModel.getValueAt(i, 4).toString());
            total += rowTotal;
        }
        totalLabel.setText(String.format("Total: ₹%.2f", total));
    } else {
        JOptionPane.showMessageDialog(panel, "Select a row to remove", "Warning", JOptionPane.WARNING_MESSAGE);
    }
});


    
    controlPanel.add(totalLabel);
    controlPanel.add(checkoutBtn);
    controlPanel.add(clearBtn);
    controlPanel.add(removeBtn);


    panel.add(controlPanel, BorderLayout.SOUTH);

    return panel;
}

private void loadBillHistory() {
        java.util.List<Bill> bills = billDAO.getAllBills();
        historyListModel.clear();
        for (Bill b : bills) {
            historyListModel.addElement(b.getBillNumber());
        }
    }

    private void showBillDetails(String billNumber) {
        Bill bill = billDAO.getBillByNumber(billNumber);
        if (bill == null) return;

        java.util.List<BillItem> items = billDAO.getBillItemsByBillId(bill.getId());
        StringBuilder sb = new StringBuilder();
        sb.append("Customer: ").append(bill.getCustomerName()).append("\n");
        sb.append("Date: ").append(bill.getDate()).append("\n");
        sb.append("Final Amount: ₹").append(bill.getFinalAmount()).append("\n\n");
        sb.append("Items:\n");

        for (BillItem item : items) {
            sb.append(item.getBookName())
              .append(" - Qty: ").append(item.getQuantity())
              .append(" - ₹").append(item.getPrice()).append("\n");
        }

        historyDetails.setText(sb.toString());
    }

private void proceedToPaymentFromTable(DefaultTableModel tableModel) {
    if (tableModel.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "Cart is empty!", "Warning", JOptionPane.WARNING_MESSAGE);
        return;
    }

    double total = 0.0;
    for (int i = 0; i < tableModel.getRowCount(); i++) {
        total += (double) tableModel.getValueAt(i, 4);
    }

    PaymentOpt payment = new PaymentOpt(total, () -> {
        // Add logic to create a bill in DB if needed
        JOptionPane.showMessageDialog(this, "Payment completed!");
        tableModel.setRowCount(0);
    });

    payment.setVisible(true);
}

private JPanel createBillingHistoryPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    
    // Header
    JLabel header = new JLabel("Billing History", SwingConstants.CENTER);
    header.setFont(new Font("Arial", Font.BOLD, 24));
    header.setForeground(new Color(70, 130, 180));
    header.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
    panel.add(header, BorderLayout.NORTH);
    
    // Create and add the BillHistoryPanel
    BillHistoryPanel billHistoryPanel = new BillHistoryPanel();
    panel.add(billHistoryPanel, BorderLayout.CENTER);
    
    // Add refresh button
    JButton refreshBtn = new JButton("Refresh");
    refreshBtn.addActionListener(e -> billHistoryPanel.refresh());
    
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(refreshBtn);
    panel.add(buttonPanel, BorderLayout.SOUTH);
    
    return panel;
}

private JPanel createTransactionHistoryPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    
    // Header
    JLabel header = new JLabel("Transaction History", SwingConstants.CENTER);
    header.setFont(new Font("Arial", Font.BOLD, 24));
    header.setForeground(new Color(70, 130, 180));
    header.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
    panel.add(header, BorderLayout.NORTH);
    
    // Create and add the TransactionHistoryPanel
    TransactionHistoryPanel transactionPanel = new TransactionHistoryPanel();
    panel.add(transactionPanel, BorderLayout.CENTER);
    
    return panel;
}
    
private JPanel createSubstorePanel() {
    JPanel panel = new JPanel(new BorderLayout());
    
    // Header with title and search controls
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(new Color(70, 130, 180));
    headerPanel.setPreferredSize(new Dimension(1400, 80));
    
    JLabel title = new JLabel("Substore Book Lookup", SwingConstants.CENTER);
    title.setFont(new Font("Arial", Font.BOLD, 24));
    title.setForeground(Color.WHITE);
    
    // Search controls panel
    JPanel searchControls = new JPanel(new FlowLayout());
    searchControls.setOpaque(false);
    
    JTextField searchField = new JTextField(20);
    JComboBox<String> searchType = new JComboBox<>(new String[]{"Title", "Author", "Publisher", "All Fields"});
    JButton searchBtn = new JButton("Search");
    JButton clearBtn = new JButton("Clear");
    
    searchBtn.addActionListener(e -> performSubstoreSearch(searchField.getText(), (String) searchType.getSelectedItem()));
    clearBtn.addActionListener(e -> {
        searchField.setText("");
        refreshSubstoreDisplay();
    });
    
    searchControls.add(new JLabel("Search:"));
    searchControls.add(searchField);
    searchControls.add(new JLabel("By:"));
    searchControls.add(searchType);
    searchControls.add(searchBtn);
    searchControls.add(clearBtn);
    
    headerPanel.add(title, BorderLayout.CENTER);
    headerPanel.add(searchControls, BorderLayout.SOUTH);
    
    // Content panel with scroll pane
    JPanel contentPanel = new JPanel(new BorderLayout());
    JPanel booksPanel = new JPanel(new WrapLayout());
    booksPanel.setBackground(Color.WHITE);
    JScrollPane scrollPane = new JScrollPane(booksPanel);
    scrollPane.setName("substoreScrollPane");
    
    contentPanel.add(scrollPane, BorderLayout.CENTER);
    
    panel.add(headerPanel, BorderLayout.NORTH);
    panel.add(contentPanel, BorderLayout.CENTER);
    
    // Store reference to books panel for searching
    this.substoreBooksPanel = booksPanel;
    
    // Load initial data
    refreshSubstoreDisplay();
    
    return panel;
}

private void performSubstoreSearch(String query, String searchType) {
    if (query == null || query.trim().isEmpty()) {
        refreshSubstoreDisplay();
        return;
    }
    
    List<Book> allBooks = bookDAO.getBooksFromSubstores();
    List<Book> filteredBooks = new ArrayList<>();
    
    String searchQuery = query.toLowerCase().trim();
    
    for (Book book : allBooks) {
        if (matchesSearchCriteria(book, searchQuery, searchType)) {
            filteredBooks.add(book);
        }
    }
    
    displaySubstoreBooks(filteredBooks);
}

private boolean matchesSearchCriteria(Book book, String query, String fieldType) {
    switch (fieldType) {
        case "Title":
            return book.getTitle().toLowerCase().contains(query);
        case "Author":
            return book.getAuthor().toLowerCase().contains(query);
        case "Publisher":
            return book.getPublisher().toLowerCase().contains(query);
        case "All Fields":
            return book.getTitle().toLowerCase().contains(query) ||
                   book.getAuthor().toLowerCase().contains(query) ||
                   book.getPublisher().toLowerCase().contains(query);
        default:
            return false;
    }
}

private void refreshSubstoreDisplay() {
    if (substoreBooksPanel == null) return;
    
    substoreBooksPanel.removeAll();
    
    List<Book> books = bookDAO.getBooksFromSubstores();
    
    displaySubstoreBooks(books);
}

private void displaySubstoreBooks(List<Book> books) {
    if (substoreBooksPanel == null) return;
    
    substoreBooksPanel.removeAll();
    
    if (books.isEmpty()) {
        JLabel noBooksLabel = new JLabel("No books found.", SwingConstants.CENTER);
        noBooksLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        substoreBooksPanel.add(noBooksLabel);
    } else {
        for (Book book : books) {
            JPanel card = createSubstoreBookCard(book);
            substoreBooksPanel.add(card);
        }
    }
    
    substoreBooksPanel.revalidate();
    substoreBooksPanel.repaint();
}

private void refreshSubstore() {
    refreshSubstoreDisplay();
}

private void substoreCart(Book book, int quantity) {
    JOptionPane.showMessageDialog(this, 
        "Please add items directly in the Substore panel using the 'Add' buttons.");
    cardLayout.show(mainPanel, "Substore");
}

private JPanel createSubstoreBookCard(Book book) {
    JPanel card = new JPanel(new BorderLayout());
    card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
    card.setPreferredSize(new Dimension(250, 120));
    
    JLabel title = new JLabel("<html><b>" + book.getTitle() + "</b></html>");
    JLabel store = new JLabel("Store: " + book.getLocation());
    JLabel qty = new JLabel("Qty: " + book.getStockQuantity());
    JLabel price = new JLabel("Price: ₹" + book.getPrice());
    
    JPanel info = new JPanel(new GridLayout(4, 1));
    info.add(title);
    info.add(store);
    info.add(qty);
    info.add(price);
    
    JButton addBtn = new JButton("Add to Cart");
    addBtn.addActionListener(e -> addToCart(book));
    
    card.add(info, BorderLayout.CENTER);
    card.add(addBtn, BorderLayout.SOUTH);
    
    return card;
}
    
    private void simulateBarcodeScan() {
        String isbn = JOptionPane.showInputDialog(this, "Enter ISBN or scan barcode:");
        if (isbn != null && !isbn.trim().isEmpty()) {
            Book book = bookDAO.findBookByISBN(isbn);
            if (book != null) {
                addToCart(book);
            } else {
                JOptionPane.showMessageDialog(this, "Book not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    
    private void addToCart(Book book) {
        cartItems.add(book);
        cartTotal += book.getPrice();
        updateCartDisplay();
    }
    
    private void updateCartDisplay() {
        if (cartArea == null || totalLabel == null) return;
        
        StringBuilder cartText = new StringBuilder();
        cartText.append("Cart Items:\n");
        cartText.append("----------------------------------------\n");
        
        for (Book book : cartItems) {
            cartText.append(String.format("%-30s ₹%.2f\n", 
                book.getTitle().length() > 30 ? book.getTitle().substring(0, 27) + "..." : book.getTitle(),
                book.getPrice()));
        }
        
        cartText.append("----------------------------------------\n");
        cartText.append(String.format("Total: ₹%.2f", cartTotal));
        
        cartArea.setText(cartText.toString());
        totalLabel.setText(String.format("Total: ₹%.2f", cartTotal));
    }
    
    private void clearCart() {
        cartItems.clear();
        cartTotal = 0.0;
        updateCartDisplay();
    }
    
    private void proceedToPayment(String customerName, String customerPhone) {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        PaymentOpt payment = new PaymentOpt(cartTotal, () -> {
            completeSale(customerName, customerPhone);
        }, customerName, customerPhone);
        
        payment.setVisible(true);
    }
    
    private void completeSale(String customerName, String customerPhone) {
        boolean success = billDAO.createBill(cartItems, cartTotal, customerName, customerPhone, currentUser.getId());
        if (success) {
            JOptionPane.showMessageDialog(this, "Sale completed successfully!");
            clearCart();
        } else {
            JOptionPane.showMessageDialog(this, "Error completing sale!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddBookDialog() {
        JDialog dialog = new JDialog(this, "Add New Book", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        String[] labels = {"ISBN:", "Title:", "Author:", "Publisher:", "Edition:", "Price:", "Quantity:", "Rack Number:", "Genre:"};
        JTextField[] fields = new JTextField[labels.length];
        
        for (int i = 0; i < labels.length; i++) {
            panel.add(new JLabel(labels[i]));
            fields[i] = new JTextField();
            panel.add(fields[i]);
        }
        
        JButton saveBtn = new JButton("Save Book");
        saveBtn.addActionListener(_ -> saveBookToDatabase(fields, dialog));
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(_ -> dialog.dispose());
        
        panel.add(saveBtn);
        panel.add(cancelBtn);
        
        dialog.add(new JScrollPane(panel));
        dialog.setVisible(true);
    }
    
    private void saveBookToDatabase(JTextField[] fields, JDialog dialog) {
        try {
            String isbn = fields[0].getText();
            String title = fields[1].getText();
            String author = fields[2].getText();
            String publisher = fields[3].getText();
            String edition = fields[4].getText();
            double price = Double.parseDouble(fields[5].getText());
            int quantity = Integer.parseInt(fields[6].getText());
            String rackNumber = fields[7].getText();
            String genre = fields[8].getText();
            
            Book book = new Book(0, isbn, title, author, publisher, edition, price, quantity, rackNumber, "Main Store", null, genre);
            if (bookDAO.addBook(book)) {
                JOptionPane.showMessageDialog(dialog, "Book added successfully!");
                dialog.dispose();
                refreshInventory();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(dialog, "Please enter valid numbers for price and quantity!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            frontpage frontPage = new frontpage();
            frontPage.setVisible(true);
            this.dispose();
        }
    }

    private void performSearch(String query, String searchType) {
        if (query == null || query.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String column;
            switch (searchType.toLowerCase()) {
                case "title": column = "title"; break;
                case "author": column = "author"; break;
                case "isbn": column = "isbn"; break;
                case "publisher": column = "publisher"; break;
                default: column = "title";
            }

            List<Book> books = bookDAO.searchBooks(query, column);
            updateSearchResultsUI(books);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error while searching: " + e.getMessage(),
                "Search Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSearchResultsUI(List<Book> books) {
        // Get the search panel (index 3 in mainPanel)
        JPanel searchPanel = (JPanel) mainPanel.getComponent(3);
        JScrollPane resultsScroll = null;
        
        // Find the scroll pane in search panel
        for (Component c : searchPanel.getComponents()) {
            if (c instanceof JScrollPane) {
                resultsScroll = (JScrollPane) c;
                break;
            }
        }
        
        if (resultsScroll == null) return;
        
        JPanel container = (JPanel) resultsScroll.getViewport().getView();
        container.removeAll();
        
        if (books.isEmpty()) {
            container.add(new JLabel("No books found for this search."));
        } else {
            for (Book b : books) {
                JPanel card = new JPanel(new BorderLayout());
                card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                card.setPreferredSize(new Dimension(250, 120));
                
                JLabel title = new JLabel("<html><b>" + b.getTitle() + "</b></html>");
                JLabel author = new JLabel("Author: " + b.getAuthor());
                JLabel category = new JLabel("Category: " + b.getGenre());
                JLabel price = new JLabel("Price: ₹" + b.getPrice());
                
                JPanel info = new JPanel(new GridLayout(4, 1));
                info.add(title);
                info.add(author);
                info.add(category);
                info.add(price);
                
                JButton addBtn = new JButton("Add to Cart");
                addBtn.addActionListener(_ -> addToCart(b));
                
                card.add(info, BorderLayout.CENTER);
                card.add(addBtn, BorderLayout.SOUTH);
                
                container.add(card);
            }
        }
        
        container.revalidate();
        container.repaint();
    }

    private void StoreReports() {
        cardLayout.show(mainPanel, "Reports");  
    }
}
