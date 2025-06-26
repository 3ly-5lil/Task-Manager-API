package com.ak.task_manger_api.auth.controllers;

import com.ak.task_manger_api.auth.DTO.LoginRequest;
import com.ak.task_manger_api.auth.DTO.RegisterRequest;
import com.ak.task_manger_api.auth.DTO.RegisterResponse;
import com.ak.task_manger_api.auth.services.AuthService;
import com.ak.task_manger_api.response.ApiResponse;
import com.ak.task_manger_api.response.ResponseBuilder;
import jakarta.persistence.EntityExistsException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@RequestBody @Valid RegisterRequest request) throws EntityExistsException, MethodArgumentNotValidException {

        return ResponseBuilder.created(authService.register(request),
                "User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) throws BadCredentialsException {
        return ResponseEntity.ok(authService.login(request));
    }
}
