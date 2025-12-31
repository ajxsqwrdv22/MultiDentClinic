package com.dentalclinic.dental.UI;

import com.dentalclinic.dental.Service.*;
import com.dentalclinic.dental.Service.impl.*;
import com.dentalclinic.dental.model.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Centralized dialog helpers for CRUD forms.
 * BASELINE VERSION — no policy / availability logic.
 */
public final class FormUtils {

    private FormUtils() {}

    // =====================================================
    // PATIENT FORM
    // =====================================================

    public static Patient showPatientForm(Component parent) {
        return showPatientForm(parent, null);
    }

    public static Patient showPatientForm(Component parent, Patient existing) {

        JTextField tfFirst = new JTextField(15);
        JTextField tfLast = new JTextField(15);
        JTextField tfContact = new JTextField(15);
        JTextField tfAddress = new JTextField(15);

        // =========================
        // PRE-FILL WHEN EDITING
        // =========================
        if (existing != null) {
            tfFirst.setText(existing.getFirstName());
            tfLast.setText(existing.getLastName());
            tfContact.setText(existing.getContact());
            tfAddress.setText(existing.getAddress());
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("First name:"));
        panel.add(tfFirst);
        panel.add(new JLabel("Last name:"));
        panel.add(tfLast);
        panel.add(new JLabel("Contact: 09XXXXXXXXX"));
        panel.add(tfContact);
        panel.add(new JLabel("Address:"));
        panel.add(tfAddress);

        int res = JOptionPane.showConfirmDialog(
                parent,
                panel,
                existing == null ? "Add Patient" : "Edit Patient",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (res != JOptionPane.OK_OPTION) return null;

        // =========================
        // VALIDATION (FORM STAYS OPEN)
        // =========================

        // Letters only (no numbers / symbols)
        if (!tfFirst.getText().matches("[A-Za-z ]+")) {
            JOptionPane.showMessageDialog(
                    parent,
                    "First name must contain letters only (no numbers or symbols).",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );
            return showPatientForm(parent, existing);
        }

        if (!tfLast.getText().matches("[A-Za-z ]+")) {
            JOptionPane.showMessageDialog(
                    parent,
                    "Last name must contain letters only (no numbers or symbols).",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );
            return showPatientForm(parent, existing);
        }

        // Philippine mobile number format
        if (!tfContact.getText().matches("^09\\d{9}$")) {
            JOptionPane.showMessageDialog(
                    parent,
                    "Contact number must be in the format 09XXXXXXXXX.",
                    "Invalid Contact Number",
                    JOptionPane.ERROR_MESSAGE
            );
            return showPatientForm(parent, existing);
        }

        // =========================
        // SAVE
        // =========================
        Patient p = existing != null ? existing : new Patient();
        p.setFirstName(tfFirst.getText().trim());
        p.setLastName(tfLast.getText().trim());
        p.setContact(tfContact.getText().trim());
        p.setAddress(tfAddress.getText().trim());

        return p;
    }



    // =====================================================
    // CLINIC FORM
    // =====================================================

    public static Clinic showClinicForm(Component parent) {
        return showClinicForm(parent, null);
    }

    public static Clinic showClinicForm(Component parent, Clinic existing) {
        JTextField tfName = new JTextField(20);
        JTextField tfAddress = new JTextField(20);

        if (existing != null) {
            tfName.setText(existing.getName());
            tfAddress.setText(existing.getAddress());
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Clinic name:"));
        panel.add(tfName);
        panel.add(new JLabel("Address:"));
        panel.add(tfAddress);

        int res = JOptionPane.showConfirmDialog(
                parent, panel,
                existing == null ? "Add Clinic" : "Edit Clinic",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (res != JOptionPane.OK_OPTION) return null;

        Clinic c = existing != null ? existing : new Clinic();
        c.setName(tfName.getText().trim());
        c.setAddress(tfAddress.getText().trim());
        return c;
    }

    // =====================================================
    // DENTIST FORM
    // =====================================================

    public static Dentist showDentistForm(Component parent) {
        try {
            return showDentistForm(parent, null, new ClinicServiceImpl().listAll());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, "Failed to load clinics");
            return null;
        }
    }

    public static Dentist showDentistForm(
            Component parent,
            Dentist existing,
            List<Clinic> clinics
    ) {

        JTextField tfFirst = new JTextField(15);
        JTextField tfLast = new JTextField(15);

        // ✅ PREDEFINED SPECIALTIES (NO RANDOM INPUT)
        JComboBox<String> cbSpecialty = new JComboBox<>(new String[]{
                "General Dentistry",
                "Orthodontics",
                "Pediatric Dentistry",
                "Oral Surgery",
                "Periodontics"
        });

        JComboBox<Clinic> cbClinic =
                new JComboBox<>(clinics.toArray(new Clinic[0]));

        // =========================
        // PRE-FILL WHEN EDITING
        // =========================
        if (existing != null) {
            tfFirst.setText(existing.getFirstName());
            tfLast.setText(existing.getLastName());
            cbSpecialty.setSelectedItem(existing.getSpecialty());

            clinics.stream()
                    .filter(c -> c.getId().equals(existing.getClinicId()))
                    .findFirst()
                    .ifPresent(cbClinic::setSelectedItem);
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("First name:"));
        panel.add(tfFirst);
        panel.add(new JLabel("Last name:"));
        panel.add(tfLast);
        panel.add(new JLabel("Specialty:"));
        panel.add(cbSpecialty);
        panel.add(new JLabel("Clinic:"));
        panel.add(cbClinic);

        int res = JOptionPane.showConfirmDialog(
                parent,
                panel,
                existing == null ? "Add Dentist" : "Edit Dentist",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (res != JOptionPane.OK_OPTION) return null;

        // =========================
        // VALIDATION (LETTERS ONLY)
        // =========================
        if (!tfFirst.getText().matches("[A-Za-z ]+")) {
            JOptionPane.showMessageDialog(
                    parent,
                    "First name must contain letters only.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );
            return showDentistForm(parent, existing, clinics);
        }

        if (!tfLast.getText().matches("[A-Za-z ]+")) {
            JOptionPane.showMessageDialog(
                    parent,
                    "Last name must contain letters only.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );
            return showDentistForm(parent, existing, clinics);
        }

        // =========================
        // SAVE
        // =========================
        Dentist d = existing != null ? existing : new Dentist();
        d.setFirstName(tfFirst.getText().trim());
        d.setLastName(tfLast.getText().trim());
        d.setSpecialty((String) cbSpecialty.getSelectedItem());
        d.setClinicId(((Clinic) cbClinic.getSelectedItem()).getId());

        return d;
    }


    // =====================================================
    // SERVICE FORM (SIMPLE — NO POLICY)
    // =====================================================
    public static Service showServiceForm(
            Component parent,
            Service existing,
            List<Clinic> clinics
    ) {
        if (clinics == null || clinics.isEmpty()) {
            JOptionPane.showMessageDialog(
                    parent,
                    "No clinics available. Add a clinic first.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE
            );
            return null;
        }

        // Fixed service catalog (NO free text)
        String[] SERVICE_CATALOG = {
                "Consultation",
                "Tooth Extraction",
                "Teeth Cleaning",
                "Dental Filling",
                "Root Canal",
                "Braces Adjustment",
                "X-Ray"
        };

        JComboBox<Clinic> cbClinic =
                new JComboBox<>(clinics.toArray(new Clinic[0]));
        JComboBox<String> cbServiceName =
                new JComboBox<>(SERVICE_CATALOG);
        JTextField tfPrice = new JTextField(10);

        if (existing != null) {
            cbServiceName.setSelectedItem(existing.getName());
            tfPrice.setText(String.valueOf(existing.getPrice()));

            clinics.stream()
                    .filter(c -> c.getId().equals(existing.getClinicId()))
                    .findFirst()
                    .ifPresent(cbClinic::setSelectedItem);
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Clinic:"));
        panel.add(cbClinic);
        panel.add(new JLabel("Service:"));
        panel.add(cbServiceName);
        panel.add(new JLabel("Price:"));
        panel.add(tfPrice);

        int res = JOptionPane.showConfirmDialog(
                parent,
                panel,
                existing == null ? "Add Service" : "Edit Service",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (res != JOptionPane.OK_OPTION) return null;

        try {
            Service s = existing != null ? existing : new Service();

            s.setClinicId(((Clinic) cbClinic.getSelectedItem()).getId());
            s.setName((String) cbServiceName.getSelectedItem());
            s.setPrice(Double.parseDouble(tfPrice.getText().trim()));
            s.setActive(true);

            return s;

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    parent,
                    "Invalid price value.",
                    "Validation",
                    JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
    }

    // =====================================================
    // APPOINTMENT FORM (NO POLICY)
    // =====================================================
    public static Appointment showAppointmentForm(
            Component parent,
            Appointment existing,
            List<Patient> patients,
            List<Dentist> dentists,
            List<Clinic> clinics
    ) {

        ServiceService serviceService = new ServiceServiceImpl();
        List<Service> services;

        try {
            services = serviceService.listAll();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, "Failed to load services");
            return null;
        }

        JComboBox<Patient> cbPatient =
                new JComboBox<>(patients.toArray(new Patient[0]));
        JComboBox<Clinic> cbClinic =
                new JComboBox<>(clinics.toArray(new Clinic[0]));
        JComboBox<Dentist> cbDentist = new JComboBox<>();
        JComboBox<Service> cbService = new JComboBox<>();

        JComboBox<AppointmentStatus> cbStatus =
                new JComboBox<>(AppointmentStatus.values());

        SpinnerDateModel dateModel =
                new SpinnerDateModel(new Date(), null, null, java.util.Calendar.MINUTE);
        JSpinner spDate = new JSpinner(dateModel);
        spDate.setEditor(new JSpinner.DateEditor(spDate, "yyyy-MM-dd HH:mm"));

        // =====================================================
        // FILTER DENTISTS BY SELECTED CLINIC
        // =====================================================
        Runnable reloadDentists = () -> {
            cbDentist.removeAllItems();
            Clinic selectedClinic = (Clinic) cbClinic.getSelectedItem();
            if (selectedClinic == null) return;

            for (Dentist d : dentists) {
                if (selectedClinic.getId().equals(d.getClinicId())) {
                    cbDentist.addItem(d);
                }
            }
        };

        // =====================================================
        // FILTER SERVICES BY SELECTED CLINIC
        // =====================================================
        Runnable reloadServices = () -> {
            cbService.removeAllItems();
            Clinic selectedClinic = (Clinic) cbClinic.getSelectedItem();
            if (selectedClinic == null) return;

            for (Service s : services) {
                if (s.isActive()
                        && selectedClinic.getId().equals(s.getClinicId())) {
                    cbService.addItem(s);
                }
            }
        };

        cbClinic.addActionListener(e -> {
            reloadDentists.run();
            reloadServices.run();
        });

        // =====================================================
        // PRE-FILL WHEN EDITING
        // =====================================================
        if (existing != null) {

            patients.stream()
                    .filter(p -> p.getId().equals(existing.getPatientId()))
                    .findFirst()
                    .ifPresent(cbPatient::setSelectedItem);

            clinics.stream()
                    .filter(c -> c.getId().equals(existing.getClinicId()))
                    .findFirst()
                    .ifPresent(cbClinic::setSelectedItem);

            // must reload AFTER clinic is set
            reloadDentists.run();
            reloadServices.run();

            dentists.stream()
                    .filter(d -> d.getId().equals(existing.getDentistId()))
                    .findFirst()
                    .ifPresent(cbDentist::setSelectedItem);

            services.stream()
                    .filter(s -> s.getId().equals(existing.getServiceId()))
                    .findFirst()
                    .ifPresent(cbService::setSelectedItem);

            cbStatus.setSelectedItem(existing.getStatus());

            spDate.setValue(Date.from(
                    existing.getScheduledAt()
                            .atZone(ZoneId.systemDefault())
                            .toInstant()
            ));

        } else {
            cbStatus.setSelectedItem(AppointmentStatus.BOOKED);
            reloadDentists.run();
            reloadServices.run();
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Patient:"));
        panel.add(cbPatient);
        panel.add(new JLabel("Clinic:"));
        panel.add(cbClinic);
        panel.add(new JLabel("Service:"));
        panel.add(cbService);
        panel.add(new JLabel("Dentist:"));
        panel.add(cbDentist);
        panel.add(new JLabel("Date & Time:"));
        panel.add(spDate);
        panel.add(new JLabel("Status:"));
        panel.add(cbStatus);

        int res = JOptionPane.showConfirmDialog(
                parent,
                panel,
                existing == null ? "Add Appointment" : "Edit Appointment",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (res != JOptionPane.OK_OPTION) return null;

        Appointment a = existing != null ? existing : new Appointment();

        a.setPatientId(((Patient) cbPatient.getSelectedItem()).getId());
        a.setClinicId(((Clinic) cbClinic.getSelectedItem()).getId());
        a.setDentistId(((Dentist) cbDentist.getSelectedItem()).getId());
        a.setServiceId(((Service) cbService.getSelectedItem()).getId());

        Date date = (Date) spDate.getValue();
        a.setScheduledAt(
                LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
        );

        a.setStatus((AppointmentStatus) cbStatus.getSelectedItem());

        return a;
    }

    // =====================================================
// USER FORM (ADMIN / STAFF ONLY – NO CLOSE ON ERROR)
// =====================================================
    public static User showUserForm(Component parent, User existing) {

        JTextField tfUsername = new JTextField(15);
        JPasswordField pfPassword = new JPasswordField(15);

        JComboBox<Role> cbRole = new JComboBox<>(
                new Role[]{
                        new Role(1L, Role.ADMIN),
                        new Role(2L, Role.STAFF)



                }
        );

        JCheckBox chkEnabled = new JCheckBox("Enabled");

        // =========================
        // PRE-FILL WHEN EDITING
        // =========================
        if (existing != null) {
            tfUsername.setText(existing.getUsername());
            cbRole.setSelectedItem(existing.getRole());
            chkEnabled.setSelected(existing.isEnabled());
        } else {
            chkEnabled.setSelected(true);
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Username:"));
        panel.add(tfUsername);
        panel.add(new JLabel("Password:"));
        panel.add(pfPassword);
        panel.add(new JLabel("Role:"));
        panel.add(cbRole);
        panel.add(new JLabel(""));
        panel.add(chkEnabled);

        int res = JOptionPane.showConfirmDialog(
                parent,
                panel,
                existing == null ? "Add User" : "Edit User",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (res != JOptionPane.OK_OPTION) return null;

        String username = tfUsername.getText().trim();
        String password = new String(pfPassword.getPassword());

        // =========================
        // VALIDATION (FORM STAYS OPEN)
        // =========================
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Username is required.");
            return showUserForm(parent, existing);
        }

        if (existing == null && password.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Password is required for new users.");
            return showUserForm(parent, existing);
        }

        if (!password.isEmpty() &&
                !password.matches("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}")) {

            JOptionPane.showMessageDialog(
                    parent,
                    "Password must be at least 8 characters and include:\n" +
                            "• Uppercase\n• Lowercase\n• Number\n• Special character"
            );
            return showUserForm(parent, existing);
        }

        // =========================
        // SAVE
        // =========================
        User u = existing != null ? existing : new User();
        u.setUsername(username);
        u.setRole((Role) cbRole.getSelectedItem());
        u.setEnabled(chkEnabled.isSelected());

        // Raw password passed to service (hashed there)
        if (!password.isEmpty()) {
            u.setPassword(password);
        }

        return u;
    }


}
