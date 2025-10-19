import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

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
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(250, 240, 230)); // Frontpage light cream

        // ---------- HEADER ----------
        JLabel headerLabel = new JLabel("Search Books", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Georgia", Font.BOLD, 32));
        headerLabel.setForeground(new Color(80, 50, 40)); // Frontpage dark brown
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        add(headerLabel, BorderLayout.NORTH);

        // ---------- SEARCH PANEL ----------
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        searchPanel.setBackground(new Color(250, 240, 230));
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 90, 60), 2), // Frontpage brown
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Georgia", Font.BOLD, 16));
        searchLabel.setForeground(new Color(80, 50, 40));

        searchField = new JTextField(25);
        searchField.setFont(new Font("Georgia", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 90, 60)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));

        JLabel typeLabel = new JLabel("By:");
        typeLabel.setFont(new Font("Georgia", Font.BOLD, 16));
        typeLabel.setForeground(new Color(80, 50, 40));

        searchType = new JComboBox<>(new String[]{"Title", "Author", "ISBN", "Publisher", "All Fields"});
        searchType.setFont(new Font("Georgia", Font.PLAIN, 14));
        searchType.setBackground(Color.WHITE);
        searchType.setBorder(BorderFactory.createLineBorder(new Color(150, 90, 60)));

        JButton searchBtn = createStyledButton("Search", new Color(150, 90, 60)); // Frontpage brown
        JButton clearBtn = createStyledButton("Clear", new Color(130, 80, 50)); // Darker brown

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(typeLabel);
        searchPanel.add(searchType);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);

        add(searchPanel, BorderLayout.CENTER);

        // ---------- RESULTS PANEL ----------
        resultsPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 20, 20));
        resultsPanel.setBackground(new Color(250, 240, 230));
        
        resultsScroll = new JScrollPane(resultsPanel);
        resultsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        resultsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        resultsScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(150, 90, 60), 2),
            "Search Results",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Georgia", Font.BOLD, 18),
            new Color(80, 50, 40) // Dark brown
        ));
        resultsScroll.getViewport().setBackground(new Color(250, 240, 230));
        
        // Style the scrollbar
        JScrollBar verticalScrollBar = resultsScroll.getVerticalScrollBar();
        verticalScrollBar.setBackground(new Color(230, 200, 180));
        verticalScrollBar.setBorder(BorderFactory.createLineBorder(new Color(150, 90, 60)));

        JPanel resultsContainer = new JPanel(new BorderLayout());
        resultsContainer.setBackground(new Color(250, 240, 230));
        resultsContainer.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        resultsContainer.add(resultsScroll, BorderLayout.CENTER);
        
        add(resultsContainer, BorderLayout.SOUTH);

        // ---------- ACTIONS ----------
        searchBtn.addActionListener(e -> performSearch());
        clearBtn.addActionListener(e -> clearSearch());
        searchField.addActionListener(e -> performSearch());
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Georgia", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker()),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Hover effect like frontpage
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        String type = (String) searchType.getSelectedItem();

        if (query.isEmpty()) {
            clearResults();
            JOptionPane.showMessageDialog(this, "Please enter a search term",
                "Search Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        clearResults();

        List<Book> results = searchBooks(query, type);
        displayResults(results);
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
        String q = query.toLowerCase();

        for (Book b : allBooks) {
            if (matchesSearchCriteria(b, q, fieldType)) results.add(b);
        }
        return results;
    }

    private boolean matchesSearchCriteria(Book book, String q, String type) {
        switch (type) {
            case "Title": return book.getTitle().toLowerCase().contains(q);
            case "Author": return book.getAuthor().toLowerCase().contains(q);
            case "ISBN": return book.getIsbn().toLowerCase().contains(q);
            case "Publisher": return book.getPublisher().toLowerCase().contains(q);
            case "All Fields":
                return book.getTitle().toLowerCase().contains(q) ||
                       book.getAuthor().toLowerCase().contains(q) ||
                       book.getIsbn().toLowerCase().contains(q) ||
                       book.getPublisher().toLowerCase().contains(q);
            default: return false;
        }
    }

    private void displayResults(List<Book> books) {
        if (books.isEmpty()) {
            JLabel empty = new JLabel("No books found matching your search criteria.", SwingConstants.CENTER);
            empty.setFont(new Font("Georgia", Font.ITALIC, 16));
            empty.setForeground(new Color(80, 50, 40));
            empty.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));
            resultsPanel.add(empty);
        } else {
            for (Book b : books) {
                resultsPanel.add(createBookCard(b));
            }
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private JPanel createBookCard(Book book) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 90, 60), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(new Color(255, 250, 245)); // Slightly lighter cream
        card.setPreferredSize(new Dimension(300, 180));

        // Title
        JLabel titleLabel = new JLabel("<html><div style='width:250px;'>" + book.getTitle() + "</div></html>");
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 14));
        titleLabel.setForeground(new Color(80, 50, 40));

        // Book info
        JLabel authorLabel = new JLabel("Author: " + book.getAuthor());
        authorLabel.setFont(new Font("Georgia", Font.PLAIN, 12));
        authorLabel.setForeground(new Color(80, 50, 40));

        JLabel isbnLabel = new JLabel("ISBN: " + book.getIsbn());
        isbnLabel.setFont(new Font("Georgia", Font.PLAIN, 12));
        isbnLabel.setForeground(new Color(80, 50, 40));

        JLabel publisherLabel = new JLabel("Publisher: " + book.getPublisher());
        publisherLabel.setFont(new Font("Georgia", Font.PLAIN, 12));
        publisherLabel.setForeground(new Color(80, 50, 40));

        JLabel stockLabel = new JLabel("In Stock: " + book.getStockQuantity());
        stockLabel.setFont(new Font("Georgia", Font.PLAIN, 12));
        stockLabel.setForeground(new Color(80, 50, 40));

        JLabel priceLabel = new JLabel("Price: ₹" + book.getPrice());
        priceLabel.setFont(new Font("Georgia", Font.BOLD, 13));
        priceLabel.setForeground(new Color(150, 90, 60));

        // Info panel
        JPanel infoPanel = new JPanel(new GridLayout(6, 1, 3, 3));
        infoPanel.setBackground(new Color(255, 250, 245));
        infoPanel.add(titleLabel);
        infoPanel.add(authorLabel);
        infoPanel.add(isbnLabel);
        infoPanel.add(publisherLabel);
        infoPanel.add(stockLabel);
        infoPanel.add(priceLabel);

        // View details button
        JButton viewBtn = createStyledButton("View Details", new Color(150, 90, 60));
        viewBtn.setFont(new Font("Georgia", Font.BOLD, 12));
        viewBtn.setPreferredSize(new Dimension(120, 30));

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(viewBtn, BorderLayout.SOUTH);

        return card;
    }

    private void showBookDetails(Book book) {
        // Create a custom dialog panel with frontpage styling
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(250, 240, 230));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel(book.getTitle());
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 20));
        titleLabel.setForeground(new Color(80, 50, 40));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Book details
        JPanel detailsPanel = new JPanel(new GridLayout(0, 1, 8, 8));
        detailsPanel.setBackground(new Color(250, 240, 230));
        
        addDetailRow(detailsPanel, "Author:", book.getAuthor());
        addDetailRow(detailsPanel, "ISBN:", book.getIsbn());
        addDetailRow(detailsPanel, "Publisher:", book.getPublisher());
        addDetailRow(detailsPanel, "Edition:", book.getEdition());
        addDetailRow(detailsPanel, "Genre:", book.getGenre());
        addDetailRow(detailsPanel, "Stock Quantity:", String.valueOf(book.getStockQuantity()));
        addDetailRow(detailsPanel, "Price:", "₹" + book.getPrice());
        addDetailRow(detailsPanel, "Rack Number:", book.getRackNumber());

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(detailsPanel, BorderLayout.CENTER);

        // Show in styled option pane
        JOptionPane.showMessageDialog(this, panel, "Book Details", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setBackground(new Color(250, 240, 230));
        
        JLabel labelLabel = new JLabel(label);
        labelLabel.setFont(new Font("Georgia", Font.BOLD, 13));
        labelLabel.setForeground(new Color(80, 50, 40));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Georgia", Font.PLAIN, 13));
        valueLabel.setForeground(new Color(80, 50, 40));
        
        rowPanel.add(labelLabel, BorderLayout.WEST);
        rowPanel.add(valueLabel, BorderLayout.CENTER);
        panel.add(rowPanel);
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

    public void refreshSearch() {
        if (!searchField.getText().trim().isEmpty()) {
            performSearch();
        }
    }
}