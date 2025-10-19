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
        orderStatusPanel = new OrderStatusPanel();
        initUI();
        refreshSubstoreBooks();
    }

    private void initUI() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // ---------- Cart Tab ----------
        JPanel cartAndBooksPanel = new JPanel(new BorderLayout());

        // Gradient title
        JLabel title = new JLabel("📚 Substore", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new GradientPaint(0, 0, new Color(70, 130, 180),
                                               getWidth(), getHeight(), new Color(135, 206, 250)));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setOpaque(false);
        title.setBorder(new EmptyBorder(10, 0, 10, 0));
        cartAndBooksPanel.add(title, BorderLayout.NORTH);

        // Books panel with WrapLayout
        booksPanel = new JPanel(new WrapLayout());
        JScrollPane booksScroll = new JScrollPane(booksPanel);
        booksScroll.setBorder(BorderFactory.createEmptyBorder());
        cartAndBooksPanel.add(booksScroll, BorderLayout.CENTER);

        // Cart panel
        JPanel cartPanel = new JPanel(new BorderLayout());
        String[] columns = {"Book", "Qty", "Price", "Total"};
        cartTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        cartTable = new JTable(cartTableModel);
        cartTable.setFillsViewportHeight(true);
        cartTable.setRowHeight(28);
        cartTable.setAutoCreateRowSorter(true);

        // Currency formatting
        cartTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 2 || column == 3) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    if (value instanceof Double || value instanceof Number) {
                        setText(String.format("₹%.2f", Double.parseDouble(value.toString())));
                    }
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                if (isSelected) {
                    c.setBackground(new Color(70, 130, 180));
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        });

        cartPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        // Cart controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalLabel = new JLabel("Total: ₹0.00");
        JButton placeOrder = createStyledButton("Place Order", new Color(46, 125, 50));
        JButton clear = createStyledButton("Clear Cart", new Color(244, 67, 54));
        JButton removeItem = createStyledButton("Remove Item", new Color(255, 152, 0));
        controls.add(removeItem);
        controls.add(totalLabel);
        controls.add(placeOrder);
        controls.add(clear);
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
        cartAndBooksPanel.add(splitPane, BorderLayout.CENTER);

        tabbedPane.addTab("Cart", cartAndBooksPanel);
        tabbedPane.addTab("Orders", orderStatusPanel);
        orderStatusPanel.refreshOrders();

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { button.setBackground(color.darker()); }
            public void mouseExited(java.awt.event.MouseEvent evt) { button.setBackground(color); }
        });
        return button;
    }

    private void refreshSubstoreBooks() {
        booksPanel.removeAll();
        List<Book> books = bookDAO.getBooksFromSubstores();
        for (Book b : books) {
            JPanel card = new JPanel(new BorderLayout());
            card.setPreferredSize(new Dimension(250, 140));
            card.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
            card.setBackground(Color.WHITE);

            JLabel title = new JLabel("<html><b>" + b.getTitle() + "</b></html>");
            JLabel author = new JLabel("By: " + b.getAuthor());
            JLabel price = new JLabel("₹" + b.getPrice());

            JButton add = createStyledButton("Add", new Color(76, 175, 80));
            add.addActionListener(e -> {
                substoreCart.addItem(b, 1);
                updateCartArea();
            });

            JPanel info = new JPanel(new GridLayout(3,1));
            info.add(title); info.add(author); info.add(price);

            card.add(info, BorderLayout.CENTER);
            card.add(add, BorderLayout.SOUTH);
            booksPanel.add(card);
        }
        booksPanel.revalidate();
        booksPanel.repaint();
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
            if (item.getQuantity() > 1) item.setQuantity(item.getQuantity() - 1);
            else substoreCart.getItems().remove(row);
            updateCartArea();
        } else {
            JOptionPane.showMessageDialog(this, "Select a row to remove.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void openPlaceOrderDialog() {
        if (substoreCart.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart empty", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(0,1));
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        panel.add(new JLabel("Customer Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Customer Phone (required):"));
        panel.add(phoneField);

        int res = JOptionPane.showConfirmDialog(this, panel, "Place Order", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            String phone = phoneField.getText().trim();
            if (phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Phone is required", "Error", JOptionPane.ERROR_MESSAGE);
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
                        JOptionPane.showMessageDialog(this, "Order placed! ID: " + id);
                        substoreCart.clear();
                        updateCartArea();
                        refreshSubstoreBooks();
                        orderStatusPanel.refreshOrders();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to save order", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }, nameField.getText().trim(), phone);
            pay.setVisible(true);
        }
    }
}
