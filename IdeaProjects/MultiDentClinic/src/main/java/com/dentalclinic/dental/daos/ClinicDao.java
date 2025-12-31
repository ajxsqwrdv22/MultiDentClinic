package com.dentalclinic.dental.daos;

import com.dentalclinic.dental.model.Clinic;

import java.util.List;
import java.util.Optional;

public interface ClinicDao {
    Optional<Clinic> findById(Long id) throws Exception;
    List<Clinic> findAll() throws Exception;
    Long create(Clinic c) throws Exception;
    boolean update(Clinic c) throws Exception;
    boolean deleteById(Long id) throws Exception;
}
