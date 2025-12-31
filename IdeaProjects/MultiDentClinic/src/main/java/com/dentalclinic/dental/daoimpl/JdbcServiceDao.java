package com.dentalclinic.dental.daoimpl;

import com.dentalclinic.dental.daos.ServiceDao;
import com.dentalclinic.dental.model.Service;
import com.dentalclinic.dental.util.DbConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcServiceDao implements ServiceDao {

    // =====================================================
    // FIND BY ID
    // =====================================================
    @Override
    public Optional<Service> findById(Long id) throws Exception {
        String sql = """
            SELECT id, clinic_id, name, price, active
            FROM services
            WHERE id = ?
        """;

        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        }
        return Optional.empty();
    }

    // =====================================================
    // FIND ALL (INCLUDING INACTIVE – ADMIN VIEW)
    // =====================================================
    @Override
    public List<Service> findAll() throws Exception {
        String sql = """
            SELECT id, clinic_id, name, price, active
            FROM services
            ORDER BY name
        """;

        List<Service> list = new ArrayList<>();

        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    // =====================================================
    // FIND ACTIVE SERVICES BY CLINIC (IMPORTANT)
    // =====================================================
    @Override
    public List<Service> findActiveByClinic(Long clinicId) throws Exception {
        String sql = """
            SELECT id, clinic_id, name, price, active
            FROM services
            WHERE clinic_id = ? AND active = 1
            ORDER BY name
        """;

        List<Service> list = new ArrayList<>();

        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, clinicId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    // =====================================================
    // CREATE
    // =====================================================
    @Override
    public Long create(Service s) throws Exception {
        String sql = """
            INSERT INTO services (clinic_id, name, price, active)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, s.getClinicId());
            ps.setString(2, s.getName());
            ps.setDouble(3, s.getPrice());
            ps.setBoolean(4, s.isActive());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        return null;
    }

    // =====================================================
    // UPDATE
    // =====================================================
    @Override
    public boolean update(Service s) throws Exception {
        String sql = """
            UPDATE services
            SET clinic_id = ?, name = ?, price = ?, active = ?
            WHERE id = ?
        """;

        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, s.getClinicId());
            ps.setString(2, s.getName());
            ps.setDouble(3, s.getPrice());
            ps.setBoolean(4, s.isActive());
            ps.setLong(5, s.getId());

            return ps.executeUpdate() > 0;
        }
    }

    // =====================================================
    // SOFT DELETE (DISABLE)
    // =====================================================
    @Override
    public boolean deactivate(Long id) throws Exception {
        String sql = "UPDATE services SET active = 0 WHERE id = ?";

        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // =====================================================
    // HARD DELETE (OPTIONAL – ADMIN ONLY)
    // =====================================================
    @Override
    public boolean deleteById(Long id) throws Exception {
        String sql = "DELETE FROM services WHERE id = ?";

        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // =====================================================
    // ROW MAPPER
    // =====================================================
    private Service map(ResultSet rs) throws SQLException {
        Service s = new Service();
        s.setId(rs.getLong("id"));
        s.setClinicId(rs.getLong("clinic_id"));
        s.setName(rs.getString("name"));
        s.setPrice(rs.getDouble("price"));
        s.setActive(rs.getBoolean("active"));
        return s;
    }
}
