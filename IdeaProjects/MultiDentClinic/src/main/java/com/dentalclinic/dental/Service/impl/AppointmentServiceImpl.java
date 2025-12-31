package com.dentalclinic.dental.Service.impl;

import com.dentalclinic.dental.Service.AppointmentService;
import com.dentalclinic.dental.daos.AppointmentDao;
import com.dentalclinic.dental.daoimpl.JdbcAppointmentDao;
import com.dentalclinic.dental.model.Appointment;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentDao appointmentDao = new JdbcAppointmentDao();

    @Override
    public List<Appointment> listAll() throws Exception {
        return appointmentDao.findAll();
    }

    @Override
    public Appointment findById(Long id) throws Exception {
        return appointmentDao.findById(id).orElse(null);
    }

    @Override
    public Long create(Appointment appointment) throws Exception {
        validateAppointment(appointment);
        validateNotInPast(appointment);
        validateClinicHours(appointment);
        validateNoDuplicate(appointment);
        return appointmentDao.create(appointment);
    }

    @Override
    public boolean update(Appointment appointment) throws Exception {
        if (appointment.getId() == null) {
            throw new IllegalArgumentException("Appointment ID is required for update.");
        }

        validateAppointment(appointment);
        validateNotInPast(appointment);
        validateClinicHours(appointment);
        validateNoDuplicate(appointment);

        return appointmentDao.update(appointment);
    }

    @Override
    public boolean delete(Long id) throws Exception {
        return appointmentDao.delete(id);
    }

    @Override
    public List<Appointment> findByPatientId(Long patientId) throws Exception {
        return appointmentDao.findByPatientId(patientId);
    }

    // =====================================================
    // CORE VALIDATION
    // =====================================================

    private void validateAppointment(Appointment a) {
        if (a == null) {
            throw new IllegalArgumentException("Appointment cannot be null.");
        }

        if (a.getPatientId() == null) {
            throw new IllegalArgumentException("Patient must be selected.");
        }

        if (a.getDentistId() == null) {
            throw new IllegalArgumentException("Dentist must be selected.");
        }

        if (a.getClinicId() == null) {
            throw new IllegalArgumentException("Clinic must be selected.");
        }

        if (a.getServiceId() == null) {
            throw new IllegalArgumentException("Service must be selected.");
        }

        if (a.getScheduledAt() == null) {
            throw new IllegalArgumentException("Appointment date and time is required.");
        }

        if (a.getStatus() == null) {
            throw new IllegalArgumentException("Appointment status is required.");
        }
    }

    // =====================================================
    // PHASE 2 RULES
    // =====================================================

    // 1️⃣ No past appointments
    private void validateNotInPast(Appointment a) {
        if (a.getScheduledAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Appointments in the past are not allowed."
            );
        }
    }

    // 2️⃣ Clinic operating hours (9 AM – 5 PM)
    private void validateClinicHours(Appointment a) {
        LocalTime time = a.getScheduledAt().toLocalTime();

        if (time.isBefore(LocalTime.of(9, 0)) ||
                time.isAfter(LocalTime.of(17, 0))) {

            throw new IllegalArgumentException(
                    "Clinic operates only between 9:00 AM and 5:00 PM."
            );
        }
    }

    // 3️⃣ Prevent duplicate & double booking
    private void validateNoDuplicate(Appointment a) throws Exception {
        boolean exists = appointmentDao.existsDuplicate(
                a.getDentistId(),
                a.getClinicId(),
                a.getScheduledAt(),
                a.getId() // allow self when editing
        );

        if (exists) {
            throw new IllegalArgumentException(
                    "This dentist already has an appointment at the selected date and time."
            );
        }
    }
}
