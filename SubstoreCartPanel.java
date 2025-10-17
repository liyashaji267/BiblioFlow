import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.mysql.cj.x.protobuf.MysqlxCrud.Order;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

// ====================== CartItem ======================
class CartItem {
    private Book book;
    private int quantity; // quantity in this cart

    public CartItem(Book book, int quantity) {
        this.book = book;
        this.quantity = quantity;
    }

    public Book getBook() { return book; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        book.setCartQuantity(quantity); // sync with book
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
        book.setCartQuantity(quantity); // update book's cart quantity
        items.add(newItem);
    }

    public List<CartItem> getItems() { return items; }

    public void clear() {
        for (CartItem item : items) {
            item.getBook().setCartQuantity(0); // reset book cart quantity
        }
        items.clear();
    }

    public double getTotalAmount() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getTotalPrice();
        }
        return total;
    }
}

// ====================== SubstoreCartPanel ======================
public class SubstoreCartPanel extends JPanel {
    private SubstoreCart substoreCart = new SubstoreCart();
    private JPanel cartPanel;
    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private JLabel totalLabel;
    private BookDAO bookDAO = new BookDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private OrderStatusPanel orderStatusPanel;
    private JPanel booksPanel;


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

    // Title
    JLabel title = new JLabel("Substore", SwingConstants.CENTER);
    title.setFont(new Font("Arial", Font.BOLD, 24));
    cartAndBooksPanel.add(title, BorderLayout.NORTH);

    // Books panel
    booksPanel = new JPanel(new WrapLayout());
    JScrollPane booksScroll = new JScrollPane(booksPanel);
    booksScroll.setName("substoreBooksScroll");
    cartAndBooksPanel.add(booksScroll, BorderLayout.CENTER);

     // Cart panel
    cartPanel = new JPanel(new BorderLayout());

    // Columns: Book, Qty, Price, Total
    String[] columns = {"Book", "Qty", "Price", "Total"};
    cartTableModel = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // make table read-only
        }
    };
    cartTable = new JTable(cartTableModel);
    cartTable.setFillsViewportHeight(true);

    cartPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

// Controls (Total, Buttons)
JPanel controls = new JPanel(new FlowLayout());
totalLabel = new JLabel("Total: ₹0.00");
JButton placeOrder = new JButton("Place Order");
JButton clear = new JButton("Clear Cart");
JButton removeItem = new JButton("Remove Item");
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

    tabbedPane.addTab("Cart", cartAndBooksPanel);
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, booksScroll, cartPanel);
    splitPane.setDividerLocation(600); // initial width for books
    splitPane.setResizeWeight(0.7);    // proportion for left component
    cartAndBooksPanel.add(splitPane, BorderLayout.CENTER);

removeItem.addActionListener(e -> {
    int selectedRow = cartTable.getSelectedRow();
    if (selectedRow >= 0 && selectedRow < substoreCart.getItems().size()) {
        CartItem item = substoreCart.getItems().get(selectedRow);

        // Remove CartItem by index from SubstoreCart
        if (item.getQuantity() > 1) {
            // Reduce quantity by 1
            item.setQuantity(item.getQuantity() - 1);
        } else {
            // Quantity is 1, remove item completely
            substoreCart.getItems().remove(selectedRow);
        }
        // Refresh table and total
        updateCartArea();
    } else {
        JOptionPane.showMessageDialog(this, "Select a row to remove.", "Warning", JOptionPane.WARNING_MESSAGE);
    }
});


    // ---------- Orders Tab ----------
    tabbedPane.addTab("Orders", orderStatusPanel);
    orderStatusPanel.refreshOrders();

    add(tabbedPane, BorderLayout.CENTER);
}


    private void refreshSubstoreBooks() {
    booksPanel.removeAll();
    List<Book> books = bookDAO.getBooksFromSubstores();

    for (Book b : books) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(250, 120));
        JLabel title = new JLabel("<html><b>"+b.getTitle()+"</b></html>");
        JLabel price = new JLabel("₹"+b.getPrice());
        JButton add = new JButton("Add");

        add.addActionListener(ae -> {
            substoreCart.addItem(b, 1);
            updateCartArea();
        });

        JPanel info = new JPanel(new GridLayout(3,1));
        info.add(title);
        info.add(new JLabel("By: "+b.getAuthor()));
        info.add(price);

        card.add(info, BorderLayout.CENTER);
        card.add(add, BorderLayout.SOUTH);
        booksPanel.add(card);
    }
    booksPanel.revalidate();
    booksPanel.repaint();
}

    public void updateCartArea() {
        // Clear existing rows
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


    public void openPlaceOrderDialog() {
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
                JOptionPane.showMessageDialog(this, "Phone is required for substore order", "Error", JOptionPane.ERROR_MESSAGE);
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
                orderStatusPanel.refreshOrders();

                StringBuilder items = new StringBuilder();
                for (CartItem item : substoreCart.getItems()) {
                    items.append(item.getBook().getTitle())
                         .append(" x")
                         .append(item.getQuantity())
                         .append(";");
                }
                order.setItemsSummary(items.toString());

                try {
                    int id = orderDAO.saveOrder(order);
                    if (id > 0) {
                        for (CartItem item : substoreCart.getItems()) {
                            bookDAO.reduceStock(item.getBook().getId(), item.getQuantity());
                        }
                        JOptionPane.showMessageDialog(this, "Order placed! Order ID: " + id);
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
    public SubstoreCart getSubstoreCart() {
        return substoreCart;
    }

}
