package com.ak.task_manger_api.auth.services;

import com.ak.task_manger_api.auth.DTO.LoginRequest;
import com.ak.task_manger_api.auth.DTO.RegisterRequest;
import com.ak.task_manger_api.auth.DTO.RegisterResponse;
import com.ak.task_manger_api.auth.configs.JwtUtil;
import com.ak.task_manger_api.auth.models.AppUser;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {
    @InjectMocks
    AuthService authService;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    AppUserService appUserService;
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    JwtUtil jwtUtil;


    @Nested
    class RegisterTests {
        @Test
        void shouldRegisterSuccessfully() {
            // Arrange
            RegisterRequest request = RegisterRequest.builder()
                    .username("TestUser1")
                    .password("TestPassword")
                    .build();

            AppUser createdUser = new AppUser(1L, "TestUser", "Hashed(TestPassword)", "USER");

            when(appUserService.findUserByUsername(request.username())).thenThrow(EntityNotFoundException.class);
            when(appUserService.createUser(any(AppUser.class))).thenReturn(createdUser);
            when(passwordEncoder.encode("TestPassword")).thenReturn("Hashed(TestPassword)");
            // Act
            RegisterResponse response = authService.register(request);

            // Assert
            assertEquals(1L, response.userId());
            assertEquals("USER", response.role());
            assertNotNull(response.username());
        }

        @Test
        void shouldThrowEntityExistsException() {
            // Arrange
            RegisterRequest request = new RegisterRequest("UserTest", "Password");

            when(appUserService.findUserByUsername(request.username()))
                    .thenReturn(any(AppUser.class));
            // Act
            assertThrows(EntityExistsException.class, () -> authService.register(request));

            // Assert
            verify(appUserService, never()).createUser(any());
        }
    }

    @Nested
    class LoginTests {
        @Test
        void shouldLoginSuccessfully() {
            // Arrange
            var request = new LoginRequest("TestUser", "TestPass");
            var user = new AppUser(1L, request.username(), "", "USER");
            var token = "TOKEN";

            when(appUserService.findUserByUsername(request.username())).thenReturn(user);
            when(jwtUtil.generateToken(user)).thenReturn(token);

            // Act
            var response = authService.login(request);

            // Assert
            assertEquals(user.getId(), response.userId());
            assertEquals(token, response.token());
        }

        @Test
        void shouldThrowBadCredentialsException() {
            // Arrange
            var request = new LoginRequest("TestUser", "TestPass");
            when(authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(),
                            request.password())))
                    .thenThrow(new BadCredentialsException(""));

            // Act
            assertThrows(BadCredentialsException.class, () -> authService.login(request));

            // Assert
            verify(appUserService, never()).findUserByUsername(any());
            verify(jwtUtil, never()).generateToken(any());
        }
    }
}
