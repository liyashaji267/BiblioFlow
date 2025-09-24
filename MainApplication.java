// MainApplication.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainApplication extends JFrame {
    private User currentUser;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private List<Book> cartItems;
    private double cartTotal;
    
    // Database DAOs
    private BookDAO bookDAO;
    private BillDAO billDAO;
    
    // Reference to cart components for updating
    private JTextArea cartArea;
    private JLabel totalLabel;
    
    public MainApplication(User currentUser) {
        this.currentUser = currentUser;
        this.cartItems = new ArrayList<>();
        this.cartTotal = 0.0;
        this.bookDAO = new BookDAO();
        this.billDAO = new BillDAO();
        
        setTitle("BiblioFlow - Main Application");
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        initUI();
    }
    
    private void initUI() {
        // Create menu bar
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Create different panels
        JPanel dashboardPanel = createDashboardPanel();
        JPanel inventoryPanel = createInventoryPanel();
        JPanel billingPanel = createBillingPanel();
        JPanel searchPanel = createSearchPanel();
        JPanel substorePanel = createSubstorePanel();
        
        mainPanel.add(dashboardPanel, "Dashboard");
        mainPanel.add(inventoryPanel, "Inventory");
        mainPanel.add(billingPanel, "Billing");
        mainPanel.add(searchPanel, "Search");
        mainPanel.add(substorePanel, "Substore");
        
        add(mainPanel);
        cardLayout.show(mainPanel, "Dashboard");
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
        
        dashboardItem.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard"));
        logoutItem.addActionListener(e -> logout());
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(dashboardItem);
        fileMenu.addSeparator();
        fileMenu.add(logoutItem);
        fileMenu.add(exitItem);
        
        // Operations Menu
        JMenu operationsMenu = new JMenu("Operations");
        operationsMenu.setForeground(Color.WHITE);
        
        JMenuItem inventoryItem = new JMenuItem("Inventory Management");
        JMenuItem billingItem = new JMenuItem("Billing");
        JMenuItem searchItem = new JMenuItem("Search Books");
        JMenuItem substoreItem = new JMenuItem("Substore Lookup");
        
        inventoryItem.addActionListener(e -> {
            refreshInventory();
            cardLayout.show(mainPanel, "Inventory");
        });
        billingItem.addActionListener(e -> cardLayout.show(mainPanel, "Billing"));
        searchItem.addActionListener(e -> cardLayout.show(mainPanel, "Search"));
        substoreItem.addActionListener(e -> {
            refreshSubstore();
            cardLayout.show(mainPanel, "Substore");
        });
        
        operationsMenu.add(inventoryItem);
        operationsMenu.add(billingItem);
        operationsMenu.add(searchItem);
        operationsMenu.add(substoreItem);
        
        menuBar.add(fileMenu);
        menuBar.add(operationsMenu);
        
        // User info label
        JLabel userLabel = new JLabel("Welcome, " + currentUser.getUsername());
        userLabel.setForeground(Color.WHITE);
        userLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(userLabel);
        
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
        
        // Quick actions panel
        JPanel actionsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        actionsPanel.setBackground(Color.WHITE);
        
        // Create quick action buttons
        JButton inventoryBtn = createDashboardButton("📚 Inventory Management", "Manage book stock", new Color(76, 175, 80));
        JButton billingBtn = createDashboardButton("💰 Billing", "Process sales", new Color(33, 150, 243));
        JButton searchBtn = createDashboardButton("🔍 Search Books", "Find books", new Color(255, 152, 0));
        JButton substoreBtn = createDashboardButton("🏪 Substore", "Check other stores", new Color(156, 39, 176));
        
        inventoryBtn.addActionListener(e -> {
            refreshInventory();
            cardLayout.show(mainPanel, "Inventory");
        });
        billingBtn.addActionListener(e -> cardLayout.show(mainPanel, "Billing"));
        searchBtn.addActionListener(e -> cardLayout.show(mainPanel, "Search"));
        substoreBtn.addActionListener(e -> {
            refreshSubstore();
            cardLayout.show(mainPanel, "Substore");
        });
        
        actionsPanel.add(inventoryBtn);
        actionsPanel.add(billingBtn);
        actionsPanel.add(searchBtn);
        actionsPanel.add(substoreBtn);
        
        panel.add(actionsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
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
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setPreferredSize(new Dimension(1400, 60));
        
        JLabel title = new JLabel("Inventory Management", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        
        JButton addBookBtn = new JButton("Add New Book");
        addBookBtn.setBackground(new Color(76, 175, 80));
        addBookBtn.setForeground(Color.WHITE);
        addBookBtn.addActionListener(e -> showAddBookDialog());
        
        headerPanel.add(title, BorderLayout.CENTER);
        headerPanel.add(addBookBtn, BorderLayout.EAST);
        
        // Book grid
        JPanel booksPanel = new JPanel(new WrapLayout());
        booksPanel.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(booksPanel);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void refreshInventory() {
        // Get the inventory panel (index 1 in mainPanel)
        JPanel inventoryPanel = (JPanel) mainPanel.getComponent(1);
        JScrollPane scrollPane = null;
        
        // Find the scroll pane in inventory panel
        for (Component c : inventoryPanel.getComponents()) {
            if (c instanceof JScrollPane) {
                scrollPane = (JScrollPane) c;
                break;
            }
        }
        
        if (scrollPane == null) return;
        
        JPanel bookContainer = (JPanel) scrollPane.getViewport().getView();
        bookContainer.removeAll();
        
        // Fetch books from DB
        List<Book> books = bookDAO.getAllBooks();
        
        if (books.isEmpty()) {
            bookContainer.add(new JLabel("No books found in inventory."));
        } else {
            for (Book b : books) {
                JPanel card = new JPanel(new BorderLayout());
                card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                card.setPreferredSize(new Dimension(250, 120));
                
                JLabel title = new JLabel("<html><b>" + b.getTitle() + "</b></html>");
                JLabel author = new JLabel("By: " + b.getAuthor());
                JLabel price = new JLabel("₹" + b.getPrice());
                JLabel stock = new JLabel("Stock: " + b.getStockQuantity());

                JPanel info = new JPanel(new GridLayout(4, 1));
                info.add(title);
                info.add(author);
                info.add(price);
                info.add(stock);

                card.add(info, BorderLayout.CENTER);
                bookContainer.add(card);
            }
        }
        
        bookContainer.revalidate();
        bookContainer.repaint();
    }
    
    private JPanel createBillingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel title = new JLabel("Billing System", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        
        JButton scanBtn = new JButton("📷 Scan Barcode");
        scanBtn.setBackground(new Color(33, 150, 243));
        scanBtn.setForeground(Color.WHITE);
        scanBtn.addActionListener(e -> simulateBarcodeScan());
        
        headerPanel.add(title, BorderLayout.CENTER);
        headerPanel.add(scanBtn, BorderLayout.EAST);
        
        // Cart items
        cartArea = new JTextArea();
        cartArea.setEditable(false);
        cartArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane cartScroll = new JScrollPane(cartArea);
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        
        JTextField customerNameField = new JTextField(15);
        customerNameField.setToolTipText("Customer Name (Optional)");
        
        JTextField customerPhoneField = new JTextField(15);
        customerPhoneField.setToolTipText("Customer Phone (Optional)");
        
        totalLabel = new JLabel("Total: ₹0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setForeground(Color.RED);
        
        JButton checkoutBtn = new JButton("Proceed to Payment");
        checkoutBtn.setBackground(new Color(76, 175, 80));
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.addActionListener(e -> proceedToPayment(customerNameField.getText(), customerPhoneField.getText()));
        
        JButton clearCartBtn = new JButton("Clear Cart");
        clearCartBtn.setBackground(Color.RED);
        clearCartBtn.setForeground(Color.WHITE);
        clearCartBtn.addActionListener(e -> clearCart());
        
        controlPanel.add(new JLabel("Customer:"));
        controlPanel.add(customerNameField);
        controlPanel.add(new JLabel("Phone:"));
        controlPanel.add(customerPhoneField);
        controlPanel.add(totalLabel);
        controlPanel.add(checkoutBtn);
        controlPanel.add(clearCartBtn);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(cartScroll, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Search header
        JPanel searchHeader = new JPanel(new BorderLayout());
        searchHeader.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel title = new JLabel("Search Books", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        
        JPanel searchControls = new JPanel(new FlowLayout());
        
        JTextField searchField = new JTextField(20);
        JComboBox<String> searchType = new JComboBox<>(new String[]{"Title", "Author", "ISBN", "Publisher"});
        JButton searchBtn = new JButton("Search");
        
        searchBtn.addActionListener(e -> performSearch(searchField.getText(), (String)searchType.getSelectedItem()));
        
        searchControls.add(new JLabel("Search:"));
        searchControls.add(searchField);
        searchControls.add(new JLabel("By:"));
        searchControls.add(searchType);
        searchControls.add(searchBtn);
        
        searchHeader.add(title, BorderLayout.NORTH);
        searchHeader.add(searchControls, BorderLayout.CENTER);
        
        // Results panel
        JPanel resultsPanel = new JPanel(new WrapLayout());
        resultsPanel.setBackground(Color.WHITE);
        JScrollPane resultsScroll = new JScrollPane(resultsPanel);
        resultsScroll.setName("searchResultsScroll"); // Name for identification
        
        panel.add(searchHeader, BorderLayout.NORTH);
        panel.add(resultsScroll, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSubstorePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel title = new JLabel("Substore Book Lookup", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Content panel with scroll pane
        JPanel contentPanel = new JPanel(new BorderLayout());
        JPanel booksPanel = new JPanel(new WrapLayout());
        JScrollPane scrollPane = new JScrollPane(booksPanel);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
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
        saveBtn.addActionListener(e -> saveBookToDatabase(fields, dialog));
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        
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
    
    private void refreshSubstore() {
        // Get the substore panel (index 4 in mainPanel)
        JPanel substorePanel = (JPanel) mainPanel.getComponent(4);
        substorePanel.removeAll();
        
        JLabel heading = new JLabel("Substore Book Lookup", JLabel.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 24));
        heading.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JPanel container = new JPanel(new WrapLayout());
        JScrollPane scrollPane = new JScrollPane(container);
        
        substorePanel.setLayout(new BorderLayout());
        substorePanel.add(heading, BorderLayout.NORTH);
        substorePanel.add(scrollPane, BorderLayout.CENTER);
        
        List<Book> books = bookDAO.getBooksFromSubstores();
        
        if (books.isEmpty()) {
            container.add(new JLabel("No books available in substores."));
        } else {
            for (Book b : books) {
                JPanel card = new JPanel(new BorderLayout());
                card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                card.setPreferredSize(new Dimension(250, 120));
                
                JLabel title = new JLabel("<html><b>" + b.getTitle() + "</b></html>");
                JLabel store = new JLabel("Store: " + b.getLocation());
                JLabel qty = new JLabel("Qty: " + b.getStockQuantity());
                JLabel price = new JLabel("Price: ₹" + b.getPrice());
                
                JPanel info = new JPanel(new GridLayout(4, 1));
                info.add(title);
                info.add(store);
                info.add(qty);
                info.add(price);
                
                JButton addBtn = new JButton("Add to Cart");
                addBtn.addActionListener(e -> addToCart(b));
                
                card.add(info, BorderLayout.CENTER);
                card.add(addBtn, BorderLayout.SOUTH);
                
                container.add(card);
            }
        }
        
        substorePanel.revalidate();
        substorePanel.repaint();
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
                addBtn.addActionListener(e -> addToCart(b));
                
                card.add(info, BorderLayout.CENTER);
                card.add(addBtn, BorderLayout.SOUTH);
                
                container.add(card);
            }
        }
        
        container.revalidate();
        container.repaint();
    }
}