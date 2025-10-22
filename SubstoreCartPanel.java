import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

// ====================== CartItem ======================
class CartItem {
    private Book book;
    private int quantity;

    public CartItem(Book book, int quantity) {
        this.book = book;
        this.quantity = quantity;
    }

    public Book getBook() { return book; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        book.setCartQuantity(quantity);
    }

    public double getTotalPrice() {
        return book.getPrice() * quantity;
    }

}

// ====================== SubstoreCart ======================
class SubstoreCart {
    private List<CartItem> items = new ArrayList<>();

    public void addItem(Book book, int quantity) {
        for (CartItem item : items) {
            if (item.getBook().getId() == book.getId()) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        CartItem newItem = new CartItem(book, quantity);
        book.setCartQuantity(quantity);
        items.add(newItem);
    }

    public List<CartItem> getItems() { return items; }

    public void clear() {
        for (CartItem item : items) {
            item.getBook().setCartQuantity(0);
        }
        items.clear();
    }

    public double getTotalAmount() {
        return items.stream().mapToDouble(CartItem::getTotalPrice).sum();
    }
}

// ====================== SubstoreCartPanel ======================
public class SubstoreCartPanel extends JPanel {
    private SubstoreCart substoreCart = new SubstoreCart();
    private JPanel booksPanel;
    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private JLabel totalLabel;

    private BookDAO bookDAO = new BookDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private OrderStatusPanel orderStatusPanel;

    public SubstoreCartPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(250, 240, 230)); // Frontpage background
        orderStatusPanel = new OrderStatusPanel();
        initUI();
        refreshSubstoreBooks();
    }

    private void initUI() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(250, 240, 230));
        tabbedPane.setForeground(new Color(80, 50, 40));

        // ---------- Cart Tab ----------
        JPanel cartAndBooksPanel = new JPanel(new BorderLayout());
        cartAndBooksPanel.setBackground(new Color(250, 240, 230));

        // Header with frontpage styling
        JLabel title = new JLabel("🏪 Substore Book Lookup", SwingConstants.CENTER);
        title.setFont(new Font("Georgia", Font.BOLD, 32));
        title.setForeground(new Color(80, 50, 40)); // Frontpage dark brown
        title.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(150, 90, 60)),
            BorderFactory.createEmptyBorder(20, 0, 20, 0)
        ));
        cartAndBooksPanel.add(title, BorderLayout.NORTH);

        // Books panel with WrapLayout
        booksPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 20, 20));
        booksPanel.setBackground(new Color(250, 240, 230));
        JScrollPane booksScroll = new JScrollPane(booksPanel);
        booksScroll.setBorder(BorderFactory.createEmptyBorder());
        booksScroll.getViewport().setBackground(new Color(250, 240, 230));
        
        // Style scrollbar
        JScrollBar verticalScrollBar = booksScroll.getVerticalScrollBar();
        verticalScrollBar.setBackground(new Color(230, 200, 180));
        verticalScrollBar.setBorder(BorderFactory.createLineBorder(new Color(150, 90, 60)));

        // Cart panel
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBackground(new Color(250, 240, 230));
        cartPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Cart table
        String[] columns = {"Book", "Qty", "Price", "Total"};
        cartTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        cartTable = new JTable(cartTableModel);
        cartTable.setFillsViewportHeight(true);
        cartTable.setRowHeight(35);
        cartTable.setFont(new Font("Georgia", Font.PLAIN, 13));
        cartTable.setBackground(new Color(255, 250, 245));
        cartTable.setForeground(new Color(80, 50, 40));
        cartTable.setGridColor(new Color(200, 180, 160));
        cartTable.setSelectionBackground(new Color(150, 90, 60));
        cartTable.setSelectionForeground(Color.WHITE);

        // Table header styling
        JTableHeader header = cartTable.getTableHeader();
        header.setBackground(new Color(150, 90, 60));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Georgia", Font.BOLD, 14));

        // Currency formatting
        cartTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(new Font("Georgia", Font.PLAIN, 13));
                
                if (column == 2 || column == 3) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    if (value instanceof Double || (value instanceof String && ((String)value).contains("₹"))) {
                        setText(String.format("₹%.2f", Double.parseDouble(value.toString().replace("₹", ""))));
                    }
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                
                if (isSelected) {
                    c.setBackground(new Color(150, 90, 60));
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(row % 2 == 0 ? new Color(255, 250, 245) : new Color(250, 245, 240));
                    c.setForeground(new Color(80, 50, 40));
                }
                return c;
            }
        });

        JScrollPane tableScroll = new JScrollPane(cartTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(150, 90, 60), 2),
            "Shopping Cart",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Georgia", Font.BOLD, 16),
            new Color(80, 50, 40)
        ));
        cartPanel.add(tableScroll, BorderLayout.CENTER);

        // Cart controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        controls.setBackground(new Color(250, 240, 230));
        controls.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        
        totalLabel = new JLabel("Total: ₹0.00");
        totalLabel.setFont(new Font("Georgia", Font.BOLD, 18));
        totalLabel.setForeground(new Color(150, 90, 60));
        
        JButton removeItem = createStyledButton("Remove Item", new Color(130, 80, 50));
        JButton clear = createStyledButton("Clear Cart", new Color(120, 70, 50));
        JButton placeOrder = createStyledButton("Place Order", new Color(150, 90, 60));

        controls.add(removeItem);
        controls.add(totalLabel);
        controls.add(clear);
        controls.add(placeOrder);
        cartPanel.add(controls, BorderLayout.SOUTH);

        // Actions
        placeOrder.addActionListener(e -> openPlaceOrderDialog());
        clear.addActionListener(e -> {
            substoreCart.clear();
            updateCartArea();
        });
        removeItem.addActionListener(e -> removeSelectedItem());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, booksScroll, cartPanel);
        splitPane.setDividerLocation(600);
        splitPane.setResizeWeight(0.7);
        splitPane.setBackground(new Color(250, 240, 230));
        cartAndBooksPanel.add(splitPane, BorderLayout.CENTER);

        tabbedPane.addTab("📚 Browse Books", cartAndBooksPanel);
        tabbedPane.addTab("📋 Order Status", orderStatusPanel);
        orderStatusPanel.refreshOrders();

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Georgia", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker()),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
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

    private void refreshSubstoreBooks() {
        booksPanel.removeAll();
        List<Book> books = bookDAO.getBooksFromSubstores();
        
        if (books.isEmpty()) {
            JLabel emptyLabel = new JLabel("No books available in substores", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Georgia", Font.ITALIC, 16));
            emptyLabel.setForeground(new Color(80, 50, 40));
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
            booksPanel.add(emptyLabel);
        } else {
            for (Book b : books) {
                booksPanel.add(createBookCard(b));
            }
        }
        
        booksPanel.revalidate();
        booksPanel.repaint();
    }

    private JPanel createBookCard(Book book) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setPreferredSize(new Dimension(280, 160));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 90, 60), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(new Color(255, 250, 245));

        // Book title
        JLabel title = new JLabel("<html><div style='width:230px;'><b>" + book.getTitle() + "</b></div></html>");
        title.setFont(new Font("Georgia", Font.BOLD, 14));
        title.setForeground(new Color(80, 50, 40));

        // Book details
        JLabel author = new JLabel("By: " + book.getAuthor());
        author.setFont(new Font("Georgia", Font.PLAIN, 12));
        author.setForeground(new Color(80, 50, 40));

        JLabel store = new JLabel("Store: " + book.getLocation());
        store.setFont(new Font("Georgia", Font.PLAIN, 12));
        store.setForeground(new Color(80, 50, 40));

        JLabel stock = new JLabel("Stock: " + book.getStockQuantity());
        stock.setFont(new Font("Georgia", Font.PLAIN, 12));
        stock.setForeground(new Color(80, 50, 40));

        JLabel price = new JLabel("₹" + book.getPrice());
        price.setFont(new Font("Georgia", Font.BOLD, 14));
        price.setForeground(new Color(150, 90, 60));

        // Info panel
        JPanel info = new JPanel(new GridLayout(5, 1, 3, 3));
        info.setBackground(new Color(255, 250, 245));
        info.add(title);
        info.add(author);
        info.add(store);
        info.add(stock);
        info.add(price);

        // Add button
        JButton add = createStyledButton("Add to Cart", new Color(150, 90, 60));
        add.setFont(new Font("Georgia", Font.BOLD, 12));
        add.addActionListener(e -> {
            substoreCart.addItem(book, 1);
            updateCartArea();
            JOptionPane.showMessageDialog(this, 
                "Added '" + book.getTitle() + "' to cart!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        });

        card.add(info, BorderLayout.CENTER);
        card.add(add, BorderLayout.SOUTH);
        
        return card;
    }

    private void updateCartArea() {
        cartTableModel.setRowCount(0);
        double total = 0;
        for (CartItem item : substoreCart.getItems()) {
            double itemTotal = item.getTotalPrice();
            total += itemTotal;
            cartTableModel.addRow(new Object[]{
                item.getBook().getTitle(),
                item.getQuantity(),
                item.getBook().getPrice(),
                itemTotal
            });
        }
        totalLabel.setText(String.format("Total: ₹%.2f", total));
    }

    private void removeSelectedItem() {
        int row = cartTable.getSelectedRow();
        if (row >= 0 && row < substoreCart.getItems().size()) {
            CartItem item = substoreCart.getItems().get(row);
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
            } else {
                substoreCart.getItems().remove(row);
            }
            updateCartArea();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select an item to remove from the cart.", 
                "Warning", 
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void openPlaceOrderDialog() {
        if (substoreCart.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Your cart is empty. Please add some books before placing an order.", 
                "Cart Empty", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBackground(new Color(250, 240, 230));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Place Substore Order");
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 18));
        titleLabel.setForeground(new Color(80, 50, 40));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JTextField nameField = new JTextField();
        nameField.setFont(new Font("Georgia", Font.PLAIN, 14));
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 90, 60)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));

        JTextField phoneField = new JTextField();
        phoneField.setFont(new Font("Georgia", Font.PLAIN, 14));
        phoneField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 90, 60)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));

        panel.add(titleLabel);
        panel.add(new JLabel("Customer Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Customer Phone (required):"));
        panel.add(phoneField);

        int res = JOptionPane.showConfirmDialog(this, panel, "Place Order", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (res == JOptionPane.OK_OPTION) {
            String phone = phoneField.getText().trim();
            if (phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Phone number is required for order confirmation.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double total = substoreCart.getTotalAmount();
            SubstorePaymentOpt pay = new SubstorePaymentOpt(total, () -> {
                SubstoreOrder order = new SubstoreOrder();
                order.setOrderNumber("SORD" + System.currentTimeMillis());
                order.setCustomerName(nameField.getText().trim());
                order.setCustomerPhone(phone);
                order.setTotalAmount(total);
                order.setOrderStatus("Placed");

                StringBuilder items = new StringBuilder();
                for (CartItem item : substoreCart.getItems()) {
                    items.append(item.getBook().getTitle())
                         .append(" x").append(item.getQuantity()).append("; ");
                }
                order.setItemsSummary(items.toString());

                try {
                    int id = orderDAO.saveOrder(order);
                    if (id > 0) {
                        for (CartItem item : substoreCart.getItems()) {
                            bookDAO.reduceStock(item.getBook().getId(), item.getQuantity());
                        }
                        JOptionPane.showMessageDialog(this, 
                            "Order placed successfully! Order ID: " + id, 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                        substoreCart.clear();
                        updateCartArea();
                        refreshSubstoreBooks();
                        orderStatusPanel.refreshOrders();
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Failed to save order. Please try again.", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, 
                        "Database error: " + ex.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }, nameField.getText().trim(), phone);
            pay.setVisible(true);
        }
    }
}