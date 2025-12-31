package com.dentalclinic.dental.Service;

import com.dentalclinic.dental.model.Dentist;

import java.util.List;
import java.util.Optional;

public interface DentistService {
    Optional<Dentist> findById(Long id) throws Exception;
    List<Dentist> listAll() throws Exception;
    Long create(Dentist dentist) throws Exception;
    boolean update(Dentist dentist) throws Exception;
    boolean delete(Long id) throws Exception;
}
