package com.dentalclinic.dental.Service;

import com.dentalclinic.dental.model.Service;
import java.util.List;
import java.util.Optional;

public interface ServiceService {
    List<Service> listAll() throws Exception;
    Optional<Service> findById(Long id) throws Exception;
    Long create(Service s) throws Exception;
    boolean update(Service s) throws Exception;
    boolean deactivate(Long id) throws Exception;
}
