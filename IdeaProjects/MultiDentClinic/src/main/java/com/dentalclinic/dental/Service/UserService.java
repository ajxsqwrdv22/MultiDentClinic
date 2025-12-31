package com.dentalclinic.dental.Service;

import com.dentalclinic.dental.model.User;
import java.util.Optional;
import java.util.List;

public interface UserService {
    Optional<User> findByUsername(String username) throws Exception;
    Optional<User> findById(Long id) throws Exception;
    List<User> listAll() throws Exception;
    boolean activateUser(Long id ) throws Exception;
    boolean deactivateUser(Long id) throws Exception;
    void assignRole(Long userId, Long roleId) throws Exception;

    void replaceRoles(Long userId, List<Long> roleIds) throws Exception;

    Long createUser(User user, String rawPassword) throws Exception;
    boolean updateUser(User user) throws Exception;
    boolean delete(Long id) throws Exception;
    boolean changePassword(Long id, String rawPassword) throws Exception;
}
