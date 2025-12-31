package com.dentalclinic.dental.UI;

import com.dentalclinic.dental.model.Session;
import com.dentalclinic.dental.security.AccessControl;

import javax.swing.*;
import java.awt.*;

public class SideMenuPanel extends JPanel {

    public SideMenuPanel(MainFrame mainFrame) {

        setLayout(new GridLayout(0, 1, 6, 6));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        var user = Session.getCurrentUser();

        JButton btnDashboard = new JButton("Dashboard");
        JButton btnPatients = new JButton("Patients");
        JButton btnAppointments = new JButton("Appointments");
        JButton btnClinics = new JButton("Clinics");
        JButton btnDentists = new JButton("Dentists");
        JButton btnServices = new JButton("Services");
        JButton btnUsers = new JButton("Users");

        btnDashboard.setActionCommand("dashboard");
        btnDashboard.addActionListener(mainFrame);

        btnPatients.addActionListener(e ->
                mainFrame.showPanel(
                        new PatientPanel(),
                        AccessControl.MANAGE_PATIENTS,
                        "Patients"
                ));

        btnAppointments.addActionListener(e ->
                mainFrame.showPanel(
                        new AppointmentPanel(),
                        AccessControl.MANAGE_APPOINTMENTS,
                        "Appointments"
                ));

        btnClinics.addActionListener(e ->
                mainFrame.showPanel(
                        new ClinicPanel(),
                        AccessControl.MANAGE_CLINICS,
                        "Clinics"
                ));

        btnDentists.addActionListener(e ->
                mainFrame.showPanel(
                        new DentistPanel(),
                        AccessControl.MANAGE_CLINICS,
                        "Dentists"
                ));

        btnServices.addActionListener(e ->
                mainFrame.showPanel(
                        new ServicesPanel(),
                        AccessControl.MANAGE_SERVICES,
                        "Services"
                ));

        btnUsers.addActionListener(e ->
                mainFrame.showPanel(
                        new UserPanel(),
                        AccessControl.MANAGE_USERS,
                        "Users"
                ));

        // =============================
        // RBAC VISIBILITY
        // =============================

        add(btnDashboard);

        if (AccessControl.hasPermission(user, AccessControl.MANAGE_PATIENTS)) {
            add(btnPatients);
        }

        if (AccessControl.hasPermission(user, AccessControl.MANAGE_APPOINTMENTS)) {
            add(btnAppointments);
        }

        if (AccessControl.hasPermission(user, AccessControl.MANAGE_CLINICS)) {
            add(btnClinics);
            add(btnDentists);
        }

        if (AccessControl.hasPermission(user, AccessControl.MANAGE_SERVICES)) {
            add(btnServices);
        }

        if (AccessControl.hasPermission(user, AccessControl.MANAGE_USERS)) {
            add(btnUsers);
        }
    }
}
