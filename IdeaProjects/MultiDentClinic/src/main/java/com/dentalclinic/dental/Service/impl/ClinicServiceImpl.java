package com.dentalclinic.dental.Service.impl;

import com.dentalclinic.dental.Service.ClinicService;
import com.dentalclinic.dental.daos.ClinicDao;
import com.dentalclinic.dental.daoimpl.JdbcClinicDao;
import com.dentalclinic.dental.model.Clinic;

import java.util.List;
import java.util.Optional;

public class ClinicServiceImpl implements ClinicService {
    private final ClinicDao dao = new JdbcClinicDao();

    @Override public Optional<Clinic> findById(Long id) throws Exception { return dao.findById(id); }
    @Override public List<Clinic> listAll() throws Exception { return dao.findAll(); }
    @Override public Long create(Clinic clinic) throws Exception {
        validateClinic(clinic);
        return dao.create(clinic);
    }
    @Override public boolean update(Clinic clinic) throws Exception {
        validateClinic(clinic);
        return dao.update(clinic); }
    @Override public boolean delete(Long id) throws Exception { return dao.deleteById(id); }

    private void validateClinic(Clinic c) {
        if (c == null) {
            throw new IllegalArgumentException("Clinic cannot be null.");
        }
        if (c.getName() == null || c.getName().isBlank()) {
            throw new IllegalArgumentException("Clinic name is required");
        }
    }
}
