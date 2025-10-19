import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BookstoreLogin extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserDAO userDAO;

    public BookstoreLogin() {
        this.userDAO = new UserDAO();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("BiblioFlow - Login");
        setSize(450, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // ===========================================================
        // MAIN PANEL WITH GRADIENT LIKE FRONT PAGE
        // ===========================================================
        JPanel mainPanel = new JPanel() {
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
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // ===========================================================
        // HEADER PANEL (LOGO IMAGE + TITLE)
        // ===========================================================
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Load logo image
        ImageIcon logoIcon = new ImageIcon("D:\\java project new\\JAVA Project\\BiblioFlow\\imgs\\logo.png");
        Image scaledLogo = logoIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        logoIcon = new ImageIcon(scaledLogo);

        // JLabel with text and icon
        JLabel logoLabel = new JLabel("BIBLIOFLOW", logoIcon, JLabel.LEFT);
        logoLabel.setFont(new Font("Georgia", Font.BOLD, 32));
        logoLabel.setForeground(new Color(80, 50, 40));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setIconTextGap(15); // Space between icon and text

        // Subtitle
        JLabel subtitleLabel = new JLabel("Bookstore Management System", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Serif", Font.ITALIC, 16));
        subtitleLabel.setForeground(new Color(100, 70, 60));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add to header panel
        headerPanel.add(logoLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // ===========================================================
        // LOGIN FORM PANEL (ROUNDED + LIGHT BACKGROUND)
        // ===========================================================
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setOpaque(false);
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 100, 70), 2, true),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        loginPanel.setBackground(new Color(255, 250, 240, 180));

        JLabel loginTitle = new JLabel("Login to Your Account", SwingConstants.CENTER);
        loginTitle.setFont(new Font("Georgia", Font.BOLD, 20));
        loginTitle.setForeground(new Color(80, 50, 40));
        loginTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username
        JPanel usernamePanel = new JPanel(new BorderLayout(5, 5));
        usernamePanel.setOpaque(false);
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Serif", Font.BOLD, 14));
        usernameLabel.setForeground(new Color(80, 50, 40));
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(250, 35));
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(150, 90, 60), 2, true));
        usernamePanel.add(usernameLabel, BorderLayout.NORTH);
        usernamePanel.add(usernameField, BorderLayout.CENTER);

        // Password
        JPanel passwordPanel = new JPanel(new BorderLayout(5, 5));
        passwordPanel.setOpaque(false);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Serif", Font.BOLD, 14));
        passwordLabel.setForeground(new Color(80, 50, 40));
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(250, 35));
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(150, 90, 60), 2, true));
        passwordPanel.add(passwordLabel, BorderLayout.NORTH);
        passwordPanel.add(passwordField, BorderLayout.CENTER);

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(150, 90, 60));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Georgia", Font.BOLD, 16));
        loginButton.setFocusPainted(false);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setPreferredSize(new Dimension(250, 40));
        loginButton.addActionListener(new LoginButtonListener());

        // Signup
        JPanel signupPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        signupPanel.setOpaque(false);
        JLabel signupLabel = new JLabel("Don't have an account?");
        signupLabel.setForeground(new Color(80, 50, 40));
        JButton signupButton = new JButton("Sign Up");
        signupButton.setBorderPainted(false);
        signupButton.setContentAreaFilled(false);
        signupButton.setForeground(new Color(150, 90, 60));
        signupButton.setFont(new Font("Serif", Font.BOLD, 14));
        signupButton.addActionListener(e -> openSignupDialog());
        signupPanel.add(signupLabel);
        signupPanel.add(signupButton);

        // Exit
        JButton exitButton = new JButton("Exit");
        exitButton.setBackground(new Color(200, 50, 50));
        exitButton.setForeground(Color.WHITE);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.addActionListener(e -> System.exit(0));

        // Add to login panel
        loginPanel.add(loginTitle);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        loginPanel.add(usernamePanel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        loginPanel.add(passwordPanel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        loginPanel.add(loginButton);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        loginPanel.add(signupPanel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        loginPanel.add(exitButton);

        // Add header + login panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(loginPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Enter key listener
        getRootPane().setDefaultButton(loginButton);
    }

    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(BookstoreLogin.this,
                        "Please enter both username and password!",
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = userDAO.authenticateUser(username, password);
            if (user != null) {
                JOptionPane.showMessageDialog(BookstoreLogin.this,
                        "Login successful! Welcome, " + user.getUsername() + "!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                SwingUtilities.invokeLater(() -> {
                    MainApplication mainApp = new MainApplication(user);
                    mainApp.setVisible(true);
                    dispose();
                });
            } else {
                JOptionPane.showMessageDialog(BookstoreLogin.this,
                        "Invalid username or password!",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }
        }
    }

    private void openSignupDialog() {
        JDialog signupDialog = new JDialog(this, "Create New Account", true);
        signupDialog.setSize(450, 450);
        signupDialog.setLocationRelativeTo(this);
        signupDialog.setResizable(false);

        JPanel mainPanel = new JPanel() {
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
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Create Your Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 24));
        titleLabel.setForeground(new Color(80, 50, 40));

        JPanel formPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        formPanel.setOpaque(false);

        String[] labels = {"Username:", "Password:", "Confirm Password:"};
        JTextField[] fields = new JTextField[labels.length];

        for (int i = 0; i < labels.length; i++) {
            JPanel fieldPanel = new JPanel(new BorderLayout(5, 5));
            fieldPanel.setOpaque(false);

            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Serif", Font.BOLD, 14));
            label.setForeground(new Color(80, 50, 40));

            if (i == 1 || i == 2) {
                fields[i] = new JPasswordField();
                fields[i].setBorder(BorderFactory.createLineBorder(new Color(150, 90, 60), 2, true));
            } else {
                fields[i] = new JTextField();
                fields[i].setBorder(BorderFactory.createLineBorder(new Color(150, 90, 60), 2, true));
            }
            fields[i].setPreferredSize(new Dimension(300, 35));

            fieldPanel.add(label, BorderLayout.NORTH);
            fieldPanel.add(fields[i], BorderLayout.CENTER);
            formPanel.add(fieldPanel);
        }

        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.setBackground(new Color(150, 90, 60));
        createAccountButton.setForeground(Color.WHITE);
        createAccountButton.setFont(new Font("Georgia", Font.BOLD, 16));
        createAccountButton.addActionListener(e -> createNewAccount(fields, signupDialog));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Georgia", Font.PLAIN, 14));
        cancelButton.addActionListener(e -> signupDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.add(createAccountButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        signupDialog.add(mainPanel);
        signupDialog.setVisible(true);
    }

    private void createNewAccount(JTextField[] fields, JDialog dialog) {
        String username = fields[0].getText().trim();
        String password = new String(((JPasswordField) fields[1]).getPassword()).trim();
        String confirmPassword = new String(((JPasswordField) fields[2]).getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Please fill in all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(dialog, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(dialog, "Password must be at least 6 characters long!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (userDAO.usernameExists(username)) {
            JOptionPane.showMessageDialog(dialog, "Username already exists! Please choose another.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User newUser = new User(0, username, password);
        if (userDAO.createUser(newUser)) {
            JOptionPane.showMessageDialog(dialog, "Account created successfully! You can now login.", "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
            usernameField.setText(username);
            passwordField.setText("");
            usernameField.requestFocus();
        } else {
            JOptionPane.showMessageDialog(dialog, "Error creating account!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookstoreLogin().setVisible(true));
    }
}
