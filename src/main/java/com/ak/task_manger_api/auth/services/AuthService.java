package com.ak.task_manger_api.auth.services;

import com.ak.task_manger_api.auth.DTO.LoginRequest;
import com.ak.task_manger_api.auth.DTO.LoginResponse;
import com.ak.task_manger_api.auth.DTO.RegisterRequest;
import com.ak.task_manger_api.auth.DTO.RegisterResponse;
import com.ak.task_manger_api.auth.configs.JwtUtil;
import com.ak.task_manger_api.auth.models.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class AuthService {
    @Autowired
    private final AuthenticationManager authenticationManager;
    @Autowired
    private final JwtUtil jwtUtil;
    @Autowired
    private final AppUserService appUserService;
    @Autowired
    private final PasswordEncoder _encoder;

    public RegisterResponse register(RegisterRequest request) throws RuntimeException {
        if (appUserService.findUserByUsername(request.username()).isPresent()) {
            throw new RuntimeException("username already exists");
        }

        AppUser user = AppUser.builder()
                .username(request.username())
                .password(_encoder.encode(request.password()))
                .role("USER")
                .build();

        appUserService.createUser(user);
        return new RegisterResponse("User registered successfully");
    }

    public LoginResponse login(LoginRequest request) throws BadCredentialsException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        UserDetails user = appUserService.loadUserByUsername(request.username());

        String token = jwtUtil.generateToken(user);

        return new LoginResponse(token);
    }

}
