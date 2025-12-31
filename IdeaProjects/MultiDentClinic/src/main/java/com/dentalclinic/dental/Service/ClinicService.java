package com.dentalclinic.dental.Service;

import com.dentalclinic.dental.model.Clinic;

import java.util.List;
import java.util.Optional;

public interface ClinicService {
    Optional<Clinic> findById(Long id) throws Exception;
    List<Clinic> listAll() throws Exception;
    Long create(Clinic clinic) throws Exception;
    boolean update(Clinic clinic) throws Exception;
    boolean delete(Long id) throws Exception;
}
