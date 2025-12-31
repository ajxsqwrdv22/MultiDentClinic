package com.dentalclinic.dental.UI;

import com.dentalclinic.dental.Service.AuthService;
import com.dentalclinic.dental.Service.impl.AuthServiceImpl;
import com.dentalclinic.dental.model.User;
import com.dentalclinic.dental.model.Session;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

/**
 * Clean LoginFrame:
 *  - Authenticates user
 *  - Sets Session.currentUser
 *  - Opens MainFrame
 */
public class LoginFrame extends JFrame {

    private final JTextField tfUsername = new JTextField(20);
    private final JPasswordField pfPassword = new JPasswordField(20);
    private final AuthService authService = new AuthServiceImpl();

    public LoginFrame() {
        setTitle("Dental Clinic - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
        pack();
        setLocationRelativeTo(null);
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        form.add(new JLabel("Username:"));
        form.add(tfUsername);
        form.add(new JLabel("Password:"));
        form.add(pfPassword);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLogin = new JButton("Login");
        JButton btnExit = new JButton("Exit");

        actions.add(btnLogin);
        actions.add(btnExit);

        root.add(form, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);

        setContentPane(root);

        btnLogin.addActionListener(e -> doLogin());
        btnExit.addActionListener(e -> System.exit(0));
        pfPassword.addActionListener(e -> doLogin());
    }

    private void doLogin() {
        String username = tfUsername.getText().trim();
        String password = new String(pfPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter username and password.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            Optional<User> ou = authService.authenticate(username, password);

            if (!ou.isPresent()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid username or password.",
                        "Login failed",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            User user = ou.get();

            // ✅ set session
            Session.setCurrentUser(user);

            // ✅ open main frame
            SwingUtilities.invokeLater(() -> {
                MainFrame main = new MainFrame();
                main.setHeaderUser(user.getUsername());
                main.setVisible(true);
            });

            // close login window
            dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Login error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Convenience launcher
    public static void showLogin() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new LoginFrame().setVisible(true);
        });
    }
}
