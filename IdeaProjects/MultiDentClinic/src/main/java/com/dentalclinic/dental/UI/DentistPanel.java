package com.dentalclinic.dental.UI;

import com.dentalclinic.dental.Service.DentistService;
import com.dentalclinic.dental.Service.ClinicService;
import com.dentalclinic.dental.Service.impl.DentistServiceImpl;
import com.dentalclinic.dental.Service.impl.ClinicServiceImpl;
import com.dentalclinic.dental.model.Dentist;
import com.dentalclinic.dental.model.Clinic;
import com.dentalclinic.dental.security.AccessControl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class DentistPanel extends JPanel implements CrudPanel {
    private final DentistService dentistService = new DentistServiceImpl();
    private final ClinicService clinicService = new ClinicServiceImpl();

    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"ID","First Name","Last Name","Specialty","Clinic"}, 0);
    private final JTable table = new JTable(model);
    private boolean sortAsc;

    public DentistPanel() {
        //Sorter
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JButton btnSort = new JButton("Sort");

        btnSort.addActionListener(e -> {
            int columnIndex = 2;

            SortOrder order = sortAsc ? SortOrder.DESCENDING : SortOrder.ASCENDING;
            sorter.setSortKeys(List.of(new RowSorter.SortKey(columnIndex, order)));
        });

        setLayout(new BorderLayout(6,6));
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Add");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");
        JButton btnRefresh = new JButton("Refresh");
        btns.add(btnSort);
        btns.add(btnRefresh);
        btns.add(btnAdd);
        btns.add(btnEdit);
        btns.add(btnDelete);
        add(btns, BorderLayout.NORTH);

        btnRefresh.addActionListener(e -> refresh());
        btnAdd.addActionListener(e -> addDentist());
        btnEdit.addActionListener(e -> editDentist());
        btnDelete.addActionListener(e -> deleteDentist());

        refresh();
    }

    @Override
    public void refresh() {
        model.setRowCount(0);
        try {
            List<Dentist> list = dentistService.listAll();
            for (Dentist d : list) {
                String clinicName = "";
                if (d.getClinicId() != null) {
                    clinicName = clinicService.findById(d.getClinicId()).map(Clinic::getName).orElse("");
                }
                model.addRow(new Object[]{d.getId(), d.getFirstName(), d.getLastName(), d.getSpecialty(), clinicName});
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading dentists: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void search(String filter) {
        // simple client-side filter (by name/specialty/clinic)
        model.setRowCount(0);
        try {
            List<Dentist> list = dentistService.listAll();
            for (Dentist d : list) {
                String clinicName = "";
                if (d.getClinicId() != null) clinicName = clinicService.findById(d.getClinicId()).map(Clinic::getName).orElse("");
                String all = (d.getFirstName()==null?"":d.getFirstName()) + " " + (d.getLastName()==null?"":d.getLastName()) + " " + (d.getSpecialty()==null?"":d.getSpecialty()) + " " + clinicName;
                if (filter == null || filter.trim().isEmpty() || all.toLowerCase().contains(filter.toLowerCase())) {
                    model.addRow(new Object[]{d.getId(), d.getFirstName(), d.getLastName(), d.getSpecialty(), clinicName});
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addDentist() {
        if (!AccessControl.requirePermission(this, AccessControl.MANAGE_DENTISTS, "add dentists")) return;
        try {
            List<Clinic> clinics = clinicService.listAll();
            Dentist created = FormUtils.showDentistForm(this, null, clinics);
            if (created == null) return;
            dentistService.create(created);
            refresh();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating dentist: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editDentist() {
        if (!AccessControl.requirePermission(this, AccessControl.MANAGE_DENTISTS, "edit dentists")) return;
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Select a row"); return; }
        Long id = (Long) model.getValueAt(r, 0);
        try {
            Optional<Dentist> od = dentistService.findById(id);
            if (!od.isPresent()) { JOptionPane.showMessageDialog(this, "Not found"); return; }
            List<Clinic> clinics = clinicService.listAll();
            Dentist updated = FormUtils.showDentistForm(this, od.get(), clinics);
            if (updated == null) return;
            boolean ok = dentistService.update(updated);
            JOptionPane.showMessageDialog(this, ok ? "Updated" : "No change");
            refresh();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error editing dentist: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteDentist() {
        if (!AccessControl.requirePermission(this, AccessControl.MANAGE_DENTISTS, "delete dentists")) return;
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Select a row"); return; }
        Long id = (Long) model.getValueAt(r, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete dentist id " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            boolean d = dentistService.delete(id);
            JOptionPane.showMessageDialog(this, d ? "Deleted" : "Not deleted");
            refresh();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting dentist: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
