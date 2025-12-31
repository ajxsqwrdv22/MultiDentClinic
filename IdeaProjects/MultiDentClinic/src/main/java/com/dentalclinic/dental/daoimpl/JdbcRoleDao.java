package com.dentalclinic.dental.daoimpl;

import com.dentalclinic.dental.daos.RoleDao;
import com.dentalclinic.dental.model.Role;
import com.dentalclinic.dental.util.DbConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcRoleDao implements RoleDao {

    @Override
    public Optional<Role> findById(Long id) throws Exception {
        String sql = "SELECT id, name FROM roles WHERE id = ?";
        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Role r = new Role(rs.getLong("id"), rs.getString("name"));
                    return Optional.of(r);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Role> findByName(String name) throws Exception {
        String sql = "SELECT id, name FROM roles WHERE name = ?";
        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Role r = new Role(rs.getLong("id"), rs.getString("name"));
                    return Optional.of(r);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Role> findAll() throws Exception {
        String sql = "SELECT id, name FROM roles ORDER BY name";
        List<Role> list = new ArrayList<>();
        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Role(rs.getLong("id"), rs.getString("name")));
            }
        }
        return list;
    }
}
