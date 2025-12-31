package com.dentalclinic.dental.UI;

import com.dentalclinic.dental.Service.*;
import com.dentalclinic.dental.Service.impl.*;
import com.dentalclinic.dental.model.*;
import com.dentalclinic.dental.security.AccessControl;
import com.dentalclinic.dental.UI.FormUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class AppointmentPanel extends JPanel implements CrudPanel {

    private final AppointmentService appointmentService = new AppointmentServiceImpl();
    private final PatientService patientService = new PatientServiceImpl();
    private final DentistService dentistService = new DentistServiceImpl();
    private final ClinicService clinicService = new ClinicServiceImpl();
    private boolean sortAsc = true;


    private final DefaultTableModel model =
            new DefaultTableModel(
                    new Object[]{"ID", "Patient", "Dentist", "Clinic", "Scheduled At", "Status"},
                    0
            );

    private final JTable table = new JTable(model);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");

    private final JTextField tfPatientSearch = new JTextField(20);
    private JComboBox<String> cbClinicFilter;

    public AppointmentPanel() {
        setLayout(new BorderLayout(6, 6));
        //Sorter
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JButton btnSort = new JButton("Sort");

        btnSort.addActionListener(e -> {
            int columnIndex = 4;

            SortOrder order = sortAsc ? SortOrder.ASCENDING : SortOrder.DESCENDING;
            sorter.setSortKeys(List.of(new RowSorter.SortKey(columnIndex, order)));
        });
        // TABLE
        add(new JScrollPane(table), BorderLayout.CENTER);

        // TOP FILTER BAR
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

        cbClinicFilter = new JComboBox<>();
        cbClinicFilter.addItem("All");

        try {
            for (Clinic c : clinicService.listAll()) {
                cbClinicFilter.addItem(c.getName());
            }
        } catch (Exception ignored) {}

        JButton btnRefresh = new JButton("Refresh");
        JButton btnAdd = new JButton("Add");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");
        top.add(new JLabel("Clinic:"));
        top.add(cbClinicFilter);

        top.add(new JLabel("Search Patient:"));
        top.add(tfPatientSearch);

        JButton btnSearch = new JButton("Search");
        JButton btnClear = new JButton("Clear");

        top.add(btnSearch);
        top.add(btnClear);
        top.add(btnRefresh);
        top.add(btnAdd);
        top.add(btnEdit);
        top.add(btnDelete);
        top.add(btnSort);

        add(top, BorderLayout.NORTH);
        // ACTIONS
        btnRefresh.addActionListener(e -> refresh());
        btnAdd.addActionListener(e -> addAppointment());
        btnEdit.addActionListener(e -> editAppointment());
        btnDelete.addActionListener(e -> deleteAppointment());

        btnSearch.addActionListener(e -> searchPatient());
        btnClear.addActionListener(e -> {
            tfPatientSearch.setText("");
            refresh();
        });

        cbClinicFilter.addActionListener(e -> applyClinicFilter());

        refresh();
    }

    // =====================================================
    // REFRESH
    // =====================================================
    @Override
    public void refresh() {
        model.setRowCount(0);
        try {
            for (Appointment a : appointmentService.listAll()) {
                appendRow(a);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading appointments: " + e.getMessage());
        }
    }

    // =====================================================
    // SEARCH BY PATIENT
    // =====================================================
    private void searchPatient() {
        String query = tfPatientSearch.getText().trim();
        if (query.isEmpty()) {
            refresh();
            return;
        }

        model.setRowCount(0);

        try {
            if (query.matches("\\d+")) {
                Long id = Long.parseLong(query);
                for (Appointment a : appointmentService.findByPatientId(id)) {
                    appendRow(a);
                }
                return;
            }

            List<Patient> matches = patientService.listAll().stream()
                    .filter(p -> (p.getFirstName() + " " + p.getLastName())
                            .toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());

            for (Patient p : matches) {
                for (Appointment a : appointmentService.findByPatientId(p.getId())) {
                    appendRow(a);
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Search error: " + e.getMessage());
        }
    }

    // =====================================================
    // CLINIC FILTER
    // =====================================================
    private void applyClinicFilter() {
        String choice = (String) cbClinicFilter.getSelectedItem();
        if (choice == null || choice.equals("All")) {
            refresh();
            return;
        }

        model.setRowCount(0);

        try {
            Long clinicId = clinicService.listAll().stream()
                    .filter(c -> c.getName().equals(choice))
                    .map(Clinic::getId)
                    .findFirst().orElse(null);

            for (Appointment a : appointmentService.listAll()) {
                if (clinicId != null && clinicId.equals(a.getClinicId())) {
                    appendRow(a);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Filter error: " + e.getMessage());
        }
    }

    // =====================================================
    // ADD ROW
    // =====================================================
    private void appendRow(Appointment a) {
        try {
            String patientName = patientService.findById(a.getPatientId())
                    .map(p -> p.getFirstName() + " " + p.getLastName())
                    .orElse("");

            String dentistName = dentistService.findById(a.getDentistId())
                    .map(d -> d.getFirstName() + " " + d.getLastName())
                    .orElse("");

            String clinicName = clinicService.findById(a.getClinicId())
                    .map(Clinic::getName)
                    .orElse("");

            model.addRow(new Object[]{
                    a.getId(),
                    patientName,
                    dentistName,
                    clinicName,
                    a.getScheduledAt() == null ? "" : a.getScheduledAt().format(formatter),
                    a.getStatus().name()
            });

        } catch (Exception ignored) {}
    }

    // =====================================================
    // ADD
    // =====================================================
    private void addAppointment() {
        if (!AccessControl.requirePermission(this, AccessControl.MANAGE_APPOINTMENTS, "add appointments"))
            return;

        try {
            Appointment a = FormUtils.showAppointmentForm(
                    this,
                    null,
                    patientService.listAll(),
                    dentistService.listAll(),
                    clinicService.listAll()
            );

            if (a == null) return;

            appointmentService.create(a);
            refresh();

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Invalid Appointment: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // =====================================================
    // EDIT
    // =====================================================
    private void editAppointment() {
        if (!AccessControl.requirePermission(this, AccessControl.MANAGE_APPOINTMENTS, "edit appointments"))
            return;

        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a row first.");
            return;
        }

        Long id = (Long) model.getValueAt(row, 0);

        try {
            Appointment existing = appointmentService.findById(id);
            if (existing == null) {
                JOptionPane.showMessageDialog(this, "Appointment not found.");
                return;
            }

            Appointment updated = FormUtils.showAppointmentForm(
                    this,
                    existing,
                    patientService.listAll(),
                    dentistService.listAll(),
                    clinicService.listAll()
            );

            if (updated == null) return;

            appointmentService.update(updated);
            refresh();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error editing appointment: " + e.getMessage());
        }
    }

    // =====================================================
    // DELETE
    // =====================================================
    private void deleteAppointment() {
        if (!AccessControl.requirePermission(this, AccessControl.MANAGE_APPOINTMENTS, "delete appointments"))
            return;

        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a row first.");
            return;
        }

        Long id = (Long) model.getValueAt(row, 0);

        if (JOptionPane.showConfirmDialog(
                this,
                "Delete appointment ID " + id + "?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        ) != JOptionPane.YES_OPTION) return;

        try {
            appointmentService.delete(id);
            refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting appointment: " + e.getMessage());
        }
    }



    @Override
    public void search(String filter) {}
}
