package com.smarthmalik.metalpulse.core.service.impl;

import com.smarthmalik.metalpulse.dto.request.RegisterRequest;
import com.smarthmalik.metalpulse.dto.response.UserDto;
import com.smarthmalik.metalpulse.core.dao.UserDAO;
import com.smarthmalik.metalpulse.core.entity.User;
import com.smarthmalik.metalpulse.core.mapper.EntityDTOMapper;
import com.smarthmalik.metalpulse.core.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final BigDecimal JOINING_BONUS = new BigDecimal("5000.0000");

    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;
    private final EntityDTOMapper mapper;

    @Override
    public UserDto register(RegisterRequest request) {
        if (userDAO.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username is already taken: " + request.username());
        }
        if (userDAO.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already registered: " + request.email());
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(User.Role.USER)
                .balance(JOINING_BONUS)
                .build();

        User saved = userDAO.save(user);
        return mapper.toUserDto(saved);
    }
}
