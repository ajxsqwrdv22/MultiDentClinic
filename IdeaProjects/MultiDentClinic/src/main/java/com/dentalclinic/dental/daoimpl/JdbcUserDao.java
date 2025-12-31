package com.dentalclinic.dental.daoimpl;

import com.dentalclinic.dental.daos.UserDao;
import com.dentalclinic.dental.model.Role;
import com.dentalclinic.dental.model.User;
import com.dentalclinic.dental.util.DbConnectionPool;

import java.sql.*;
import java.util.*;

public class JdbcUserDao implements UserDao {
    @Override
    public boolean setEnabled(Long id, boolean enabled) throws Exception {
        String sql = "UPDATE users SET enabled = ? WHERE id = ?";

        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, enabled);
            ps.setLong(2, id);

            return ps.executeUpdate() > 0;
        }
    }


    // =====================================================
    // FIND USER BY USERNAME (LOGIN)
    // =====================================================
    @Override
    public Optional<User> findByUsername(String username) throws Exception {
        String sql = """
            SELECT id, username, password, enabled
            FROM users
            WHERE username = ?
        """;

        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();

                User user = mapUserBasic(rs);
                user.setRoles(findRolesByUserId(user.getId(), conn));
                return Optional.of(user);
            }
        }
    }

    // =====================================================
    // FIND USER BY ID
    // =====================================================
    @Override
    public Optional<User> findById(Long id) throws Exception {
        String sql = """
            SELECT id, username, password, enabled
            FROM users
            WHERE id = ?
        """;

        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();

                User user = mapUserBasic(rs);
                user.setRoles(findRolesByUserId(user.getId(), conn));
                return Optional.of(user);
            }
        }
    }

    // =====================================================
    // FIND ALL USERS
    // =====================================================
    @Override
    public List<User> findAll() throws Exception {
        String sql = """
            SELECT id, username, password, enabled
            FROM users
            ORDER BY username
        """;

        List<User> users = new ArrayList<>();

        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User user = mapUserBasic(rs);
                user.setRoles(findRolesByUserId(user.getId(), conn));
                users.add(user);
            }
        }
        return users;
    }

    // =====================================================
    // CREATE USER (NO ROLE HERE)
    // =====================================================
    @Override
    public Long create(User user) throws Exception {
        String sql = """
            INSERT INTO users (username, password, enabled)
            VALUES (?, ?, ?)
        """;

        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setBoolean(3, user.isEnabled());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        return null;
    }

    // =====================================================
    // UPDATE USER (USERNAME + ENABLED)
    // =====================================================
    @Override
    public boolean update(User user) throws Exception {
        String sql = """
            UPDATE users
            SET username = ?, enabled = ?
            WHERE id = ?
        """;

        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setBoolean(2, user.isEnabled());
            ps.setLong(3, user.getId());

            return ps.executeUpdate() > 0;
        }
    }

    // =====================================================
    // DELETE USER
    // =====================================================
    @Override
    public boolean deleteById(Long id) throws Exception {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // =====================================================
    // UPDATE PASSWORD
    // =====================================================
    @Override
    public boolean updatePassword(Long id, String passwordHash) throws Exception {
        String sql = "UPDATE users SET password = ? WHERE id = ?";

        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, passwordHash);
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    // =====================================================
    // INTERNAL HELPERS
    // =====================================================

    private User mapUserBasic(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setEnabled(rs.getBoolean("enabled"));
        return u;
    }

    private List<Role> findRolesByUserId(Long userId, Connection conn) throws SQLException {
        String sql = """
            SELECT r.id, r.name
            FROM roles r
            JOIN user_roles ur ON ur.role_id = r.id
            WHERE ur.user_id = ?
        """;

        List<Role> roles = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    roles.add(new Role(
                            rs.getLong("id"),
                            rs.getString("name")
                    ));
                }
            }
        }
        return roles;
    }
}
