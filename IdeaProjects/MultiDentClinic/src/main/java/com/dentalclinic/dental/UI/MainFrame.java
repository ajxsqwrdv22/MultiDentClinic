package com.dentalclinic.dental.UI;

import com.dentalclinic.dental.model.Session;
import com.dentalclinic.dental.security.AccessControl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * MainFrame wired to open the CRUD panels and top toolbar.
 * RBAC-SAFE: panels are hidden and blocked if user has no access.
 */
public class MainFrame extends JFrame implements ActionListener {

    private final HeaderPanel header = new HeaderPanel();
    private final JPanel content = new JPanel(new BorderLayout());
    private final SideMenuPanel side;
    private final TopToolbar toolbar;

    private CrudPanel currentCrudPanel;
    private SessionTimeoutManager timeoutManager;

    public MainFrame() {
        setTitle("Dental Clinic - Main");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 760);
        setLocationRelativeTo(null);

        side = new SideMenuPanel(this);
        toolbar = new TopToolbar(this);

        init();
    }

    private void init() {
        setLayout(new BorderLayout(6, 6));

        // ===============================
        // NORTH: Header + Toolbar + Controls
        // ===============================
        JPanel north = new JPanel(new BorderLayout());

        north.add(header, BorderLayout.WEST);
        north.add(toolbar, BorderLayout.CENTER);

        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 8));
        JButton btnChangePwd = new JButton("Change Password");
        JButton btnLogout = new JButton("Logout");
        rightControls.add(btnChangePwd);
        rightControls.add(btnLogout);
        north.add(rightControls, BorderLayout.EAST);

        btnChangePwd.addActionListener(e -> {
            if (Session.getCurrentUser() == null) {
                JOptionPane.showMessageDialog(this, "Not logged in.");
                return;
            }
            new ChangePasswordDialog(this).setVisible(true);
        });

        btnLogout.addActionListener(e -> doLogout());

        add(north, BorderLayout.NORTH);
        add(side, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);

        // ===============================
        // DEFAULT LANDING PANEL (RBAC-AWARE)
        // ===============================
        if (AccessControl.hasPermission(Session.getCurrentUser(),
                AccessControl.MANAGE_APPOINTMENTS)) {
            showAppointments();
        } else if (AccessControl.hasPermission(Session.getCurrentUser(),
                AccessControl.MANAGE_PATIENTS)) {
            showPatients();
        } else {
            showDashboard();
        }

        // ===============================
        // SESSION TIMEOUT (15 min)
        // ===============================
        timeoutManager = new SessionTimeoutManager(this, 900);
        timeoutManager.start();
    }

    // =====================================================
    // CORE PANEL SWITCHER (RBAC ENFORCED)
    // =====================================================
    private void setContentPanel(JPanel panel, String permission, String title) {
        if (permission != null &&
                !AccessControl.hasPermission(Session.getCurrentUser(), permission)) {

            JOptionPane.showMessageDialog(
                    this,
                    "You do not have access to " + title,
                    "Access denied",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        content.removeAll();
        content.add(panel, BorderLayout.CENTER);

        currentCrudPanel = (panel instanceof CrudPanel)
                ? (CrudPanel) panel
                : null;

        revalidate();
        repaint();
    }

    // =====================================================
    // DASHBOARD
    // =====================================================
    private void showDashboard() {
        header.setTitle("Dashboard");
        setContentPanel(new DashboardPanel(), null, "Dashboard");
    }

    // =====================================================
    // PANEL ROUTES (RBAC-SAFE)
    // =====================================================
    private void showClinics() {
        header.setTitle("Clinics");
        setContentPanel(new ClinicPanel(),
                AccessControl.MANAGE_CLINICS,
                "Clinics");
    }

    private void showDentists() {
        header.setTitle("Dentists");
        setContentPanel(new DentistPanel(),
                AccessControl.MANAGE_CLINICS,
                "Dentists");
    }

    private void showPatients() {
        header.setTitle("Patients");
        setContentPanel(new PatientPanel(),
                AccessControl.MANAGE_PATIENTS,
                "Patients");
    }

    private void showAppointments() {
        header.setTitle("Appointments");
        setContentPanel(new AppointmentPanel(),
                AccessControl.MANAGE_APPOINTMENTS,
                "Appointments");
    }

    private void showServices() {
        header.setTitle("Services");
        setContentPanel(new ServicesPanel(),
                AccessControl.MANAGE_SERVICES,
                "Services");
    }

    private void showUsers() {
        header.setTitle("Users");
        setContentPanel(new UserPanel(),
                AccessControl.MANAGE_USERS,
                "Users");
    }

    // =====================================================
    // ACTION HANDLER (SIDE MENU + TOOLBAR)
    // =====================================================
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {
            case "dashboard": showDashboard(); return;
            case "clinics": showClinics(); return;
            case "dentists": showDentists(); return;
            case "patients": showPatients(); return;
            case "appointments": showAppointments(); return;
            case "services": showServices(); return;
            case "users": showUsers(); return;
        }

        // Toolbar actions
        if ("toolbar_refresh".equals(cmd) && currentCrudPanel != null) {
            currentCrudPanel.refresh();
        }
        if ("toolbar_search".equals(cmd) && currentCrudPanel != null) {
            currentCrudPanel.search(toolbar.getSearchText());
        }
        if ("toolbar_clear".equals(cmd) && currentCrudPanel != null) {
            currentCrudPanel.search("");
        }
    }

    // =====================================================
    // LOGOUT
    // =====================================================
    public void doLogout() {
        int ok = JOptionPane.showConfirmDialog(
                this,
                "Log out?",
                "Logout",
                JOptionPane.YES_NO_OPTION
        );
        if (ok != JOptionPane.YES_OPTION) return;

        if (timeoutManager != null) timeoutManager.stop();

        Session.clear();

        SwingUtilities.invokeLater(() -> {
            for (Frame f : Frame.getFrames()) {
                if (f.isDisplayable()) f.dispose();
            }
            LoginFrame.showLogin();
        });
    }

    @Override
    public void dispose() {
        if (timeoutManager != null) timeoutManager.stop();
        super.dispose();
    }

    /**
     * Update header user label.
     */
    public void setHeaderUser(String username) {
        try {
            header.setUser(username);
        } catch (Throwable ignored) {}
    }
    public void showPanel(JPanel panel, String permission, String title) {
        if (!AccessControl.hasPermission(
                com.dentalclinic.dental.model.Session.getCurrentUser(),
                permission)) {

            JOptionPane.showMessageDialog(
                    this,
                    "You do not have access to " + title,
                    "Access denied",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        content.removeAll();
        content.add(panel, BorderLayout.CENTER);

        if (panel instanceof CrudPanel) {
            currentCrudPanel = (CrudPanel) panel;
        } else {
            currentCrudPanel = null;
        }

        revalidate();
        repaint();
    }
}
