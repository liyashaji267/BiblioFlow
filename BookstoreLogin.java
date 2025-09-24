// BookstoreLogin.java
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
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // Main panel with background color
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Logo/Header section
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(240, 248, 255));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel logoLabel = new JLabel("📚 BIBLIOFLOW", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Serif", Font.BOLD, 32));
        logoLabel.setForeground(new Color(70, 130, 180));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Bookstore Management System", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.DARK_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(logoLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Login form panel
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBackground(new Color(240, 248, 255));
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        JLabel loginTitle = new JLabel("Login to Your Account", SwingConstants.CENTER);
        loginTitle.setFont(new Font("Arial", Font.BOLD, 20));
        loginTitle.setForeground(new Color(70, 130, 180));
        loginTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username field
        JPanel usernamePanel = new JPanel(new BorderLayout(5, 5));
        usernamePanel.setBackground(new Color(240, 248, 255));
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(200, 35));
        usernamePanel.add(usernameLabel, BorderLayout.NORTH);
        usernamePanel.add(usernameField, BorderLayout.CENTER);

        // Password field
        JPanel passwordPanel = new JPanel(new BorderLayout(5, 5));
        passwordPanel.setBackground(new Color(240, 248, 255));
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 12));
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 35));
        passwordPanel.add(passwordLabel, BorderLayout.NORTH);
        passwordPanel.add(passwordField, BorderLayout.CENTER);

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setFocusPainted(false);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setPreferredSize(new Dimension(200, 40));
        loginButton.addActionListener(new LoginButtonListener());

        // Signup prompt
        JPanel signupPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        signupPanel.setBackground(new Color(240, 248, 255));
        JLabel signupLabel = new JLabel("Don't have an account?");
        JButton signupButton = new JButton("Sign Up");
        signupButton.setBorderPainted(false);
        signupButton.setContentAreaFilled(false);
        signupButton.setForeground(new Color(70, 130, 180));
        signupButton.setFont(new Font("Arial", Font.BOLD, 12));
        signupButton.addActionListener(e -> openSignupDialog());
        signupPanel.add(signupLabel);
        signupPanel.add(signupButton);

        // Exit button
        JButton exitButton = new JButton("Exit");
        exitButton.setBackground(Color.RED);
        exitButton.setForeground(Color.WHITE);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.addActionListener(e -> System.exit(0));

        // Add components to login panel
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

        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(loginPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Enter key listener for login
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
                
                // Open main application
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
                
                // Clear password field
                passwordField.setText("");
            }
        }
    }

    private void openSignupDialog() {
        JDialog signupDialog = new JDialog(this, "Create New Account", true);
        signupDialog.setSize(400, 400);
        signupDialog.setLocationRelativeTo(this);
        signupDialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Create Your Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(70, 130, 180));

        JPanel formPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        formPanel.setBackground(new Color(240, 248, 255));

        // Only 3 fields now: Username, Password, Confirm Password
        String[] labels = {"Username:", "Password:", "Confirm Password:"};
        JTextField[] fields = new JTextField[labels.length];
        
        for (int i = 0; i < labels.length; i++) {
            JPanel fieldPanel = new JPanel(new BorderLayout(5, 5));
            fieldPanel.setBackground(new Color(240, 248, 255));
            
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Arial", Font.BOLD, 12));
            
            if (i == 1 || i == 2) {
                fields[i] = new JPasswordField();
            } else {
                fields[i] = new JTextField();
            }
            fields[i].setPreferredSize(new Dimension(300, 35));
            
            fieldPanel.add(label, BorderLayout.NORTH);
            fieldPanel.add(fields[i], BorderLayout.CENTER);
            formPanel.add(fieldPanel);
        }

        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.setBackground(new Color(76, 175, 80));
        createAccountButton.setForeground(Color.WHITE);
        createAccountButton.setFont(new Font("Arial", Font.BOLD, 14));
        createAccountButton.addActionListener(e -> {
            createNewAccount(fields, signupDialog);
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> signupDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(240, 248, 255));
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
        String password = new String(((JPasswordField)fields[1]).getPassword()).trim();
        String confirmPassword = new String(((JPasswordField)fields[2]).getPassword()).trim();

        // Validation
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
        SwingUtilities.invokeLater(() -> {
            new BookstoreLogin().setVisible(true);
        });
    }
}