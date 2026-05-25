package com.smarthmalik.metalpulse.core.repository;

import com.smarthmalik.metalpulse.core.constant.SQLQueryConstants;
import com.smarthmalik.metalpulse.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(SQLQueryConstants.USER_FIND_BY_USERNAME)
    Optional<User> findByUsername(@Param("username") String username);

    @Query(SQLQueryConstants.USER_FIND_BY_EMAIL)
    Optional<User> findByEmail(@Param("email") String email);

    @Query(SQLQueryConstants.USER_EXISTS_BY_USERNAME)
    boolean existsByUsername(@Param("username") String username);

    @Query(SQLQueryConstants.USER_EXISTS_BY_EMAIL)
    boolean existsByEmail(@Param("email") String email);
}
