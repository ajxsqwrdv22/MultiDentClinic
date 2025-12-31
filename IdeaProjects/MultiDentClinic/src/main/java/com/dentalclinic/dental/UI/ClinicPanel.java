package com.dentalclinic.dental.UI;

import com.dentalclinic.dental.Service.ClinicService;
import com.dentalclinic.dental.Service.impl.ClinicServiceImpl;
import com.dentalclinic.dental.model.Clinic;
import com.dentalclinic.dental.security.AccessControl;
import com.dentalclinic.dental.model.Session;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class ClinicPanel extends JPanel implements CrudPanel {
    private final ClinicService service = new ClinicServiceImpl();
    private final DefaultTableModel model =
            new DefaultTableModel(new Object[]{"ID", "Name", "Address"}, 0);
    private final JTable table = new JTable(model);
    private boolean sortAsc;

    public ClinicPanel() {
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

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Add");
        JButton edit = new JButton("Edit");
        JButton delete = new JButton("Delete");

        bottom.add(add);
        bottom.add(edit);
        bottom.add(delete);
        bottom.add(btnSort);
        add(bottom, BorderLayout.NORTH);

        add.addActionListener(e -> addClinic());
        edit.addActionListener(e -> editClinic());
        delete.addActionListener(e -> deleteClinic());

        refresh();
    }

    @Override
    public void refresh() {
        search(null);
    }

    @Override
    public void search(String filter) {
        model.setRowCount(0);
        try {
            List<Clinic> list = service.listAll();
            String f = filter == null ? "" : filter.trim().toLowerCase();
            for (Clinic c : list) {
                if (f.isEmpty() || c.getName().toLowerCase().contains(f)) {
                    model.addRow(new Object[]{
                            c.getId(), c.getName(), c.getAddress()
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading clinics: " + e.getMessage());
        }
    }

    private void addClinic() {
        if (!AccessControl.requirePermission(
                this,
                AccessControl.MANAGE_CLINICS,
                "add clinics"
        )) return;

        Clinic c = FormUtils.showClinicForm(this, null);
        if (c == null) return;
        try {
            service.create(c);
            refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error creating clinic: " + e.getMessage());
        }
    }

    private void editClinic() {
        if (!AccessControl.requirePermission(
                this,
                AccessControl.MANAGE_CLINICS,
                "edit clinics"
        )) return;

        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a row."); return; }

        Long id = (Long) model.getValueAt(row, 0);

        try {
            Optional<Clinic> oc = service.findById(id);
            if (!oc.isPresent()) {
                JOptionPane.showMessageDialog(this, "Clinic not found.");
                return;
            }
            Clinic updated = FormUtils.showClinicForm(this, oc.get());
            if (updated != null) {
                service.update(updated);
                refresh();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error editing clinic: " + e.getMessage());
        }
    }

    private void deleteClinic() {
        if (!AccessControl.requirePermission(
                this,
                AccessControl.MANAGE_CLINICS,
                "delete clinics"
        )) return;

        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a row."); return; }

        Long id = (Long) model.getValueAt(row, 0);

        if (JOptionPane.showConfirmDialog(this, "Delete clinic ID " + id + "?",
                "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        try {
            service.delete(id);
            refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting clinic: " + e.getMessage());
        }
    }
}
