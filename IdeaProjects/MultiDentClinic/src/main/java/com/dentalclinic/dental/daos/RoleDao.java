package com.dentalclinic.dental.daos;

import com.dentalclinic.dental.model.Role;
import java.util.Optional;
import java.util.List;

public interface RoleDao {
    Optional<Role> findById(Long id) throws Exception;
    Optional<Role> findByName(String name) throws Exception;
    List<Role> findAll() throws Exception;
}
