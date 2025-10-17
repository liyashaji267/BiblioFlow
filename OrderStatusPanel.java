import javax.swing.*;
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

        orderTable = new JTable();
        refresh();

        btnProcess = new JButton("Process");
        btnDeliver = new JButton("Deliver");

        btnProcess.addActionListener(e -> processOrder());
        btnDeliver.addActionListener(e -> deliverOrder());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnProcess);
        buttonPanel.add(btnDeliver);

        add(new JScrollPane(orderTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
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
                // Save OTP to database (also sets otp_generated_at)
                if (!orderDAO.saveOrderOtp(orderId, otp)) {
                    throw new Exception("Failed to save OTP to database");
                }

                // SMS content (simulate)
                String msg = "BiblioFlow Delivery Alert:\n" +
                        "Order: " + selectedOrder.getOrderNumber() + "\n" +
                        "Items: " + (selectedOrder.getItemsSummary() != null ? selectedOrder.getItemsSummary() : "") + "\n" +
                        "Delivery OTP: " + otp + "\n" +
                        "Please provide this OTP to the delivery person.";

                // Send SMS (simulation - print to console). Replace with real provider as needed.
                TwilioSMS.sendSms(selectedOrder.getCustomerPhone(), msg);

                // Prompt for OTP verification
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

    private void sendSms(String toPhone, String message) throws Exception {
    // Show OTP/message in a pop-up for testing
    JOptionPane.showMessageDialog(this,
            "=== SIMULATED SMS ===\nTo: " + toPhone + "\n\n" + message,
            "SMS Simulation",
            JOptionPane.INFORMATION_MESSAGE);

    // Optional: still print to console (for logging)
    System.out.println("=== SMS SIMULATION ===");
    System.out.println("To: " + toPhone);
    System.out.println("Message: " + message);
    System.out.println("=== SMS SENT SUCCESSFULLY ===");
}


    // Public method to call externally to refresh orders
    public void refreshOrders() {
        refresh();
    }
}
