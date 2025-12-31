package com.dentalclinic.dental.Service.impl;

import com.dentalclinic.dental.Service.UserService;
import com.dentalclinic.dental.daos.UserDao;
import com.dentalclinic.dental.daoimpl.JdbcUserDao;
import com.dentalclinic.dental.model.User;
import com.dentalclinic.dental.util.DbConnectionPool;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private final UserDao dao = new JdbcUserDao();

    @Override
    public Optional<User> findByUsername(String username) throws Exception {
        return dao.findByUsername(username);
    }

    @Override
    public Optional<User> findById(Long id) throws Exception {
        return dao.findById(id);
    }

    @Override
    public List<User> listAll() throws Exception {
        return dao.findAll();
    }

    // =====================================================
    // CREATE USER (WITH PASSWORD VALIDATION)
    // =====================================================
    @Override
    public Long createUser(User user, String rawPassword) throws Exception {
        validatePassword(rawPassword);

        String hashed = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        user.setPassword(hashed);
        user.setEnabled(true); // default

        return dao.create(user);
    }

    // =====================================================
    // UPDATE USER (NO PASSWORD CHANGE)
    // =====================================================
    @Override
    public boolean updateUser(User user) throws Exception {
        return dao.update(user);
    }

    // =====================================================
    // DELETE USER (DISABLED BY BUSINESS RULE)
    // =====================================================
    @Override
    public boolean delete(Long id) throws Exception {
        throw new UnsupportedOperationException(
                "Users cannot be deleted. Use deactivate instead."
        );
    }

    // =====================================================
    // CHANGE PASSWORD (WITH VALIDATION)
    // =====================================================
    @Override
    public boolean changePassword(Long id, String rawPassword) throws Exception {
        validatePassword(rawPassword);

        String hashed = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        return dao.updatePassword(id, hashed);
    }

    // =====================================================
    // ACTIVATE / DEACTIVATE USER
    // =====================================================
    @Override
    public boolean activateUser(Long id) throws Exception {
        return dao.setEnabled(id, true);
    }

    @Override
    public boolean deactivateUser(Long id) throws Exception {
        return dao.setEnabled(id, false);


    }
    @Override
    public void assignRole(Long userId, Long roleId) throws Exception {
        String sql = """
        INSERT INTO user_roles (user_id, role_id)
        VALUES (?, ?)
    """;

        try (Connection conn = DbConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setLong(2, roleId);
            ps.executeUpdate();
        }
    }

    @Override
    public void replaceRoles(Long userId, List<Long> roleIds) throws Exception {
        String deleteSql = "DELETE FROM user_roles WHERE user_id = ?";
        String insertSql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";

        try (Connection conn = DbConnectionPool.getDataSource().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement del = conn.prepareStatement(deleteSql)) {
                del.setLong(1, userId);
                del.executeUpdate();
            }

            try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
                for (Long roleId : roleIds) {
                    ins.setLong(1, userId);
                    ins.setLong(2, roleId);
                    ins.addBatch();
                }
                ins.executeBatch();
            }

            conn.commit();
        }
    }


    // =====================================================
    // PASSWORD VALIDATION
    // =====================================================
    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required.");
        }

        if (!password.matches(
                "(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}"
        )) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters and include " +
                            "uppercase, lowercase, number, and special character."
            );
        }
    }
}
