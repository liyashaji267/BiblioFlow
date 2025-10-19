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
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 245, 245));

        // ---------- HEADER ----------
        JLabel headerLabel = new JLabel("Search Books", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerLabel.setForeground(new Color(70, 130, 180));
        add(headerLabel, BorderLayout.NORTH);

        // ---------- SEARCH PANEL ----------
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(new Color(245, 245, 245));

        searchField = new JTextField(25);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));

        searchType = new JComboBox<>(new String[]{"Title", "Author", "ISBN", "Publisher", "All Fields"});
        searchType.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(70, 130, 180));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("Arial", Font.BOLD, 14));
        searchBtn.setFocusPainted(false);

        JButton clearBtn = new JButton("Clear");
        clearBtn.setBackground(new Color(244, 67, 54));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFont(new Font("Arial", Font.BOLD, 14));
        clearBtn.setFocusPainted(false);

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("By:"));
        searchPanel.add(searchType);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);

        add(searchPanel, BorderLayout.CENTER);

        // ---------- RESULTS PANEL ----------
        resultsPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 15, 15));
        resultsPanel.setBackground(Color.WHITE);
        resultsScroll = new JScrollPane(resultsPanel);
        resultsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        resultsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        resultsScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            "Search Results",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 16),
            new Color(70, 130, 180)
        ));

        add(resultsScroll, BorderLayout.SOUTH);

        // ---------- ACTIONS ----------
        searchBtn.addActionListener(e -> performSearch());
        clearBtn.addActionListener(e -> clearSearch());
        searchField.addActionListener(e -> performSearch());
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
            empty.setFont(new Font("Arial", Font.ITALIC, 16));
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
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(280, 160));

        JLabel titleLabel = new JLabel("<html><b>" + book.getTitle() + "</b></html>");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel authorLabel = new JLabel("Author: " + book.getAuthor());
        JLabel isbnLabel = new JLabel("ISBN: " + book.getIsbn());
        JLabel publisherLabel = new JLabel("Publisher: " + book.getPublisher());
        JLabel stockLabel = new JLabel("In Stock: " + book.getStockQuantity());
        JLabel priceLabel = new JLabel("Price: ₹" + book.getPrice());

        JPanel infoPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        infoPanel.add(titleLabel);
        infoPanel.add(authorLabel);
        infoPanel.add(isbnLabel);
        infoPanel.add(publisherLabel);
        infoPanel.add(stockLabel);
        infoPanel.add(priceLabel);

        JButton viewBtn = new JButton("View Details");
        viewBtn.setBackground(new Color(70, 130, 180));
        viewBtn.setForeground(Color.WHITE);
        viewBtn.setFont(new Font("Arial", Font.BOLD, 13));
        viewBtn.setFocusPainted(false);
        viewBtn.addActionListener(e -> showBookDetails(book));

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(viewBtn, BorderLayout.SOUTH);

        return card;
    }

    private void showBookDetails(Book book) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(book.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.add(new JLabel("Author: " + book.getAuthor()));
        infoPanel.add(new JLabel("ISBN: " + book.getIsbn()));
        infoPanel.add(new JLabel("Publisher: " + book.getPublisher()));
        infoPanel.add(new JLabel("Stock Quantity: " + book.getStockQuantity()));
        infoPanel.add(new JLabel("Price: ₹" + book.getPrice()));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(infoPanel, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, "Book Details", JOptionPane.INFORMATION_MESSAGE);
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
