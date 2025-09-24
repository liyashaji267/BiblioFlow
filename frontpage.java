// frontpage.java
import javax.swing.*;
import java.awt.*;

public class frontpage extends JFrame {
    
    public frontpage() {
        setTitle("BiblioFlow");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        initUI();
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        
        // Header with logo
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setPreferredSize(new Dimension(800, 150));
        
        JLabel logoLabel = new JLabel("📚 BIBLIOFLOW", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Serif", Font.BOLD, 48));
        logoLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Bookstore Management System", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitleLabel.setForeground(Color.WHITE);
        
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.add(Box.createVerticalGlue());
        headerPanel.add(logoLabel);
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createVerticalGlue());
        
        // Center content
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(240, 248, 255));
        
        JLabel welcomeLabel = new JLabel("Welcome to BiblioFlow");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(70, 130, 180));
        
        JLabel quoteLabel = new JLabel("Where Stories Flow Like Rivers");
        quoteLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        quoteLabel.setForeground(Color.DARK_GRAY);
        
        JButton startButton = new JButton("Start Application");
        startButton.setBackground(new Color(70, 130, 180));
        startButton.setForeground(Color.WHITE);
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.setPreferredSize(new Dimension(200, 50));
        startButton.addActionListener(e -> {
            openLoginWindow();
        });
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0;
        centerPanel.add(welcomeLabel, gbc);
        
        gbc.gridy = 1;
        centerPanel.add(quoteLabel, gbc);
        
        gbc.gridy = 2;
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)), gbc);
        
        gbc.gridy = 3;
        centerPanel.add(startButton, gbc);
        
        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(240, 248, 255));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel addressLabel = new JLabel("123 Book Street, Literature City | Phone: (555) 123-4567 | Email: info@biblioflow.com");
        addressLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        addressLabel.setForeground(Color.DARK_GRAY);
        
        footerPanel.add(addressLabel);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void openLoginWindow() {
    SwingUtilities.invokeLater(() -> {
        BookstoreLogin loginWindow = new BookstoreLogin();
        loginWindow.setVisible(true);
        this.dispose();
    });
}
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new frontpage().setVisible(true);
        });
    }
}