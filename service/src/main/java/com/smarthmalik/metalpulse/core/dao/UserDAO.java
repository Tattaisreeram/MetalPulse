package com.smarthmalik.metalpulse.core.dao;

import com.smarthmalik.metalpulse.core.entity.User;

import java.util.Optional;

public interface UserDAO {

    User save(User user);

    User getReferenceById(Long id);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
