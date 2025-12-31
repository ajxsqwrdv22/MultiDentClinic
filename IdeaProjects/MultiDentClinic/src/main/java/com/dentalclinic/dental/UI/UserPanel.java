package com.dentalclinic.dental.UI;

import com.dentalclinic.dental.Service.UserService;
import com.dentalclinic.dental.Service.RoleService;
import com.dentalclinic.dental.Service.impl.UserServiceImpl;
import com.dentalclinic.dental.Service.impl.RoleServiceImpl;
import com.dentalclinic.dental.model.User;
import com.dentalclinic.dental.model.Role;
import com.dentalclinic.dental.model.Session;
import com.dentalclinic.dental.security.AccessControl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserPanel extends JPanel implements CrudPanel {

    private final UserService userService = new UserServiceImpl();
    private final RoleService roleService = new RoleServiceImpl();

    private final DefaultTableModel model =
            new DefaultTableModel(new Object[]{"ID", "Username", "Roles", "Enabled"}, 0);

    private final JTable table = new JTable(model);

    public UserPanel() {

        // 🔒 ADMIN ONLY
        if (!AccessControl.hasPermission(
                Session.getCurrentUser(),
                AccessControl.MANAGE_USERS)) {

            setLayout(new BorderLayout());
            add(new JLabel("Access denied", SwingConstants.CENTER), BorderLayout.CENTER);
            return;
        }

        setLayout(new BorderLayout(6, 6));
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refresh = new JButton("Refresh");
        JButton add = new JButton("Add");
        JButton edit = new JButton("Edit");
        JButton del = new JButton("Delete");
        JButton toggle = new JButton("Enable / Disable");
        JButton chpwd = new JButton("Change Password");

        btns.add(refresh);
        btns.add(add);
        btns.add(edit);
        btns.add(del);
        btns.add(toggle);
        btns.add(chpwd);

        add(btns, BorderLayout.SOUTH);

        refresh.addActionListener(e -> refresh());
        add.addActionListener(e -> addUser());
        edit.addActionListener(e -> editUser());
        del.addActionListener(e -> deleteUser());
        toggle.addActionListener(e -> toggleUser());
        chpwd.addActionListener(e -> changePassword());

        refresh();
    }

    // =====================================================
    // LOAD USERS
    // =====================================================
    @Override
    public void refresh() {
        model.setRowCount(0);
        try {
            for (User u : userService.listAll()) {
                String roles = u.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.joining(", "));

                model.addRow(new Object[]{
                        u.getId(),
                        u.getUsername(),
                        roles,
                        u.isEnabled()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error loading users: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    @Override
    public void search(String filter) {
        refresh();
    }

    // =====================================================
    // ADD USER
    // =====================================================
    private void addUser() {
        try {
            JTextField username = new JTextField();
            JPasswordField pwd = new JPasswordField();

            List<Role> roles = roleService.findAll();
            JComboBox<Role> roleBox = new JComboBox<>(roles.toArray(new Role[0]));

            int ok = JOptionPane.showConfirmDialog(
                    this,
                    new Object[]{
                            "Username", username,
                            "Password", pwd,
                            "Role", roleBox
                    },
                    "Add User",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (ok != JOptionPane.OK_OPTION) return;

            String u = username.getText().trim();
            String p = new String(pwd.getPassword());

            if (u.isEmpty() || p.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and password required.");
                return;
            }

            User user = new User();
            user.setUsername(u);
            user.setEnabled(true);

            Long id = userService.createUser(user, p);
            userService.assignRole(id, ((Role) roleBox.getSelectedItem()).getId());

            JOptionPane.showMessageDialog(this, "User created.");
            refresh();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error creating user: " + ex.getMessage());
        }
    }

    // =====================================================
    // EDIT USER (USERNAME + ROLE)
    // =====================================================
    private void editUser() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select a user.");
            return;
        }

        Long id = (Long) model.getValueAt(r, 0);

        try {
            Optional<User> ou = userService.findById(id);
            if (!ou.isPresent()) return;

            User user = ou.get();
            JTextField username = new JTextField(user.getUsername());

            List<Role> roles = roleService.findAll();
            JComboBox<Role> roleBox = new JComboBox<>(roles.toArray(new Role[0]));

            // select current role
            if (!user.getRoles().isEmpty()) {
                for (int i = 0; i < roles.size(); i++) {
                    if (roles.get(i).getId().equals(user.getRoles().get(0).getId())) {
                        roleBox.setSelectedIndex(i);
                        break;
                    }
                }
            }

            int ok = JOptionPane.showConfirmDialog(
                    this,
                    new Object[]{"Username", username, "Role", roleBox},
                    "Edit User",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (ok != JOptionPane.OK_OPTION) return;

            user.setUsername(username.getText().trim());
            userService.updateUser(user);

            userService.replaceRoles(
                    user.getId(),
                    List.of(((Role) roleBox.getSelectedItem()).getId())
            );

            refresh();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error editing user: " + ex.getMessage());
        }
    }

    // =====================================================
    // DELETE USER (SAFE)
    // =====================================================
    private void deleteUser() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        Long id = (Long) model.getValueAt(r, 0);

        try {
            long adminCount = userService.listAll().stream()
                    .filter(u -> u.hasRole("ADMIN"))
                    .count();

            Optional<User> target = userService.findById(id);
            if (target.isPresent() && target.get().hasRole("ADMIN") && adminCount <= 1) {
                JOptionPane.showMessageDialog(this, "Cannot delete last ADMIN.");
                return;
            }

            if (JOptionPane.showConfirmDialog(
                    this,
                    "Delete user?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION
            ) != JOptionPane.YES_OPTION) return;

            userService.delete(id);
            refresh();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error deleting user: " + ex.getMessage());
        }
    }

    // =====================================================
    // ENABLE / DISABLE USER
    // =====================================================
    private void toggleUser() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        Long id = (Long) model.getValueAt(r, 0);

        try {
            Optional<User> ou = userService.findById(id);
            if (!ou.isPresent()) return;

            User u = ou.get();
            u.setEnabled(!u.isEnabled());
            userService.updateUser(u);
            refresh();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating user: " + ex.getMessage());
        }
    }

    // =====================================================
    // CHANGE PASSWORD
    // =====================================================
    private void changePassword() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        Long id = (Long) model.getValueAt(r, 0);

        JPasswordField p1 = new JPasswordField();
        JPasswordField p2 = new JPasswordField();

        int ok = JOptionPane.showConfirmDialog(
                this,
                new Object[]{"New password", p1, "Confirm", p2},
                "Change Password",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (ok != JOptionPane.OK_OPTION) return;

        if (!new String(p1.getPassword()).equals(new String(p2.getPassword()))) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.");
            return;
        }

        try {
            userService.changePassword(id, new String(p1.getPassword()));
            JOptionPane.showMessageDialog(this, "Password changed.");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
