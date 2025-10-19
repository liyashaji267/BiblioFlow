import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderStatusPanel extends JPanel {
    private JTable orderTable;
    private JButton btnProcess, btnDeliver;
    private OrderDAO orderDAO;
    private List<SubstoreOrder> orders;

    public OrderStatusPanel() {
        orderDAO = new OrderDAO();
        setLayout(new BorderLayout());
        setBackground(new Color(250, 240, 230)); // light warm background

        // TABLE SETUP
        orderTable = new JTable();
        orderTable.setRowHeight(28);
        orderTable.setFont(new Font("Serif", Font.PLAIN, 16));
        orderTable.getTableHeader().setFont(new Font("Georgia", Font.BOLD, 16));
        orderTable.getTableHeader().setBackground(new Color(150, 90, 60));
        orderTable.getTableHeader().setForeground(Color.WHITE);

        // Center align text in all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        orderTable.setDefaultRenderer(Object.class, centerRenderer);

        refresh();

        // BUTTONS
        btnProcess = createStyledButton("Process");
        btnDeliver = createStyledButton("Deliver");

        btnProcess.addActionListener(e -> processOrder());
        btnDeliver.addActionListener(e -> deliverOrder());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(230, 200, 180)); // soft panel background
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(btnProcess);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(btnDeliver);

        add(new JScrollPane(orderTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Georgia", Font.BOLD, 18));
        button.setBackground(new Color(150, 90, 60));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 45));
        button.setMaximumSize(new Dimension(140, 45));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(120, 70, 50));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(150, 90, 60));
            }
        });
        return button;
    }

    private void refresh() {
        try {
            orders = orderDAO.getAllOrders();
            String[] columnNames = {"Order Number", "Customer", "Phone", "Total", "Status", "Created At"};
            Object[][] data = new Object[orders.size()][columnNames.length];

            for (int i = 0; i < orders.size(); i++) {
                SubstoreOrder o = orders.get(i);
                data[i][0] = o.getOrderNumber();
                data[i][1] = o.getCustomerName();
                data[i][2] = o.getCustomerPhone();
                data[i][3] = "₹" + String.format("%.2f", o.getTotalAmount());
                data[i][4] = o.getOrderStatus();
                data[i][5] = o.getCreatedAt() > 0 ? new java.util.Date(o.getCreatedAt()).toString() : "";
            }

            orderTable.setModel(new DefaultTableModel(data, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading orders: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void processOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an order to process", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SubstoreOrder selectedOrder = orders.get(selectedRow);
        if (!"Placed".equalsIgnoreCase(selectedOrder.getOrderStatus())) {
            JOptionPane.showMessageDialog(this, "Only 'Placed' orders can be processed", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Mark order " + selectedOrder.getOrderNumber() + " as 'Processing'?",
                "Confirm Processing", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                orderDAO.updateOrderStatus(selectedOrder.getId(), "Processing");
                JOptionPane.showMessageDialog(this, "Order marked as Processing");
                refresh();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error updating order: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void deliverOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an order to deliver", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SubstoreOrder selectedOrder = orders.get(selectedRow);
        if (!"Processing".equalsIgnoreCase(selectedOrder.getOrderStatus())) {
            JOptionPane.showMessageDialog(this, "Only 'Processing' orders can be delivered", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Initiate delivery for order: " + selectedOrder.getOrderNumber() +
                        "\nCustomer: " + selectedOrder.getCustomerName() +
                        "\nPhone: " + selectedOrder.getCustomerPhone() +
                        "\nAmount: ₹" + String.format("%.2f", selectedOrder.getTotalAmount()),
                "Confirm Delivery", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int orderId = selectedOrder.getId();
            String otp = generateOtp();

            try {
                if (!orderDAO.saveOrderOtp(orderId, otp)) {
                    throw new Exception("Failed to save OTP to database");
                }

                String msg = "BiblioFlow Delivery Alert:\n" +
                        "Order: " + selectedOrder.getOrderNumber() + "\n" +
                        "Items: " + (selectedOrder.getItemsSummary() != null ? selectedOrder.getItemsSummary() : "") + "\n" +
                        "Delivery OTP: " + otp + "\n" +
                        "Please provide this OTP to the delivery person.";

                TwilioSMS.sendSms(selectedOrder.getCustomerPhone(), msg);

                String typedOtp = JOptionPane.showInputDialog(this,
                        "OTP sent to " + selectedOrder.getCustomerPhone() +
                                "\nEnter OTP for delivery confirmation:");

                if (typedOtp != null && !typedOtp.trim().isEmpty()) {
                    if (orderDAO.verifyOtp(orderId, typedOtp.trim())) {
                        orderDAO.updateOrderStatus(orderId, "Delivered");
                        JOptionPane.showMessageDialog(this,
                                "Delivery confirmed! Order status updated to Delivered.");
                        refresh();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Invalid OTP! Delivery not confirmed.",
                                "OTP Mismatch", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "OTP verification cancelled.",
                            "Cancelled", JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Delivery failed: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private String generateOtp() {
        int otp = 100000 + new java.util.Random().nextInt(900000);
        return String.valueOf(otp);
    }

    public void refreshOrders() {
        refresh();
    }
}
