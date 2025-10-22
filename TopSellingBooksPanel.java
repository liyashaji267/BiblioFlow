// Add this method to StoreReports.java
private JPanel createTopSellingBooksPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(new Color(250, 240, 230));
    
    JLabel titleLabel = new JLabel("📊 Top Selling Books", SwingConstants.CENTER);
    titleLabel.setFont(new Font("Georgia", Font.BOLD, 24));
    titleLabel.setForeground(new Color(80, 50, 40));
    titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
    
    // Table for top selling books
    String[] columns = {"Rank", "Book Title", "Author", "Quantity Sold", "Total Revenue"};
    DefaultTableModel model = new DefaultTableModel(columns, 0);
    JTable table = new JTable(model);
    
    // Style the table
    table.setBackground(new Color(250, 240, 230));
    table.setForeground(new Color(80, 50, 40));
    table.setGridColor(new Color(200, 180, 160));
    table.setSelectionBackground(new Color(150, 90, 60));
    table.setSelectionForeground(Color.WHITE);
    table.setFont(new Font("Noto Sans Malayalam", Font.PLAIN, 12));
    
    JTableHeader header = table.getTableHeader();
    header.setBackground(new Color(150, 90, 60));
    header.setForeground(Color.WHITE);
    header.setFont(new Font("Georgia", Font.BOLD, 14));
    
    JScrollPane scrollPane = new JScrollPane(table);
    
    // Load data
    loadTopSellingBooks(model);
    
    panel.add(titleLabel, BorderLayout.NORTH);
    panel.add(scrollPane, BorderLayout.CENTER);
    
    return panel;
}

private void loadTopSellingBooks(DefaultTableModel model) {
    try {
        BillDAO billDAO = new BillDAO();
        List<BillItem> allItems = billDAO.getAllBillItems();
        
        // Group by book ISBN and calculate totals
        Map<String, BookSales> salesMap = new HashMap<>();
        
        for (BillItem item : allItems) {
            String isbn = item.getBookIsbn();
            String bookName = item.getBookName();
            
            BookSales sales = salesMap.getOrDefault(isbn, new BookSales(bookName, isbn));
            sales.addSale(item.getQuantity(), item.getPrice() * item.getQuantity());
            salesMap.put(isbn, sales);
        }
        
        // Convert to list and sort by quantity sold
        List<BookSales> topSellers = new ArrayList<>(salesMap.values());
        topSellers.sort((a, b) -> Integer.compare(b.getQuantitySold(), a.getQuantitySold()));
        
        // Add to table (top 10)
        model.setRowCount(0);
        int rank = 1;
        for (BookSales sales : topSellers) {
            if (rank > 10) break;
            model.addRow(new Object[]{
                rank++,
                sales.getBookName(),
                getAuthorFromISBN(sales.getIsbn()), // You'll need to implement this
                sales.getQuantitySold(),
                String.format("₹%.2f", sales.getTotalRevenue())
            });
        }
        
    } catch (Exception e) {
        e.printStackTrace();
    }
}

// Helper class for book sales data
class BookSales {
    private String bookName;
    private String isbn;
    private int quantitySold;
    private double totalRevenue;
    
    public BookSales(String bookName, String isbn) {
        this.bookName = bookName;
        this.isbn = isbn;
        this.quantitySold = 0;
        this.totalRevenue = 0;
    }
    
    public void addSale(int quantity, double revenue) {
        this.quantitySold += quantity;
        this.totalRevenue += revenue;
    }
    
    // Getters
    public String getBookName() { return bookName; }
    public String getIsbn() { return isbn; }
    public int getQuantitySold() { return quantitySold; }
    public double getTotalRevenue() { return totalRevenue; }
}

private String getAuthorFromISBN(String isbn) {
    // Implement this method to fetch author from your book database
    try {
        BookDAO bookDAO = new BookDAO();
        Book book = bookDAO.findBookByISBN(isbn);
        return book != null ? book.getAuthor() : "Unknown";
    } catch (Exception e) {
        return "Unknown";
    }
}