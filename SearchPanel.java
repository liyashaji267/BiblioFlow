import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

public class SearchPanel extends JPanel {
    private JTextField searchField;
    private JComboBox<String> searchType;
    private JPanel resultsPanel;
    private JScrollPane resultsScroll;
    private BookDAO bookDAO;
    
    public SearchPanel() {
        this(null);
    }
    
    public SearchPanel(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
        initializeUI();
    }
    
    public void setBookDAO(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Search header
        JPanel searchHeader = createSearchHeader();
        
        // Results area
        setupResultsPanel();
        
        add(searchHeader, BorderLayout.NORTH);
        add(resultsScroll, BorderLayout.CENTER);
    }
    
    private JPanel createSearchHeader() {
        JPanel searchHeader = new JPanel(new BorderLayout());
        searchHeader.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel title = new JLabel("Search Books", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        
        // Search controls
        JPanel searchControls = createSearchControls();
        
        searchHeader.add(title, BorderLayout.NORTH);
        searchHeader.add(searchControls, BorderLayout.CENTER);
        
        return searchHeader;
    }
    
    private JPanel createSearchControls() {
        JPanel searchControls = new JPanel(new FlowLayout());
        
        searchField = new JTextField(20);
        searchType = new JComboBox<>(new String[]{"Title", "Author", "ISBN", "Publisher", "All Fields"});
        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear");
        
        // Add action listeners
        searchBtn.addActionListener(e -> performSearch());
        clearBtn.addActionListener(e -> clearSearch());
        
        // Allow searching by pressing Enter in the text field
        searchField.addActionListener(e -> performSearch());
        
        searchControls.add(new JLabel("Search:"));
        searchControls.add(searchField);
        searchControls.add(new JLabel("By:"));
        searchControls.add(searchType);
        searchControls.add(searchBtn);
        searchControls.add(clearBtn);
        
        return searchControls;
    }
    
    private void setupResultsPanel() {
        resultsPanel = new JPanel(new WrapLayout());
        resultsPanel.setBackground(Color.WHITE);
        resultsScroll = new JScrollPane(resultsPanel);
        resultsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        resultsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        resultsScroll.setName("searchResultsScroll");
    }
    
    private void performSearch() {
        String query = searchField.getText().trim();
        String searchFieldType = (String) searchType.getSelectedItem();
        
        if (query.isEmpty()) {
            clearResults();
            JOptionPane.showMessageDialog(this, "Please enter a search term", 
                "Search Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Clear previous results
        clearResults();
        
        // Perform search based on the selected field type
        List<Book> searchResults = searchBooks(query, searchFieldType);
        
        // Display results
        displaySearchResults(searchResults);
    }
    
    private List<Book> searchBooks(String query, String fieldType) {
        if (bookDAO == null) {
            JOptionPane.showMessageDialog(this, 
                "Database connection not available", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return new ArrayList<>();
        }
        
        List<Book> allBooks = bookDAO.getAllBooks();
        List<Book> results = new ArrayList<>();
        
        String searchQuery = query.toLowerCase().trim();
        
        for (Book book : allBooks) {
            if (matchesSearchCriteria(book, searchQuery, fieldType)) {
                results.add(book);
            }
        }
        
        return results;
    }
    
    private boolean matchesSearchCriteria(Book book, String query, String fieldType) {
        if (query.isEmpty()) {
            return false;
        }
        
        switch (fieldType) {
            case "Title":
                return book.getTitle().toLowerCase().contains(query);
            case "Author":
                return book.getAuthor().toLowerCase().contains(query);
            case "ISBN":
                return book.getIsbn().toLowerCase().contains(query);
            case "Publisher":
                return book.getPublisher().toLowerCase().contains(query);
            case "All Fields":
                return book.getTitle().toLowerCase().contains(query) ||
                       book.getAuthor().toLowerCase().contains(query) ||
                       book.getIsbn().toLowerCase().contains(query) ||
                       book.getPublisher().toLowerCase().contains(query);
            default:
                return false;
        }
    }
    
    private void displaySearchResults(List<Book> books) {
        if (books.isEmpty()) {
            JLabel noResults = new JLabel("No books found matching your search criteria.");
            noResults.setHorizontalAlignment(SwingConstants.CENTER);
            noResults.setFont(new Font("Arial", Font.ITALIC, 16));
            resultsPanel.add(noResults);
        } else {
            for (Book book : books) {
                JPanel bookCard = createBookCard(book);
                resultsPanel.add(bookCard);
            }
        }
        
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }
    
    private JPanel createBookCard(Book book) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(280, 150));
        
        JLabel titleLabel = new JLabel("<html><b>" + book.getTitle() + "</b></html>");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel authorLabel = new JLabel("Author: " + book.getAuthor());
        JLabel isbnLabel = new JLabel("ISBN: " + book.getIsbn());
        JLabel publisherLabel = new JLabel("Publisher: " + book.getPublisher());
        JLabel stockLabel = new JLabel("In Stock: " + book.getStockQuantity());
        
        JPanel infoPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        infoPanel.add(titleLabel);
        infoPanel.add(authorLabel);
        infoPanel.add(isbnLabel);
        infoPanel.add(publisherLabel);
        infoPanel.add(stockLabel);
        
        JButton viewDetailsBtn = new JButton("View Details");
        viewDetailsBtn.addActionListener(e -> showBookDetails(book));
        
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(viewDetailsBtn, BorderLayout.SOUTH);
        
        return card;
    }
    
    private void showBookDetails(Book book) {
        // Create a detailed view dialog
        JPanel detailsPanel = new JPanel(new BorderLayout(10, 10));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel(book.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.add(new JLabel("Author: " + book.getAuthor()));
        infoPanel.add(new JLabel("ISBN: " + book.getIsbn()));
        infoPanel.add(new JLabel("Publisher: " + book.getPublisher()));
        infoPanel.add(new JLabel("Stock Quantity: " + book.getStockQuantity()));
        
        detailsPanel.add(titleLabel, BorderLayout.NORTH);
        detailsPanel.add(infoPanel, BorderLayout.CENTER);
        
        JOptionPane.showMessageDialog(this, detailsPanel, 
            "Book Details", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void clearSearch() {
        searchField.setText("");
        clearResults();
    }
    
    private void clearResults() {
        resultsPanel.removeAll();
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }
    
    // Method to refresh search results (useful when inventory changes)
    public void refreshSearch() {
        if (!searchField.getText().trim().isEmpty()) {
            performSearch();
        }
    }
    
    // Getters for external access
    public String getCurrentSearchQuery() {
        return searchField.getText();
    }
    
    public String getCurrentSearchType() {
        return (String) searchType.getSelectedItem();
    }
}