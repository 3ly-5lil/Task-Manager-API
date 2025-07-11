package com.ak.task_manger_api.auth.services;

import com.ak.task_manger_api.auth.DTO.LoginRequest;
import com.ak.task_manger_api.auth.DTO.LoginResponse;
import com.ak.task_manger_api.auth.DTO.RegisterRequest;
import com.ak.task_manger_api.auth.DTO.RegisterResponse;
import com.ak.task_manger_api.auth.configs.JwtUtil;
import com.ak.task_manger_api.auth.models.AppUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register and login users")
public class AuthService {
    @Autowired
    private final AuthenticationManager authenticationManager;
    @Autowired
    private final JwtUtil jwtUtil;
    @Autowired
    private final AppUserService appUserService;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    public RegisterResponse register(RegisterRequest request) throws EntityExistsException {
        log.info("Attempting to register user '{}'", request.username());
        try {
            log.debug("Checking if the username'{}' exist", request.username());
            appUserService.findUserByUsername(request.username());

            log.error("User with the username:'{}' already exists", request.username());

            throw new EntityExistsException("username already exists");
        } catch (EntityNotFoundException e) {
            log.info("Creating new user '{}'", request.username());

            AppUser createdUser = appUserService.createUser(
                    AppUser.builder()
                            .username(request.username())
                            .password(passwordEncoder.encode(request.password()))
                            .role("USER")
                            .build());

            RegisterResponse response = new RegisterResponse(createdUser.getId(), createdUser.getUsername(), createdUser.getRole());

            log.info("User with username:'{}' created successfully with the id={}", response.username(), response.userId());
            return response;
        }
    }

    public LoginResponse login(LoginRequest request) throws BadCredentialsException {
        log.info("User '{}' attempting login", request.username());

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        AppUser user = appUserService.findUserByUsername(request.username());

        String token = jwtUtil.generateToken(user);

        LoginResponse response = new LoginResponse(user.getId(), user.getUsername(), user.getRole(), token);

        log.info("User '{}' logged in successfully", response.username());

        return response;
    }

}
