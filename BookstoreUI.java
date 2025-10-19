import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BookstoreUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(BookstoreUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("BiblioFlow - Bookstore");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 700);
        frame.setLocationRelativeTo(null);

        // Main container panel with BorderLayout
        JPanel container = new JPanel(new BorderLayout());

        // ===========================================================
        // HEADER PANEL (LOGO + TITLE)
        // ===========================================================
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Load logo image
        ImageIcon logoIcon = new ImageIcon("D:\\java project new\\JAVA Project\\BiblioFlow\\imgs\\logo.png");
        Image scaledLogo = logoIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        logoIcon = new ImageIcon(scaledLogo);

        JLabel logoLabel = new JLabel("BIBLIOFLOW", logoIcon, JLabel.LEFT);
        logoLabel.setFont(new Font("Georgia", Font.BOLD, 32));
        logoLabel.setForeground(new Color(80, 50, 40));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setIconTextGap(15);

        JLabel subtitleLabel = new JLabel("Bookstore Management System", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Serif", Font.ITALIC, 16));
        subtitleLabel.setForeground(new Color(100, 70, 60));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(logoLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(subtitleLabel);

        container.add(headerPanel, BorderLayout.NORTH);

        // ===========================================================
        // BOOK GRID PANEL
        // ===========================================================
        JPanel bookPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(250, 240, 230),
                        0, getHeight(), new Color(230, 200, 180)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bookPanel.setLayout(new GridLayout(0, 4, 20, 20));
        bookPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        BookDAO dao = new BookDAO();
        List<Book> books = dao.getAllBooks();

        if (books.isEmpty()) {
            JLabel emptyLabel = new JLabel("No books found in the database!", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Georgia", Font.BOLD, 18));
            emptyLabel.setForeground(new Color(80, 50, 40));
            bookPanel.add(emptyLabel);
        } else {
            for (Book book : books) {
                bookPanel.add(createBookCard(book));
            }
        }

        JScrollPane scrollPane = new JScrollPane(bookPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // smooth scrolling
        container.add(scrollPane, BorderLayout.CENTER);

        frame.add(container);
        frame.setVisible(true);
    }

    private static JPanel createBookCard(Book book) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(255, 250, 240, 200));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 90, 60), 2, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setPreferredSize(new Dimension(220, 340));

        // Book image
        JLabel imageLabel;
        if (book.getImagePath() != null && !book.getImagePath().isEmpty()) {
            ImageIcon icon = new ImageIcon(book.getImagePath());
            Image img = icon.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH);
            imageLabel = new JLabel(new ImageIcon(img));
        } else {
            imageLabel = new JLabel("No Image", SwingConstants.CENTER);
            imageLabel.setOpaque(true);
            imageLabel.setBackground(new Color(220, 220, 220));
            imageLabel.setPreferredSize(new Dimension(150, 200));
        }
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel titleLabel = new JLabel("<html><center>" + book.getTitle() + "</center></html>");
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 14));
        titleLabel.setForeground(new Color(80, 50, 40));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Author
        JLabel authorLabel = new JLabel(book.getAuthor());
        authorLabel.setFont(new Font("Serif", Font.ITALIC, 12));
        authorLabel.setForeground(new Color(100, 70, 60));
        authorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Price
        JLabel priceLabel = new JLabel("₹" + book.getPrice());
        priceLabel.setFont(new Font("Georgia", Font.BOLD, 13));
        priceLabel.setForeground(new Color(0, 120, 0));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Buy button
        JButton buyButton = new JButton("Buy");
        buyButton.setBackground(new Color(150, 90, 60));
        buyButton.setForeground(Color.WHITE);
        buyButton.setFocusPainted(false);
        buyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        card.add(imageLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(titleLabel);
        card.add(authorLabel);
        card.add(priceLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(buyButton);

        return card;
    }
}
