package com.dentalclinic.dental.UI;

import com.dentalclinic.dental.Service.ClinicService;
import com.dentalclinic.dental.Service.ServiceService;
import com.dentalclinic.dental.Service.impl.ClinicServiceImpl;
import com.dentalclinic.dental.Service.impl.ServiceServiceImpl;
import com.dentalclinic.dental.UI.FormUtils;
import com.dentalclinic.dental.model.Clinic;
import com.dentalclinic.dental.model.Service;
import com.dentalclinic.dental.model.Session;
import com.dentalclinic.dental.security.AccessControl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class ServicesPanel extends JPanel implements CrudPanel {

    private final ServiceService serviceService = new ServiceServiceImpl();
    private final ClinicService clinicService = new ClinicServiceImpl();

    private final DefaultTableModel model =
            new DefaultTableModel(new Object[]{
                    "ID", "Clinic", "Service", "Price", "Active"
            }, 0);

    private final JTable table = new JTable(model);

    public ServicesPanel() {
        setLayout(new BorderLayout(6,6));
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Refresh");
        JButton btnAdd = new JButton("Add");
        JButton btnEdit = new JButton("Edit");
        JButton btnDisable = new JButton("Disable");

        bottom.add(btnRefresh);
        bottom.add(btnAdd);
        bottom.add(btnEdit);
        bottom.add(btnDisable);
        add(bottom, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> refresh());
        btnAdd.addActionListener(e -> addService());
        btnEdit.addActionListener(e -> editService());
        btnDisable.addActionListener(e -> disableService());

        refresh();
    }

    // =====================================================
    // LOAD TABLE
    // =====================================================
    @Override
    public void refresh() {
        model.setRowCount(0);
        try {
            List<Service> services = serviceService.listAll();
            for (Service s : services) {
                String clinicName = clinicService.findById(s.getClinicId())
                        .map(Clinic::getName)
                        .orElse("");
                model.addRow(new Object[]{
                        s.getId(),
                        clinicName,
                        s.getName(),
                        s.getPrice(),
                        s.isActive() ? "Yes" : "No"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading services: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void search(String filter) {
        refresh(); // simple panel, no search needed
    }

    // =====================================================
    // ADD
    // =====================================================
    private void addService() {
        if (!AccessControl.requirePermission(
                this, AccessControl.MANAGE_SERVICES, "add services")) return;

        try {
            List<Clinic> clinics = clinicService.listAll();
            if (clinics.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Add a clinic first.",
                        "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Service s = FormUtils.showServiceForm(this, null, clinics);
            if (s == null) return;

            serviceService.create(s);
            refresh();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error adding service: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // =====================================================
    // EDIT
    // =====================================================
    private void editService() {
        if (!AccessControl.requirePermission(
                this, AccessControl.MANAGE_SERVICES, "edit services")) return;

        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a row first.");
            return;
        }

        Long id = (Long) model.getValueAt(row, 0);

        try {
            Optional<Service> opt = serviceService.findById(id);
            if (!opt.isPresent()) {
                JOptionPane.showMessageDialog(this, "Service not found.");
                return;
            }

            Service updated = FormUtils.showServiceForm(
                    this,
                    opt.get(),
                    clinicService.listAll()
            );

            if (updated == null) return;

            serviceService.update(updated);
            refresh();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error editing service: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // =====================================================
    // DISABLE (SOFT DELETE)
    // =====================================================
    private void disableService() {
        if (!AccessControl.requirePermission(
                this, AccessControl.MANAGE_SERVICES, "disable services")) return;

        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a row first.");
            return;
        }

        Long id = (Long) model.getValueAt(row, 0);

        int ok = JOptionPane.showConfirmDialog(
                this,
                "Disable this service?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        if (ok != JOptionPane.YES_OPTION) return;

        try {
            serviceService.deactivate(id);
            refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error disabling service: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
