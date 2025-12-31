package com.dentalclinic.dental.Service;

import com.dentalclinic.dental.model.User;
import java.util.Optional;

public interface AuthService {
    Optional<User> authenticate(String username, String password) throws Exception;
}
