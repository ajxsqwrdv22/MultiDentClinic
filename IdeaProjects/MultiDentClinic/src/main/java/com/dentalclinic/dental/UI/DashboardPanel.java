package com.dentalclinic.dental.UI;

import com.dentalclinic.dental.Service.*;
import com.dentalclinic.dental.Service.impl.*;
import com.dentalclinic.dental.model.Appointment;
import com.dentalclinic.dental.model.AppointmentStatus;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class DashboardPanel extends JPanel {

    private final ClinicService clinicService = new ClinicServiceImpl();
    private final DentistService dentistService = new DentistServiceImpl();
    private final PatientService patientService = new PatientServiceImpl();
    private final AppointmentService appointmentService = new AppointmentServiceImpl();

    public DashboardPanel() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        add(buildStatsPanel(), BorderLayout.NORTH);
        add(buildAppointmentsSummary(), BorderLayout.CENTER);
    }

    // =====================================================
    // TOP STATS
    // =====================================================
    private JPanel buildStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 12, 12));

        panel.add(createCard("Clinics", safeCount(() -> clinicService.listAll().size()), new Color(52, 25, 175)));
        panel.add(createCard("Dentists", safeCount(() -> dentistService.listAll().size()), new Color(52, 25, 175)));
        panel.add(createCard("Patients", safeCount(() -> patientService.listAll().size()), new Color(52, 25, 175)));
        panel.add(createCard("Today Appointments", countTodayAppointments(), new Color(52, 25, 175)));

        return panel;
    }

    private JPanel createCard(String title, int value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        card.setBackground(color);

        JLabel lblValue = new JLabel(String.valueOf(value), SwingConstants.CENTER);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblValue.setForeground(Color.WHITE);

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);

        card.add(lblValue, BorderLayout.CENTER);
        card.add(lblTitle, BorderLayout.SOUTH);

        return card;
    }

    // =====================================================
    // APPOINTMENT SUMMARY
    // =====================================================
    private JPanel buildAppointmentsSummary() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 12, 12));
        panel.setBorder(BorderFactory.createTitledBorder("Appointments Overview"));

        List<Appointment> list;
        try {
            list = appointmentService.listAll();
        } catch (Exception e) {
            list = List.of();
        }

        panel.add(summaryCard("UPCOMING",
                list.stream().filter(a -> a.getStatus() == AppointmentStatus.BOOKED).count()));

        panel.add(summaryCard("SCHEDULED",
                list.stream().filter(a -> a.getStatus() == AppointmentStatus.SCHEDULED).count()));

        panel.add(summaryCard("Completed",
                list.stream().filter(a -> a.getStatus() == AppointmentStatus.COMPLETED).count()));

        panel.add(summaryCard("Cancelled",
                list.stream().filter(a -> a.getStatus() == AppointmentStatus.CANCELLED).count()));

        panel.add(summaryCard("Total",
                list.size()));

        return panel;
    }

    private JPanel summaryCard(String title, long count) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        p.setBackground(Color.WHITE);

        JLabel lblCount = new JLabel(String.valueOf(count), SwingConstants.CENTER);
        lblCount.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));

        p.add(lblCount, BorderLayout.CENTER);
        p.add(lblTitle, BorderLayout.SOUTH);
        return p;
    }

    // =====================================================
    // HELPERS
    // =====================================================
    private int safeCount(CountSupplier supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return 0;
        }
    }

    private int countTodayAppointments() {
        try {
            LocalDate today = LocalDate.now();
            return (int) appointmentService.listAll().stream()
                    .filter(a -> a.getScheduledAt() != null)
                    .filter(a -> a.getScheduledAt().toLocalDate().equals(today))
                    .count();
        } catch (Exception e) {
            return 0;
        }
    }

    @FunctionalInterface
    private interface CountSupplier {
        int get() throws Exception;
    }
}
