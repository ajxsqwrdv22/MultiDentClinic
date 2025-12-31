package com.dentalclinic.dental.UI;

import com.dentalclinic.dental.Service.PatientService;
import com.dentalclinic.dental.Service.impl.PatientServiceImpl;
import com.dentalclinic.dental.model.Patient;
import com.dentalclinic.dental.security.AccessControl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.util.Optional;

/**
 * Robust PatientPanel:
 * - Uses FormUtils.showPatientForm for add/edit
 * - QuickAdd opens the same form prefilled (instead of inserting John Doe blindly)
 * - Delete button present and confirmed
 * - Safely reads ID from table even if DefaultTableModel stored Integer
 */
public class PatientPanel extends JPanel implements CrudPanel {
    private final PatientService patientService = new PatientServiceImpl();

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "First Name", "Last Name", "Contact", "Address", "Created"}, 0
    );

    private final JTable table = new JTable(model);
    private boolean sortAsc;

    public PatientPanel() {
        //Sorter
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JButton btnSort = new JButton("Sort");

        btnSort.addActionListener(e -> {
            int columnIndex = 1;

            SortOrder order = sortAsc ? SortOrder.ASCENDING : SortOrder.DESCENDING;
            sorter.setSortKeys(List.of(new RowSorter.SortKey(columnIndex, order)));
        });
        setLayout(new BorderLayout(6,6));
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Refresh");
        JButton btnAdd = new JButton("Add");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");

        controls.add(btnRefresh);
        controls.add(btnAdd);
        controls.add(btnEdit);
        controls.add(btnDelete);
        controls.add(btnSort);
        add(controls, BorderLayout.NORTH);

        btnRefresh.addActionListener(e -> refresh());
        btnAdd.addActionListener(e -> addPatient());
        btnEdit.addActionListener(e -> editPatient());
        btnDelete.addActionListener(e -> deletePatient());


        refresh();
    }

    @Override
    public void refresh() {
        model.setRowCount(0);
        try {
            List<Patient> list = patientService.listAll();
            for (Patient p : list) {
                String created = p.getCreatedAt() == null ? "" : p.getCreatedAt().toString();
                model.addRow(new Object[]{p.getId(), p.getFirstName(), p.getLastName(), p.getContact(), p.getAddress(), created});
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading patients: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void search(String filter) {
        model.setRowCount(0);
        try {
            List<Patient> list = patientService.listAll();
            for (Patient p : list) {
                String full = (p.getFirstName()==null?"":p.getFirstName()) + " " + (p.getLastName()==null?"":p.getLastName());
                if (filter == null || filter.trim().isEmpty() || full.toLowerCase().contains(filter.toLowerCase())) {
                    String created = p.getCreatedAt() == null ? "" : p.getCreatedAt().toString();
                    model.addRow(new Object[]{p.getId(), p.getFirstName(), p.getLastName(), p.getContact(), p.getAddress(), created});
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addPatient() {
        if (!AccessControl.requirePermission(this, AccessControl.MANAGE_PATIENTS, "add patients")) return;
        // open form (empty)
        Patient p = FormUtils.showPatientForm(this, null);
        if (p == null) return;
        try {
            Long id = patientService.create(p);
            if (id != null) {
                JOptionPane.showMessageDialog(this, "Patient created (id=" + id + ").");
            }
            refresh();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating patient: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editPatient() {
        if (!AccessControl.requirePermission(this, AccessControl.MANAGE_PATIENTS, "edit patients")) return;
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Select a row to edit."); return; }

        Long id = safeGetIdAtRow(r);
        if (id == null) { JOptionPane.showMessageDialog(this, "Unable to read selected id."); return; }

        try {
            Optional<Patient> op = patientService.findById(id);
            if (!op.isPresent()) { JOptionPane.showMessageDialog(this, "Patient not found."); return; }

            Patient updated = FormUtils.showPatientForm(this, op.get());
            if (updated == null) return;

            boolean ok = patientService.update(updated);
            JOptionPane.showMessageDialog(this, ok ? "Updated." : "No change.");
            refresh();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error editing patient: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePatient() {
        if (!AccessControl.requirePermission(this, AccessControl.MANAGE_PATIENTS, "delete patients")) return;
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Select a row to delete."); return; }

        Long id = safeGetIdAtRow(r);
        if (id == null) { JOptionPane.showMessageDialog(this, "Unable to read selected id."); return; }

        if (JOptionPane.showConfirmDialog(this, "Delete patient id " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        try {
            boolean d = patientService.delete(id);
            JOptionPane.showMessageDialog(this, d ? "Deleted." : "Not deleted.");
            refresh();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting patient: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Safe id retrieval because DefaultTableModel might store Integer or Long.
     */
    private Long safeGetIdAtRow(int row) {
        Object o = model.getValueAt(row, 0);
        if (o == null) return null;
        if (o instanceof Long) return (Long) o;
        if (o instanceof Integer) return ((Integer) o).longValue();
        if (o instanceof Number) return ((Number) o).longValue();
        try {
            return Long.parseLong(o.toString());
        } catch (Exception ignored) { return null; }
    }
}
