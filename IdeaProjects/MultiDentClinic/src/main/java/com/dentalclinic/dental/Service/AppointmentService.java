package com.dentalclinic.dental.Service;

import com.dentalclinic.dental.model.Appointment;

import java.util.List;

public interface AppointmentService {

    List<Appointment> listAll() throws Exception;

    Appointment findById(Long id) throws Exception;

    Long create(Appointment appointment) throws Exception;

    boolean update(Appointment appointment) throws Exception;

    boolean delete(Long id) throws Exception;

    List<Appointment> findByPatientId(Long patientId) throws Exception;
}
