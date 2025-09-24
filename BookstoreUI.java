import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BookstoreUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(BookstoreUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Bookstore");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 700);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(0, 4, 20, 20)); // 4 columns
        mainPanel.setBackground(new Color(245, 245, 245));

        BookDAO dao = new BookDAO();
        List<Book> books = dao.getAllBooks();

        if (books.isEmpty()) {
            JLabel emptyLabel = new JLabel("No books found in the database!", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            frame.add(emptyLabel);
        } else {
            for (Book book : books) {
                mainPanel.add(createBookCard(book));
            }
            JScrollPane scrollPane = new JScrollPane(mainPanel);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            frame.add(scrollPane);
        }

        frame.setVisible(true);
    }

    private static JPanel createBookCard(Book book) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(220, 320));

        // Load image from book.getImagePath()
        JLabel imageLabel;
        if (book.getImagePath() != null && !book.getImagePath().isEmpty()) {
            ImageIcon icon = new ImageIcon(book.getImagePath());
            // Resize image to fit nicely
            Image img = icon.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH);
            imageLabel = new JLabel(new ImageIcon(img));
        } else {
            imageLabel = new JLabel("No Image", SwingConstants.CENTER);
            imageLabel.setOpaque(true);
            imageLabel.setBackground(new Color(220, 220, 220));
            imageLabel.setPreferredSize(new Dimension(150, 200));
        }
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("<html><center>" + book.getTitle() + "</center></html>");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel authorLabel = new JLabel(book.getAuthor());
        authorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        authorLabel.setForeground(new Color(90, 90, 90));
        authorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel priceLabel = new JLabel("₹" + book.getPrice());
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        priceLabel.setForeground(new Color(0, 150, 0));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton buyButton = new JButton("Buy");
        buyButton.setBackground(new Color(0, 120, 255));
        buyButton.setForeground(Color.WHITE);
        buyButton.setFocusPainted(false);
        buyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        card.add(imageLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(titleLabel);
        card.add(authorLabel);
        card.add(priceLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(buyButton);

        return card;
    }
}
