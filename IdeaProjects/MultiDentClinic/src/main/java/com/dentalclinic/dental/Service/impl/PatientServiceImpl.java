package com.dentalclinic.dental.Service.impl;

import com.dentalclinic.dental.Service.PatientService;
import com.dentalclinic.dental.daos.PatientDao;
import com.dentalclinic.dental.daoimpl.JdbcPatientDao;
import com.dentalclinic.dental.model.Patient;

import java.util.List;
import java.util.Optional;

public class PatientServiceImpl implements PatientService {

    private final PatientDao dao = new JdbcPatientDao();

    @Override
    public List<Patient> listAll() throws Exception {
        return dao.findAll();
    }

    @Override
    public Optional<Patient> findById(Long id) throws Exception {
        return dao.findById(id);
    }

    @Override
    public Long create(Patient p) throws Exception {
        validate(p);
        checkDuplicate(p);
        return dao.create(p);
    }

    @Override
    public boolean update(Patient p) throws Exception {
        validate(p);
        checkDuplicate(p);
        return dao.update(p);
    }

    @Override
    public boolean delete(Long id) throws Exception {
        return dao.deleteById(id);
    }

    // =========================
    // VALIDATION
    // =========================
    private void validate(Patient p) {
        if (p.getFirstName() == null || p.getFirstName().trim().isEmpty())
            throw new IllegalArgumentException("First name is required");

        if (p.getLastName() == null || p.getLastName().trim().isEmpty())
            throw new IllegalArgumentException("Last name is required");

        if (p.getContact() == null || p.getContact().trim().isEmpty())
            throw new IllegalArgumentException("Contact is required");
    }

    // =========================
    // DUPLICATE CHECK
    // =========================
    private void checkDuplicate(Patient p) throws Exception {
        for (Patient x : dao.findAll()) {

            if (p.getId() != null && p.getId().equals(x.getId()))
                continue;

            if (x.getFirstName().equalsIgnoreCase(p.getFirstName())
                    && x.getLastName().equalsIgnoreCase(p.getLastName())
                    && x.getContact().equalsIgnoreCase(p.getContact())) {

                throw new IllegalArgumentException(
                        "Patient already exists (same name and contact)."
                );
            }
        }
    }
}
