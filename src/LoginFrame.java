import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {
    private JTextField userField;
    private JPasswordField passField;

    public LoginFrame() {
        setTitle("Hotel Management System - Login");
        setSize(400, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Title ---
        JLabel titleLabel = new JLabel("Welcome Back", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Username:"), gbc);

        userField = new JTextField(15);
        gbc.gridx = 1;
        add(userField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Password:"), gbc);

        passField = new JPasswordField(15);
        gbc.gridx = 1;
        add(passField, gbc);

        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));
        loginBtn.setBackground(new Color(70, 130, 180)); // Steel Blue
        loginBtn.setForeground(Color.WHITE);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        add(loginBtn, gbc);

        loginBtn.addActionListener(this::handleLogin);

        this.getRootPane().setDefaultButton(loginBtn);
    }

    private void handleLogin(ActionEvent e) {
        String uInput = userField.getText();
        String pInput = new String(passField.getPassword());

        boolean authenticated = false;
        User authenticatedUser = null;

        for (User user : DataRegistry.allUsers) {
            if (user.getUsername().equals(uInput) && user.getPassword().equals(pInput)) {
                authenticated = true;
                authenticatedUser = user;
                break;
            }
        }

        if (authenticated) {
            JOptionPane.showMessageDialog(this, "Login Successful! Role: " + authenticatedUser.getRole());
            this.dispose(); // Close login window
            if (authenticatedUser.getRole().equals("MANAGER")) {
                new ManagerFrame().setVisible(true);
                this.dispose();
            } else if (authenticatedUser.getRole().equals("EMPLOYEE")) {
                new EmployeeFrame().setVisible(true);
                this.dispose();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Username or Password.", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}