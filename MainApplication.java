import javax.swing.*;
import java.awt.Color;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.apache.poi.sl.usermodel.PaintStyle.GradientPaint;

import com.itextpdf.text.Image;

import java.awt.Component;
import java.awt.Container;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
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
    private ManualBillEntryPanel manualBillPanel;
    
    // Color constants at the top of MainApplication class
    private static final java.awt.Color TABLE_BG = new java.awt.Color(250, 240, 230);
    private static final java.awt.Color TABLE_FG = new java.awt.Color(80, 50, 40);
    private static final java.awt.Color TABLE_GRID = new java.awt.Color(200, 180, 160);
    private static final java.awt.Color SELECTION_BG = new java.awt.Color(150, 90, 60);
    private static final java.awt.Color SELECTION_FG = java.awt.Color.WHITE;
    private static final java.awt.Color HEADER_BG = new java.awt.Color(150, 90, 60);
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
        this.manualBillPanel = new ManualBillEntryPanel(bookDAO, billDAO);
        
        setTitle("BiblioFlow - Main Application");
        setSize(1000, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        initUI();
    }

    private void initUI() {
    // Set the frontpage-style gradient background - CORRECT VERSION
    setContentPane(new JPanel(new BorderLayout()) {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            // Use LinearGradientPaint instead
            float[] fractions = {0.0f, 1.0f};
            Color[] colors = {new Color(250, 240, 230), new Color(230, 200, 180)};
            LinearGradientPaint gp = new LinearGradientPaint(
                    0, 0, 0, getHeight(), fractions, colors
            );
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    });
    
    
    cardLayout = new CardLayout();
    mainPanel = new JPanel(cardLayout);
    mainPanel.setOpaque(false); // Make transparent to show background

    // Create different panels
    JPanel dashboardPanel = createDashboardPanel();
    JPanel inventoryPanel = createInventoryPanel();
    JPanel billingPanel = createBillingPanel();
    substorePanel = new SubstoreCartPanel();
    JPanel storeReports = new StoreReports();
    
    // Create the history panels
    JPanel billingHistoryPanel = createBillingHistoryPanel();
    JPanel transactionHistoryPanel = createTransactionHistoryPanel();
     
    mainPanel.add(dashboardPanel, "Dashboard");
    mainPanel.add(inventoryPanel, "Inventory");
    mainPanel.add(billingPanel, "Billing");
    mainPanel.add(billingHistoryPanel, "BillingHistory");
    mainPanel.add(transactionHistoryPanel, "TransactionHistory");
    mainPanel.add(substorePanel, "Substore");
    mainPanel.add(storeReports, "Reports");

    // Create menu bar with frontpage colors
    JMenuBar menuBar = createMenuBar();
    setJMenuBar(menuBar);
    
    // Add main panel to content
    getContentPane().add(mainPanel, BorderLayout.CENTER);
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        // In createMenuBar method
        menuBar.setBackground(new Color(150, 90, 60)); // Main brown
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
        userLabel.setFont(new Font("Georgia", Font.BOLD, 14));
        userLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(userLabel);
        
        return menuBar;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false); // Transparent to show background
        
        // Header with frontpage styling
        JLabel header = new JLabel("BiblioFlow Dashboard", SwingConstants.CENTER);
        header.setFont(new Font("Georgia", Font.BOLD, 36));
        header.setForeground(new Color(80, 50, 40));
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(header, BorderLayout.NORTH);
        
        // Quick actions panel
        JPanel actionsPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        actionsPanel.setOpaque(false);
        
        // Create quick action buttons with frontpage colors
        JButton inventoryBtn = createDashboardButton("📚 Inventory Management", "Manage book stock", new Color(150, 90, 60));
        JButton billingBtn = createDashboardButton("💰 Billing", "Process sales", new Color(130, 80, 50));
        JButton billingHistoryBtn = createDashboardButton("📋 Billing History", "View past bills", new Color(110, 70, 40));
        JButton transactionHistoryBtn = createDashboardButton("💳 Transaction History", "View all transactions", new Color(90, 60, 30));
        JButton substoreBtn = createDashboardButton("🏪 Substore", "Check other stores", new Color(150, 90, 60));
        JButton reportsBtn = createDashboardButton("📊 Reports", "View sales & profits", new Color(130, 80, 50));

        // Add action listeners
        inventoryBtn.addActionListener(_ -> {
            refreshInventory();
            cardLayout.show(mainPanel, "Inventory");
        });
        billingBtn.addActionListener(_ -> cardLayout.show(mainPanel, "Billing"));
        billingHistoryBtn.addActionListener(_ -> cardLayout.show(mainPanel, "BillingHistory"));
        transactionHistoryBtn.addActionListener(_ -> cardLayout.show(mainPanel, "TransactionHistory"));
        substoreBtn.addActionListener(_ -> {
            refreshSubstore();
            cardLayout.show(mainPanel, "Substore");
        });
        reportsBtn.addActionListener(e -> cardLayout.show(mainPanel, "Reports"));
        
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

    private JButton createDashboardButton(String title, String subtitle, Color color) {
    JButton button = new JButton("<html><center><b>" + title + "</b><br><small>" + subtitle + "</small></center></html>");
    button.setBackground(color);
    button.setForeground(Color.WHITE);
    button.setFont(new Font("Georgia", Font.BOLD, 16));
    button.setPreferredSize(new Dimension(200, 100));
    button.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    button.setFocusPainted(false);
    
    // Hover effect - use darker brown like in frontpage
    button.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            button.setBackground(new Color(120, 70, 50)); // Darker brown for hover
        }

        public void mouseExited(java.awt.event.MouseEvent evt) {
            button.setBackground(color);
        }
    });
    
    return button;
    }
    
    private JPanel createInventoryPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setOpaque(false);

    // DB connection
    Connection connection = null;
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/your_db_name", "username", "password");
    } catch (Exception e) {
        e.printStackTrace();
    }

    BookDAO bookDAO = new BookDAO(connection);

    // Tabbed Pane
    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.setOpaque(false);

    // Inventory View Tab
    JPanel inventoryViewPanel = new JPanel(new BorderLayout()); // create panel for inventory view
    tabbedPane.addTab("Inventory View", inventoryViewPanel);

    // Books Panel inside Inventory View
    JPanel booksPanel = new JPanel();
    booksPanel.setLayout(new BoxLayout(booksPanel, BoxLayout.Y_AXIS));
    JScrollPane scrollPane = new JScrollPane(booksPanel);
    inventoryViewPanel.add(scrollPane, BorderLayout.CENTER);
    // Fetch books and add cards
    List<Book> booksList = bookDAO.getAllBooks();
    for (Book book : booksList) {
        JPanel bookCard = createBookCard(book);
        booksPanel.add(bookCard);
    }
    booksPanel.revalidate();
    booksPanel.repaint();

    // Search Tab
    SearchPanel searchPanel = new SearchPanel(bookDAO);
    tabbedPane.addTab("Search Books", searchPanel);

    panel.add(tabbedPane, BorderLayout.CENTER);
    return panel;
    }


    private JPanel createInventoryViewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        // Header
        JPanel headerPanel = createInventoryHeader();
        
        // Book grid
        JPanel booksPanel = new JPanel(new WrapLayout());
        booksPanel.setBackground(new Color(250, 240, 230));
        JScrollPane scrollPane = new JScrollPane(booksPanel);
        scrollPane.setName("inventoryScrollPane"); // Name for identification
        scrollPane.getViewport().setBackground(new Color(250, 240, 230));
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load initial data
        refreshInventoryDisplay(booksPanel);
        
        return panel;
    }

    private JPanel createInventoryHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(150, 90, 60)); // Main brown

        JLabel titleLabel = new JLabel("Inventory Management", SwingConstants.CENTER); // Changed variable name
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        // Control buttons panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setOpaque(false);
        
        JButton addBookBtn = new JButton("Add New Book");
        addBookBtn.setBackground(new Color(150, 90, 60)); // Main brown
        addBookBtn.setForeground(Color.WHITE);
        addBookBtn.setFont(new Font("Georgia", Font.BOLD, 14));
        addBookBtn.addActionListener(e -> showAddBookDialog());
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(130, 80, 50)); // Slightly darker brown
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFont(new Font("Georgia", Font.BOLD, 14));
        refreshBtn.addActionListener(e -> refreshInventory());
        
        controlPanel.add(refreshBtn);
        controlPanel.add(addBookBtn);
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(controlPanel, BorderLayout.EAST);
        
        return headerPanel;
    }

    private void showEditBookDialog(Book book) {
        // Create edit dialog
        JDialog editDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Edit Book", Dialog.ModalityType.APPLICATION_MODAL);
        editDialog.setLayout(new BorderLayout());
        editDialog.setSize(400, 500);
        editDialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(new Color(250, 240, 230));
        
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
        buttonPanel.setBackground(new Color(250, 240, 230));
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
            noBooksLabel.setFont(new Font("Georgia", Font.PLAIN, 16));
            noBooksLabel.setForeground(new Color(80, 50, 40));
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
        BorderFactory.createLineBorder(new Color(150, 90, 60), 1),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));
    card.setPreferredSize(new Dimension(318, 188));
    card.setBackground(new Color(255, 250, 245));

    // =============================
    // Book Info Section
    // =============================
    JLabel titleLabel = new JLabel("<html><b>" + book.getTitle() + "</b></html>");
    titleLabel.setFont(new Font("Georgia", Font.BOLD, 14));
    titleLabel.setForeground(new Color(80, 50, 40));

    JLabel author = new JLabel("By: " + book.getAuthor());
    author.setForeground(new Color(80, 50, 40));

    JLabel price = new JLabel("₹" + book.getPrice());
    price.setForeground(new Color(80, 50, 40));

    JLabel stock = new JLabel("Stock: " + book.getStockQuantity());
    stock.setForeground(new Color(80, 50, 40));

    JLabel isbn = new JLabel("ISBN: " + book.getIsbn());
    isbn.setForeground(new Color(80, 50, 40));

    JPanel infoPanel = new JPanel(new GridLayout(5, 1, 5, 5));
    infoPanel.setBackground(new Color(255, 250, 245));
    infoPanel.add(titleLabel);
    infoPanel.add(author);
    infoPanel.add(price);
    infoPanel.add(stock);
    infoPanel.add(isbn);

    // =============================
    // Cover Image Section
    // =============================
    JLabel coverLabel;
    try {
        String path = book.getImagePath();
        if (path != null && !path.trim().isEmpty()) {
            ImageIcon icon = new ImageIcon(path);
            java.awt.Image scaled = icon.getImage().getScaledInstance(80, 120, java.awt.Image.SCALE_SMOOTH);
            coverLabel = new JLabel(new ImageIcon(scaled));
        } else {
            throw new Exception("No path");
        }
    } catch (Exception e) {
        coverLabel = new JLabel("No Image");
        coverLabel.setHorizontalAlignment(SwingConstants.CENTER);
        coverLabel.setPreferredSize(new Dimension(80, 120));
    }

    // =============================
    // Combine image + info
    // =============================
    JPanel centerPanel = new JPanel(new BorderLayout());
    centerPanel.setBackground(new Color(255, 250, 245));
    centerPanel.add(coverLabel, BorderLayout.WEST);
    centerPanel.add(infoPanel, BorderLayout.CENTER);

    // =============================
    // Action Buttons
    // =============================
    JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 0));
    buttonPanel.setBackground(new Color(255, 250, 245));

    JButton editBtn = new JButton("Edit");
    editBtn.setBackground(new Color(150, 90, 60));
    editBtn.setForeground(Color.WHITE);
    editBtn.addActionListener(e -> showEditBookDialog(book));

    JButton deleteBtn = new JButton("Delete");
    deleteBtn.setBackground(new Color(130, 70, 40));
    deleteBtn.setForeground(Color.WHITE);
    deleteBtn.addActionListener(e -> deleteBook(book));

    buttonPanel.add(editBtn);
    buttonPanel.add(deleteBtn);

    // =============================
    // Assemble Card
    // =============================
    card.add(centerPanel, BorderLayout.CENTER);
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
        panel.setOpaque(false);

        // --- Header ---
        JLabel titleLabel = new JLabel("Billing System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 32));
        titleLabel.setForeground(new Color(80, 50, 40));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Create tabbed pane for Billing methods
        JTabbedPane billingTabs = new JTabbedPane();
        billingTabs.setOpaque(false);
        
        // Tab 1: Barcode Scanning
        JPanel scanPanel = createBarcodeScanPanel();
        billingTabs.addTab("Barcode Scanning", scanPanel);
        
        // Tab 2: Manual Bill Entry
        billingTabs.addTab("Manual Bill Entry", manualBillPanel);

        panel.add(billingTabs, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createBarcodeScanPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        // --- Billing table ---
        String[] columns = {"ISBN", "Title", "Price", "Qty", "Total"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable billingTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(billingTable);
        panel.add(tableScroll, BorderLayout.CENTER);

        // Use the color constants (fixed)
        billingTable.setBackground(new Color(255, 250, 245));
        billingTable.setForeground(new Color(80, 50, 40));
        billingTable.setGridColor(new Color(200, 180, 160));
        billingTable.setSelectionBackground(new Color(150, 90, 60));
        billingTable.setSelectionForeground(Color.WHITE);
        billingTable.setFillsViewportHeight(true); // fills empty space

        // Update table header colors
        JTableHeader header = billingTable.getTableHeader();
        header.setBackground(new Color(150, 90, 60));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Georgia", Font.BOLD, 14));

        // --- Log area ---
        JTextArea logArea = new JTextArea(5, 30);
        logArea.setEditable(false);
        logArea.setBackground(new Color(255, 250, 245));
        logArea.setForeground(new Color(80, 50, 40));
        JScrollPane logScroll = new JScrollPane(logArea);

        // --- Total label ---
        JLabel totalLabel = new JLabel("Total: ₹0.00");
        totalLabel.setFont(new Font("Georgia", Font.BOLD, 18));
        totalLabel.setForeground(new Color(150, 90, 60));

        // --- ScanBarcodePanel ---
        ScanBarcodePanel scanPanel = new ScanBarcodePanel(billingTable, logArea, totalLabel);
        scanPanel.setPreferredSize(new Dimension(400, 300));

        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setOpaque(false);
        rightPanel.add(scanPanel, BorderLayout.CENTER);
        rightPanel.add(logScroll, BorderLayout.SOUTH);

        panel.add(rightPanel, BorderLayout.EAST);

        // --- Control panel (checkout / clear cart) ---
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setOpaque(false);

        JButton checkoutBtn = new JButton("Proceed to Payment");
        checkoutBtn.setBackground(new Color(150, 90, 60)); // Main brown
        checkoutBtn.setForeground(Color.WHITE);

        checkoutBtn.setFont(new Font("Georgia", Font.BOLD, 14));
        checkoutBtn.addActionListener(e -> proceedToPaymentFromTable(tableModel));

        JButton clearBtn = new JButton("Clear Cart");
        clearBtn.setBackground(new Color(130, 70, 40)); // Darker brown
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFont(new Font("Georgia", Font.BOLD, 14));
        clearBtn.addActionListener(e -> {
            tableModel.setRowCount(0);
            totalLabel.setText("Total: ₹0.00");
            logArea.setText("");
        });

        JButton removeBtn = new JButton("Remove");
        removeBtn.setBackground(new Color(110, 60, 30)); // Even darker brown
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setFont(new Font("Georgia", Font.BOLD, 14));

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
        panel.setOpaque(false);
        
        // Header with frontpage styling
        JLabel header = new JLabel("📋 Bill History", SwingConstants.CENTER);
        header.setFont(new Font("Georgia", Font.BOLD, 32));
        header.setForeground(new Color(80, 50, 40));
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(header, BorderLayout.NORTH);
        
        // Create and add the BillHistoryPanel
        BillHistoryPanel billHistoryPanel = new BillHistoryPanel();
        panel.add(billHistoryPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createTransactionHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        // Header with frontpage styling
        JLabel header = new JLabel("Transaction History", SwingConstants.CENTER);
        header.setFont(new Font("Georgia", Font.BOLD, 32));
        header.setForeground(new Color(80, 50, 40));
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(header, BorderLayout.NORTH);
        
        // Create and add the TransactionHistoryPanel
        TransactionHistoryPanel transactionPanel = new TransactionHistoryPanel();
        panel.add(transactionPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSubstorePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        // Header with title and search controls
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(150, 90, 60)); // Main brown

        JLabel titleLabel = new JLabel("Substore Book Lookup", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
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
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(searchControls, BorderLayout.SOUTH);
        
        // Content panel with scroll pane
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        JPanel booksPanel = new JPanel(new WrapLayout());
        booksPanel.setBackground(new Color(250, 240, 230));
        JScrollPane scrollPane = new JScrollPane(booksPanel);
        scrollPane.setName("substoreScrollPane");
        scrollPane.getViewport().setBackground(new Color(250, 240, 230));
        
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
            noBooksLabel.setFont(new Font("Georgia", Font.PLAIN, 16));
            noBooksLabel.setForeground(new Color(80, 50, 40));
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
        card.setBorder(BorderFactory.createLineBorder(new Color(150, 90, 60), 1));
        card.setPreferredSize(new Dimension(250, 120));
        card.setBackground(new Color(255, 250, 245));
        
        JLabel titleLabel = new JLabel("<html><b>" + book.getTitle() + "</b></html>");
        titleLabel.setForeground(new Color(80, 50, 40));
        JLabel store = new JLabel("Store: " + book.getLocation());
        store.setForeground(new Color(80, 50, 40));
        JLabel qty = new JLabel("Qty: " + book.getStockQuantity());
        qty.setForeground(new Color(80, 50, 40));
        JLabel price = new JLabel("Price: ₹" + book.getPrice());
        price.setForeground(new Color(80, 50, 40));
        
        JPanel info = new JPanel(new GridLayout(4, 1));
        info.setBackground(new Color(255, 250, 245));
        info.add(titleLabel);
        info.add(store);
        info.add(qty);
        info.add(price);
        
        JButton addBtn = new JButton("Add to Cart");
        addBtn.setBackground(new Color(150, 90, 60));
        addBtn.setForeground(Color.WHITE);
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
        panel.setBackground(new Color(250, 240, 230));
        
        String[] labels = {"ISBN:", "Title:", "Author:", "Publisher:", "Edition:", "Price:", "Quantity:", "Rack Number:", "Genre:"};
        JTextField[] fields = new JTextField[labels.length];
        
        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i]);
            label.setForeground(new Color(80, 50, 40));
            panel.add(label);
            fields[i] = new JTextField();
            panel.add(fields[i]);
        }
        
        JButton saveBtn = new JButton("Save Book");
        saveBtn.setBackground(new Color(150, 90, 60));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(_ -> saveBookToDatabase(fields, dialog));
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(new Color(130, 80, 50));
        cancelBtn.setForeground(Color.WHITE);
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
                
                JLabel titleLabel = new JLabel("<html><b>" + b.getTitle() + "</b></html>");
                JLabel author = new JLabel("Author: " + b.getAuthor());
                JLabel category = new JLabel("Category: " + b.getGenre());
                JLabel price = new JLabel("Price: ₹" + b.getPrice());
                
                JPanel info = new JPanel(new GridLayout(4, 1));
                info.add(titleLabel);
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