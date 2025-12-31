package com.dentalclinic.dental.Service.impl;

import com.dentalclinic.dental.Service.DentistService;
import com.dentalclinic.dental.daos.DentistDao;
import com.dentalclinic.dental.daoimpl.JdbcDentistDao;
import com.dentalclinic.dental.model.Dentist;

import java.util.List;
import java.util.Optional;

public class DentistServiceImpl implements DentistService {

    private final DentistDao dao = new JdbcDentistDao();

    @Override
    public List<Dentist> listAll() throws Exception {
        return dao.findAll();
    }

    @Override
    public Optional<Dentist> findById(Long id) throws Exception {
        return dao.findById(id);
    }

    @Override
    public Long create(Dentist d) throws Exception {
        validate(d);
        checkDuplicate(d);
        return dao.create(d);
    }

    @Override
    public boolean update(Dentist d) throws Exception {
        validate(d);
        checkDuplicate(d);
        return dao.update(d);
    }

    @Override
    public boolean delete(Long id) throws Exception {
        return dao.deleteById(id);
    }

    // =========================
    // VALIDATION
    // =========================
    private void validate(Dentist d) {
        if (d.getFirstName() == null || d.getFirstName().trim().isEmpty())
            throw new IllegalArgumentException("First name is required");

        if (d.getLastName() == null || d.getLastName().trim().isEmpty())
            throw new IllegalArgumentException("Last name is required");

        if (d.getClinicId() == null)
            throw new IllegalArgumentException("Clinic is required");
    }

    // =========================
    // DUPLICATE CHECK
    // =========================
    private void checkDuplicate(Dentist d) throws Exception {
        for (Dentist x : dao.findAll()) {

            if (d.getId() != null && d.getId().equals(x.getId()))
                continue;

            if (x.getClinicId().equals(d.getClinicId())
                    && x.getFirstName().equalsIgnoreCase(d.getFirstName())
                    && x.getLastName().equalsIgnoreCase(d.getLastName())) {

                throw new IllegalArgumentException(
                        "Dentist already exists in this clinic."
                );
            }
        }
    }
}
