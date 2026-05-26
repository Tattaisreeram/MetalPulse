package com.smarthmalik.metalpulse.core.facade;

import com.smarthmalik.metalpulse.dto.request.LoginRequest;
import com.smarthmalik.metalpulse.dto.request.RegisterRequest;
import com.smarthmalik.metalpulse.dto.response.ApiResponse;
import com.smarthmalik.metalpulse.dto.response.AuthResponse;
import com.smarthmalik.metalpulse.dto.response.UserDto;
import com.smarthmalik.metalpulse.core.mapper.EntityDTOMapper;
import com.smarthmalik.metalpulse.core.security.JwtTokenProvider;
import com.smarthmalik.metalpulse.core.security.UserPrincipal;
import com.smarthmalik.metalpulse.core.service.AuthService;
import com.smarthmalik.metalpulse.core.service.TradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthFacade {

    private final AuthService authService;
    private final TradeService tradeService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final EntityDTOMapper mapper;

    @Transactional
    public ApiResponse<AuthResponse> register(RegisterRequest request) {
        UserDto userDto = authService.register(request);
        tradeService.createBonusTrade(userDto.userId(), userDto.balance());
        String token = jwtTokenProvider.generateToken(userDto.username());
        AuthResponse authResponse = mapper.toAuthResponse(userDto, token);
        log.info("New user registered: username={} joining bonus applied: ${}", userDto.username(), userDto.balance());
        return ApiResponse.success("Registration successful", authResponse);
    }

    @Transactional(readOnly = true)
    public ApiResponse<AuthResponse> login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        Object raw = auth.getPrincipal();
        if (!(raw instanceof UserPrincipal principal)) {
            throw new IllegalStateException(
                "Expected UserPrincipal but got: " + (raw == null ? "null" : raw.getClass().getName())
            );
        }
        String token = jwtTokenProvider.generateToken(principal.getUsername());
        UserDto userDto = mapper.toUserDto(principal.getUser());
        AuthResponse authResponse = mapper.toAuthResponse(userDto, token);
        log.info("User logged in: username={}", principal.getUsername());
        return ApiResponse.success("Login successful", authResponse);
    }
}
