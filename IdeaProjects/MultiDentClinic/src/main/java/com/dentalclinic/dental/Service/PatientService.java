package com.dentalclinic.dental.Service;

import com.dentalclinic.dental.model.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientService {
    Optional<Patient> findById(Long id) throws Exception;
    List<Patient> listAll() throws Exception;
    Long create(Patient patient) throws Exception;
    boolean update(Patient patient) throws Exception;
    boolean delete(Long id) throws Exception;
}
