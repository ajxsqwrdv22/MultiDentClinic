package com.dentalclinic.dental.Service.impl;

import com.dentalclinic.dental.Service.ServiceService;
import com.dentalclinic.dental.daos.ServiceDao;
import com.dentalclinic.dental.daoimpl.JdbcServiceDao;
import com.dentalclinic.dental.model.Service;

import java.util.List;
import java.util.Optional;

/**
 * ServiceServiceImpl delegates to JdbcServiceDao
 * Uses SOFT DELETE (deactivate) instead of hard delete.
 */
public class ServiceServiceImpl implements ServiceService {

    private final ServiceDao dao = new JdbcServiceDao();

    // =====================================================
    // LIST ALL (ADMIN)
    // =====================================================
    @Override
    public List<Service> listAll() {
        try {
            return dao.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Failed to list services", e);
        }
    }

    // =====================================================
    // FIND BY ID
    // =====================================================
    @Override
    public Optional<Service> findById(Long id) {
        try {
            return dao.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find service by id: " + id, e);
        }
    }

    // =====================================================
    // CREATE
    // =====================================================
    @Override
    public Long create(Service service) throws Exception {
        validateService(service);
        return dao.create(service);
    }

    // =====================================================
    // UPDATE
    // =====================================================
    @Override
    public boolean update(Service service) throws Exception {
        validateService(service);
        return dao.update(service);
    }

    // =====================================================
    // DEACTIVATE (SOFT DELETE)
    // =====================================================

    @Override
    public boolean deactivate(Long id) throws Exception {
        if (id == null) {
            throw new IllegalArgumentException("Service id is required.");
        }
        return dao.deactivate(id);
    }

    // =====================================================
    // VALIDATION
    // =====================================================
    private void validateService(Service s) {
        if (s == null) {
            throw new IllegalArgumentException("Service cannot be null.");
        }
        if (s.getClinicId() == null) {
            throw new IllegalArgumentException("Clinic is required for service.");
        }
        if (s.getName() == null || s.getName().isBlank()) {
            throw new IllegalArgumentException("Service name is required.");
        }
        if (s.getPrice() < 0) {
            throw new IllegalArgumentException("Service price must be non-negative.");
        }
    }
}
