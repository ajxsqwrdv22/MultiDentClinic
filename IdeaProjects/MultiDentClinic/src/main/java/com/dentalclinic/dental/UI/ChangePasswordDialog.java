package com.dentalclinic.dental.UI;

import com.dentalclinic.dental.Service.AuthService;
import com.dentalclinic.dental.Service.UserService;
import com.dentalclinic.dental.Service.impl.AuthServiceImpl;
import com.dentalclinic.dental.Service.impl.UserServiceImpl;
import com.dentalclinic.dental.model.Session;
import com.dentalclinic.dental.model.User;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog that allows the currently logged-in user to change their own password.
 * Verifies the old password by calling AuthService.authenticate(username, oldPassword).
 */
public class ChangePasswordDialog extends JDialog {
    private final JPasswordField oldPwd = new JPasswordField(20);
    private final JPasswordField newPwd = new JPasswordField(20);
    private final JPasswordField newPwd2 = new JPasswordField(20);

    private final AuthService authService = new AuthServiceImpl();
    private final UserService userService = new UserServiceImpl();

    public ChangePasswordDialog(Window owner) {
        super(owner, "Change Password", ModalityType.APPLICATION_MODAL);
        initUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        JPanel p = new JPanel(new BorderLayout(8,8));
        JPanel fields = new JPanel(new GridLayout(0,1,6,6));
        fields.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        fields.add(new JLabel("Current password"));
        fields.add(oldPwd);
        fields.add(new JLabel("New password"));
        fields.add(newPwd);
        fields.add(new JLabel("Confirm new password"));
        fields.add(newPwd2);

        p.add(fields, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnOk = new JButton("Change");
        JButton btnCancel = new JButton("Cancel");
        actions.add(btnOk);
        actions.add(btnCancel);
        p.add(actions, BorderLayout.SOUTH);

        btnOk.addActionListener(e -> doChange());
        btnCancel.addActionListener(e -> dispose());

        setContentPane(p);
        getRootPane().setDefaultButton(btnOk);
    }

    private void doChange() {
        User cur = Session.getCurrentUser();
        if (cur == null) {
            JOptionPane.showMessageDialog(this, "No user in session.", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        String oldp = new String(oldPwd.getPassword());
        String np1 = new String(newPwd.getPassword());
        String np2 = new String(newPwd2.getPassword());

        if (oldp.isEmpty() || np1.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!np1.equals(np2)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // verify current password via AuthService
            // AuthService.authenticate(...) is expected to return Optional<User> on success
            // Adapt if your AuthService returns different types.
            java.util.Optional<User> ok = authService.authenticate(cur.getUsername(), oldp);
            if (ok == null || !ok.isPresent()) {
                JOptionPane.showMessageDialog(this, "Current password is incorrect.", "Authentication", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean changed = userService.changePassword(cur.getId(), np1);
            if (changed) {
                JOptionPane.showMessageDialog(this, "Password changed. Please login again.");
                // force logout after password change
                Session.clear();
                dispose();
                // show login screen
                LoginFrame.showLogin();
                // close all open frames (best-effort)
                for (Frame f : Frame.getFrames()) {
                    if (f.isDisplayable()) f.dispose();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Password was not changed.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NoSuchMethodError nsme) {
            nsme.printStackTrace();
            JOptionPane.showMessageDialog(this, "AuthService.authenticate(...) not found. Adapt ChangePasswordDialog to your AuthService API.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error changing password: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
