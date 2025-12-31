package com.dentalclinic.dental.Service.impl;

import com.dentalclinic.dental.Service.AuthService;
import com.dentalclinic.dental.daos.UserDao;
import com.dentalclinic.dental.daoimpl.JdbcUserDao;
import com.dentalclinic.dental.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class AuthServiceImpl implements AuthService {

    private final UserDao userDao = new JdbcUserDao();

    boolean setEnabled(Long userId, boolean enabled) throws Exception {
        return false;
    }

    @Override
    public Optional<User> authenticate(String username, String password) throws Exception {
        Optional<User> ou = userDao.findByUsername(username);

        if (!ou.isPresent()) {
            return Optional.empty();
        }

        User u = ou.get();

        // 🔒 IMPORTANT: enabled check
        if (!u.isEnabled()) {
            return Optional.empty();
        }

        if (!BCrypt.checkpw(password, u.getPassword())) {
            return Optional.empty();
        }

        return Optional.of(u);
    }
}
