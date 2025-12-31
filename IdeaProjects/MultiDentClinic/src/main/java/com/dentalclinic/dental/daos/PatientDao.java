package com.dentalclinic.dental.daos;

import com.dentalclinic.dental.model.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientDao {
    Optional<Patient> findById(Long id) throws Exception;
    List<Patient> findAll() throws Exception;
    Long create(Patient p) throws Exception;
    boolean update(Patient p) throws Exception;
    boolean deleteById(Long id) throws Exception;
}
