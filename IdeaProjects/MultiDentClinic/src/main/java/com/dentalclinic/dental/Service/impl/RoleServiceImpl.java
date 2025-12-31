package com.dentalclinic.dental.Service.impl;

import com.dentalclinic.dental.Service.RoleService;
import com.dentalclinic.dental.daoimpl.JdbcRoleDao;
import com.dentalclinic.dental.daos.RoleDao;
import com.dentalclinic.dental.model.Role;

import java.util.List;
import java.util.Optional;

public class RoleServiceImpl implements RoleService {
    private final RoleDao dao = new JdbcRoleDao();

    @Override
    public Optional<Role> findById(Long id) throws Exception {
        return dao.findById(id);
    }

    @Override
    public Optional<Role> findByName(String name) throws Exception {
        return dao.findByName(name);
    }


    public List<Role> listAll() throws Exception {
        return dao.findAll();
    }

    /**
     * Convenience alias used by some UI code.
     */
    public List<Role> findAll() throws Exception {
        return listAll();
    }
}
