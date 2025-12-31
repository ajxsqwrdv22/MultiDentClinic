package com.dentalclinic.dental.daoimpl;

import com.dentalclinic.dental.daos.PatientDao;
import com.dentalclinic.dental.model.Patient;
import com.dentalclinic.dental.util.DbConnectionPool;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation for PatientDao.
 */
public class JdbcPatientDao implements PatientDao {

    @Override
    public Optional<Patient> findById(Long id) throws Exception {
        String sql = "SELECT id, first_name, last_name, contact, address, created_at FROM patients WHERE id = ?";
        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Patient> findAll() throws Exception {
        String sql = "SELECT id, first_name, last_name, contact, address, created_at FROM patients ORDER BY last_name, first_name";
        List<Patient> list = new ArrayList<>();
        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public Long create(Patient patient) throws Exception {
        String sql = "INSERT INTO patients (first_name, last_name, contact, address) VALUES (?, ?, ?, ?)";
        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, patient.getFirstName());
            ps.setString(2, patient.getLastName());
            if (patient.getContact() != null) ps.setString(3, patient.getContact()); else ps.setNull(3, Types.VARCHAR);
            if (patient.getAddress() != null) ps.setString(4, patient.getAddress()); else ps.setNull(4, Types.VARCHAR);

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        return null;
    }

    @Override
    public boolean update(Patient patient) throws Exception {
        String sql = "UPDATE patients SET first_name = ?, last_name = ?, contact = ?, address = ? WHERE id = ?";
        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, patient.getFirstName());
            ps.setString(2, patient.getLastName());
            if (patient.getContact() != null) ps.setString(3, patient.getContact()); else ps.setNull(3, Types.VARCHAR);
            if (patient.getAddress() != null) ps.setString(4, patient.getAddress()); else ps.setNull(4, Types.VARCHAR);
            ps.setLong(5, patient.getId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(Long id) throws Exception {
        String sql = "DELETE FROM patients WHERE id = ?";
        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /* ---------- helpers ---------- */

    private Patient mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String first = rs.getString("first_name");
        String last = rs.getString("last_name");
        String contact = rs.getString("contact");
        String address = rs.getString("address");
        Timestamp ts = rs.getTimestamp("created_at");
        Instant createdAt = ts == null ? null : ts.toInstant();

        Patient p = new Patient();
        p.setId(id);
        p.setFirstName(first);
        p.setLastName(last);
        p.setContact(contact);
        p.setAddress(address);
        p.setCreatedAt(createdAt);
        return p;
    }
}
