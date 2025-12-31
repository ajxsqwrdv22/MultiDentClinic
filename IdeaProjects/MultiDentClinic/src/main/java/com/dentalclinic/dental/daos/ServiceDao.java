package com.dentalclinic.dental.daos;

import com.dentalclinic.dental.model.Service;
import java.util.Optional;
import java.util.List;

public interface ServiceDao {
    Optional<Service> findById(Long id) throws Exception;
    List<Service> findAll() throws Exception;
    Long create(Service s) throws Exception;
    boolean update(Service s) throws Exception;
    boolean deleteById(Long id) throws Exception;
    List<Service> findActiveByClinic(Long clinicId) throws Exception;
    boolean deactivate(Long id) throws Exception;
}
