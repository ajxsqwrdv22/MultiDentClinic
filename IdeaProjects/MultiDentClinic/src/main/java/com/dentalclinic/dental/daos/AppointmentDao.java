package com.dentalclinic.dental.daos;

import com.dentalclinic.dental.model.Appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentDao {

    Optional<Appointment> findById(Long id) throws Exception;

    List<Appointment> findAll() throws Exception;

    List<Appointment> findByPatientId(Long patientId) throws Exception;

    Long create(Appointment appointment) throws Exception;

    boolean update(Appointment appointment) throws Exception;

    boolean delete(Long id) throws Exception;

    boolean existsDuplicate(
            Long dentistId,
            Long clinicId,
            LocalDateTime scheduledAt,
            Long excludeId
    ) throws Exception;

}
