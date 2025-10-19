import javax.swing.*;
import java.awt.*;

public class frontpage extends JFrame {

    public frontpage() {
        setTitle("BiblioFlow");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        // ===========================================================
        // MAIN BACKGROUND PANEL WITH GRADIENT
        // ===========================================================
        JPanel mainPanel = new JPanel(new BorderLayout()) {
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

        // ===========================================================
        // HEADER (LOGO + TITLE + SUBTITLE) INLINE
        // ===========================================================
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));

        // LOGO
        ImageIcon logoIcon = new ImageIcon("D:\\java project new\\JAVA Project\\BiblioFlow\\imgs\\logo.png");
        Image scaledLogo = logoIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        logoLabel.setAlignmentY(Component.CENTER_ALIGNMENT);

        // SMALL SPACE
        headerPanel.add(logoLabel);
        headerPanel.add(Box.createHorizontalStrut(15));

        // TITLE + SUBTITLE
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setAlignmentY(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("BiblioFlow");
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 48));
        titleLabel.setForeground(new Color(80, 50, 40));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Bookstore Management System");
        subtitleLabel.setFont(new Font("Serif", Font.ITALIC, 20));
        subtitleLabel.setForeground(new Color(100, 70, 60));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        headerPanel.add(textPanel);

        // ===========================================================
        // CENTER CONTENT (WELCOME + QUOTE + BUTTON)
        // ===========================================================
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel welcomeLabel = new JLabel("Welcome !!");
        welcomeLabel.setFont(new Font("Times New Roman", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(90, 60, 50));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel quoteLabel = new JLabel("“Where every book tells a new story...”");
        quoteLabel.setFont(new Font("Palatino Linotype", Font.ITALIC, 20));
        quoteLabel.setForeground(new Color(110, 80, 70));
        quoteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        quoteLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // BUTTON
        JButton startButton = new JButton("Start Application");
        startButton.setFont(new Font("Georgia", Font.BOLD, 20));
        startButton.setBackground(new Color(150, 90, 60));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setPreferredSize(new Dimension(250, 55));
        startButton.setMaximumSize(new Dimension(250, 55));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        startButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Hover effect
        startButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                startButton.setBackground(new Color(120, 70, 50));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                startButton.setBackground(new Color(150, 90, 60));
            }
        });

        startButton.addActionListener(e -> openLoginWindow());

        // Add center elements
        centerPanel.add(welcomeLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(quoteLabel);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(startButton);

        // ===========================================================
        // FOOTER
        // ===========================================================
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(205, 155, 125));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JLabel addressLabel = new JLabel(
                "123 Book Street, Literature City | Phone: (555) 123-4567 | Email: info@biblioflow.com",
                SwingConstants.CENTER);
        addressLabel.setFont(new Font("Century Schoolbook", Font.PLAIN, 13));
        addressLabel.setForeground(Color.WHITE);

        footerPanel.add(addressLabel, BorderLayout.CENTER);

        // ===========================================================
        // ADD ALL SECTIONS TO MAIN PANEL
        // ===========================================================
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
        SwingUtilities.invokeLater(() -> new frontpage().setVisible(true));
    }
}
