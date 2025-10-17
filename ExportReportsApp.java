import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class ExportReportsApp extends JFrame {
    private JTextField bookField, quantityField;
    private JButton addBtn, exportExcelBtn, exportPdfBtn;
    private HashMap<String, Integer> salesMap = new HashMap<>();

    public ExportReportsApp() {
        setTitle("Export Reports Example");
        setSize(450, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // Input fields
        add(new JLabel("Book Name:"));
        bookField = new JTextField(20);
        add(bookField);

        add(new JLabel("Quantity Sold:"));
        quantityField = new JTextField(5);
        add(quantityField);

        // Buttons
        addBtn = new JButton("Add Sale");
        exportExcelBtn = new JButton("Export to Excel");
        exportPdfBtn = new JButton("Export to PDF");
        add(addBtn);
        add(exportExcelBtn);
        add(exportPdfBtn);

        // Button actions
        addBtn.addActionListener(e -> addSale());
        exportExcelBtn.addActionListener(e -> exportExcel());
        exportPdfBtn.addActionListener(e -> exportPDF());

        setVisible(true);
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
            document.add(new Paragraph("Sales Report\n\n"));

            for (Map.Entry<String, Integer> entry : salesMap.entrySet()) {
                document.add(new Paragraph("Book: " + entry.getKey() + " - Quantity Sold: " + entry.getValue()));
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
