package com.ak.task_manger_api.auth.controllers;

import com.ak.task_manger_api.auth.DTO.LoginRequest;
import com.ak.task_manger_api.auth.DTO.RegisterRequest;
import com.ak.task_manger_api.auth.DTO.RegisterResponse;
import com.ak.task_manger_api.auth.services.AuthService;
import com.ak.task_manger_api.response.CustomResponse;
import com.ak.task_manger_api.response.ResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityExistsException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private final AuthService authService;

    @Operation(summary = "Register a new user", description = "Creates a new user account with a default role.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<CustomResponse<RegisterResponse>> register(
            @Parameter(description = "Registration request body")
            @RequestBody @Valid RegisterRequest request
    ) throws EntityExistsException, MethodArgumentNotValidException {
        return ResponseBuilder.created(authService.register(request),
                "User registered successfully");
    }

    @Operation(summary = "Login user", description = "Authenticates a user and returns a JWT token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User logged in successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid username or password")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Parameter(description = "Login request body")
            @RequestBody LoginRequest request
    ) throws BadCredentialsException {
        return ResponseBuilder.ok(authService.login(request),
                "User logged in successfully");
    }
}
