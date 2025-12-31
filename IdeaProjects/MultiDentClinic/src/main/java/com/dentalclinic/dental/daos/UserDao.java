package com.dentalclinic.dental.daos;

import com.dentalclinic.dental.model.User;
import java.util.Optional;
import java.util.List;

public interface UserDao {
    Optional<User> findByUsername(String username) throws Exception;
    Optional<User> findById(Long id) throws Exception;
    List<User> findAll() throws Exception;
    boolean setEnabled(Long id, boolean b) throws Exception;

    Long create(User user) throws Exception;
    boolean update(User user) throws Exception;
    boolean deleteById(Long id) throws Exception;
    boolean updatePassword(Long id, String passwordHash) throws Exception;
}
