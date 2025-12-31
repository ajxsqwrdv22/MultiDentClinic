package com.dentalclinic.dental.daos;

import com.dentalclinic.dental.model.Dentist;

import java.util.List;
import java.util.Optional;

public interface DentistDao {
    Optional<Dentist> findById(Long id) throws Exception;
    List<Dentist> findAll() throws Exception;
    Long create(Dentist d) throws Exception;
    boolean update(Dentist d) throws Exception;
    boolean deleteById(Long id) throws Exception;
}
