import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.io.FileOutputStream;
import java.util.*;
// Apache POI imports
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
// iText PDF imports
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class ExportReportsApp extends JFrame {
    private JTextField bookField, quantityField;
    private JButton addBtn, exportExcelBtn, exportPdfBtn;
    private JTable salesTable;
    private DefaultTableModel tableModel;
    private HashMap<String, Integer> salesMap = new HashMap<>();

    public ExportReportsApp() {
        setTitle("Export Reports");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Header
        JLabel header = new JLabel("Sales Report Exporter", SwingConstants.CENTER);
        header.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        header.setForeground(new java.awt.Color(33, 150, 243));
        add(header, BorderLayout.NORTH);

        // Input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add Sale"));
        inputPanel.setBackground(java.awt.Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Book Name:"), gbc);
        gbc.gridx = 1;
        bookField = new JTextField(20);
        inputPanel.add(bookField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Quantity Sold:"), gbc);
        gbc.gridx = 1;
        quantityField = new JTextField(5);
        inputPanel.add(quantityField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        addBtn = new JButton("Add Sale");
        styleButton(addBtn, new java.awt.Color(76, 175, 80));
        inputPanel.add(addBtn, gbc);

        add(inputPanel, BorderLayout.WEST);

        // Table panel
        String[] columns = {"Book Name", "Quantity Sold"};
        tableModel = new DefaultTableModel(columns, 0);
        salesTable = new JTable(tableModel);
        salesTable.setFillsViewportHeight(true);
        salesTable.setRowHeight(25);
        JScrollPane tableScroll = new JScrollPane(salesTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Current Sales"));
        add(tableScroll, BorderLayout.CENTER);

        // Export buttons panel
        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        exportPanel.setBackground(java.awt.Color.WHITE);
        exportExcelBtn = new JButton("Export to Excel");
        exportPdfBtn = new JButton("Export to PDF");
        styleButton(exportExcelBtn, new java.awt.Color(33, 150, 243));
        styleButton(exportPdfBtn, new java.awt.Color(255, 193, 7));
        exportPanel.add(exportExcelBtn);
        exportPanel.add(exportPdfBtn);
        add(exportPanel, BorderLayout.SOUTH);

        // Action listeners
        addBtn.addActionListener(e -> addSale());
        exportExcelBtn.addActionListener(e -> exportExcel());
        exportPdfBtn.addActionListener(e -> exportPDF());

        getContentPane().setBackground(java.awt.Color.WHITE);
        setVisible(true);
    }

    private void styleButton(JButton button, java.awt.Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(java.awt.Color.WHITE);
        button.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    private void addSale() {
        String book = bookField.getText().trim();
        if (book.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter book name!");
            return;
        }
        try {
            int qty = Integer.parseInt(quantityField.getText().trim());
            salesMap.put(book, salesMap.getOrDefault(book, 0) + qty);

            // Update table
            boolean found = false;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).equals(book)) {
                    tableModel.setValueAt(salesMap.get(book), i, 1);
                    found = true;
                    break;
                }
            }
            if (!found) {
                tableModel.addRow(new Object[]{book, qty});
            }

            JOptionPane.showMessageDialog(this, "Sale added!");
            bookField.setText("");
            quantityField.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter a valid quantity!");
        }
    }

    private void exportExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sales Report");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Book Name");
            header.createCell(1).setCellValue("Quantity Sold");

            int rowNum = 1;
            for (Map.Entry<String, Integer> entry : salesMap.entrySet()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

            FileOutputStream fileOut = new FileOutputStream("SalesReport.xlsx");
            workbook.write(fileOut);
            fileOut.close();
            JOptionPane.showMessageDialog(this, "Excel report exported successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting Excel: " + e.getMessage());
        }
    }

    private void exportPDF() {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("SalesReport.pdf"));
            document.open();
            
            // Use iText Font with fully qualified name
            com.itextpdf.text.Font pdfFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.NORMAL);
            document.add(new Paragraph("Sales Report\n\n", pdfFont));

            for (Map.Entry<String, Integer> entry : salesMap.entrySet()) {
                document.add(new Paragraph("Book: " + entry.getKey() + " - Quantity Sold: " + entry.getValue(), pdfFont));
            }

            document.close();
            JOptionPane.showMessageDialog(this, "PDF report exported successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting PDF: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new ExportReportsApp();
    }
}