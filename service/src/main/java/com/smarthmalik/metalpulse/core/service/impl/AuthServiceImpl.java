package com.smarthmalik.metalpulse.core.service.impl;

import com.smarthmalik.metalpulse.dto.request.LoginRequest;
import com.smarthmalik.metalpulse.dto.request.RegisterRequest;
import com.smarthmalik.metalpulse.dto.response.AuthResponse;
import com.smarthmalik.metalpulse.core.dao.TradeDAO;
import com.smarthmalik.metalpulse.core.dao.UserDAO;
import com.smarthmalik.metalpulse.core.entity.Trade;
import com.smarthmalik.metalpulse.core.entity.User;
import com.smarthmalik.metalpulse.core.mapper.EntityDTOMapper;
import com.smarthmalik.metalpulse.core.security.JwtTokenProvider;
import com.smarthmalik.metalpulse.core.security.UserPrincipal;
import com.smarthmalik.metalpulse.core.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final BigDecimal JOINING_BONUS = new BigDecimal("5000.0000");

    private final UserDAO userDAO;
    private final TradeDAO tradeDAO;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final EntityDTOMapper mapper;

    @Override
    public AuthResponse register(RegisterRequest request) {
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

        tradeDAO.save(Trade.builder()
                .user(saved)
                .tradeType(Trade.TradeType.BONUS)
                .currency("USD")
                .totalAmount(JOINING_BONUS)
                .status(Trade.TradeStatus.COMPLETED)
                .build());

        String token = jwtTokenProvider.generateToken(saved);
        log.info("New user registered: username={} joining bonus applied: ${}", saved.getUsername(), JOINING_BONUS);
        return mapper.toAuthResponse(saved, token);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        Object raw = authentication.getPrincipal();
        if (!(raw instanceof UserPrincipal principal)) {
            throw new IllegalStateException(
                "Expected UserPrincipal but got: " + (raw == null ? "null" : raw.getClass().getName())
            );
        }
        User user = principal.getUser();
        String token = jwtTokenProvider.generateToken(user);

        log.info("User logged in: username={}", user.getUsername());
        return mapper.toAuthResponse(user, token);
    }
}
