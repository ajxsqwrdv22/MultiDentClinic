package com.dentalclinic.dental.daoimpl;

import com.dentalclinic.dental.daos.ClinicDao;
import com.dentalclinic.dental.model.Clinic;
import com.dentalclinic.dental.util.DbConnectionPool;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcClinicDao implements ClinicDao {

    @Override
    public Optional<Clinic> findById(Long id) throws Exception {
        String sql = "SELECT id, name, address, phone, created_at FROM clinics WHERE id = ?";
        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Clinic> findAll() throws Exception {
        String sql = "SELECT id, name, address, phone, created_at FROM clinics ORDER BY name";
        List<Clinic> list = new ArrayList<>();
        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public Long create(Clinic c) throws Exception {
        String sql = "INSERT INTO clinics (name, address, phone) VALUES (?, ?, ?)";
        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getName());
            if (c.getAddress() != null) ps.setString(2, c.getAddress()); else ps.setNull(2, Types.VARCHAR);
            if (c.getPhone() != null) ps.setString(3, c.getPhone()); else ps.setNull(3, Types.VARCHAR);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        return null;
    }

    @Override
    public boolean update(Clinic c) throws Exception {
        String sql = "UPDATE clinics SET name = ?, address = ?, phone = ? WHERE id = ?";
        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            if (c.getAddress() != null) ps.setString(2, c.getAddress()); else ps.setNull(2, Types.VARCHAR);
            if (c.getPhone() != null) ps.setString(3, c.getPhone()); else ps.setNull(3, Types.VARCHAR);
            ps.setLong(4, c.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(Long id) throws Exception {
        String sql = "DELETE FROM clinics WHERE id = ?";
        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Clinic mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String address = rs.getString("address");
        String phone = rs.getString("phone");
        Timestamp ts = rs.getTimestamp("created_at");
        Instant createdAt = ts == null ? null : ts.toInstant();
        return new Clinic(id, name, address, phone, createdAt);
    }
}
