package com.dentalclinic.dental.daoimpl;

import com.dentalclinic.dental.daos.DentistDao;
import com.dentalclinic.dental.model.Dentist;
import com.dentalclinic.dental.util.DbConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation for DentistDao.
 */
public class JdbcDentistDao implements DentistDao {

    @Override
    public Optional<Dentist> findById(Long id) throws Exception {
        String sql = "SELECT id, first_name, last_name, specialty, clinic_id FROM dentists WHERE id = ?";
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
    public List<Dentist> findAll() throws Exception {
        String sql = "SELECT id, first_name, last_name, specialty, clinic_id FROM dentists ORDER BY last_name, first_name";
        List<Dentist> list = new ArrayList<>();
        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public Long create(Dentist dentist) throws Exception {
        String sql = "INSERT INTO dentists (first_name, last_name, specialty, clinic_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, dentist.getFirstName());
            ps.setString(2, dentist.getLastName());
            ps.setString(3, dentist.getSpecialty());
            if (dentist.getClinicId() != null) ps.setLong(4, dentist.getClinicId());
            else ps.setNull(4, Types.BIGINT);

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        return null;
    }

    @Override
    public boolean update(Dentist dentist) throws Exception {
        String sql = "UPDATE dentists SET first_name = ?, last_name = ?, specialty = ?, clinic_id = ? WHERE id = ?";
        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dentist.getFirstName());
            ps.setString(2, dentist.getLastName());
            ps.setString(3, dentist.getSpecialty());
            if (dentist.getClinicId() != null) ps.setLong(4, dentist.getClinicId());
            else ps.setNull(4, Types.BIGINT);
            ps.setLong(5, dentist.getId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(Long id) throws Exception {
        String sql = "DELETE FROM dentists WHERE id = ?";
        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /* ---------- helper ---------- */
    private Dentist mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String first = rs.getString("first_name");
        String last = rs.getString("last_name");
        String specialty = rs.getString("specialty");
        Long clinicId = rs.getObject("clinic_id") == null ? null : rs.getLong("clinic_id");
        return new Dentist(id, first, last, specialty, clinicId);
    }
}
