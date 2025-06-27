package com.ak.task_manger_api.auth.controllers;

import com.ak.task_manger_api.auth.DTO.LoginRequest;
import com.ak.task_manger_api.auth.DTO.LoginResponse;
import com.ak.task_manger_api.auth.DTO.RegisterRequest;
import com.ak.task_manger_api.auth.DTO.RegisterResponse;
import com.ak.task_manger_api.auth.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class AuthControllerTests {
    static final String registerUri = "/api/auth/register";
    static final String loginUri = "/api/auth/login";
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    AuthService authService;

    @Nested
    class RegisterTests {
        @Test
        void shouldThrowEntityExistsExceptionWhenUserAlreadyExists() throws Exception {
            RegisterRequest request = new RegisterRequest("User_01", "Password_123");

            when(authService.register(request)).thenThrow(EntityExistsException.class);

            mockMvc.perform(post(registerUri).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andExpect(status().isConflict());
        }

        @Test
        void shouldThrowMethodArgumentNotValidExceptionWhenParamsNotValid() throws Exception {
            RegisterRequest request = new RegisterRequest("User 01", "123");

            when(authService.register(request)).thenThrow(EntityExistsException.class);

            mockMvc.perform(post(registerUri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("username:")))
                    .andExpect(jsonPath("$.message", containsString("password:")));
        }

        @Test
        void shouldRegisterSuccessfully() throws Exception {
            RegisterRequest request = new RegisterRequest("User_01", "Password123");
            RegisterResponse response = new RegisterResponse(1L, request.username(), "USER");

            when(authService.register(request)).thenReturn(response);

            mockMvc.perform(post(registerUri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").value("User registered successfully"))
                    .andExpect(jsonPath("$.data.userId").value(response.userId()))
                    .andExpect(jsonPath("$.data.username").value(response.username()))
                    .andExpect(jsonPath("$.data.role").value(response.role()));
        }
    }

    @Nested
    class LoginTests {
        @Test
        void shouldThrowBadCredentialsException() throws Exception {
            LoginRequest request = new LoginRequest("User_01", "Password");

            when(authService.login(request)).thenThrow(BadCredentialsException.class);

            mockMvc.perform(post(loginUri)
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error").value("Unauthorized"))
                    .andExpect(jsonPath("$.message").value("Invalid username or password"));
        }

        @Test
        void shouldLoginSuccessfully() throws Exception {
            LoginRequest request = new LoginRequest("User_01", "Password");
            LoginResponse response = new LoginResponse(1L, request.username(), "USER", "Bearer token");

            when(authService.login(request)).thenReturn(response);

            mockMvc.perform(post(loginUri)
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("User logged in successfully"))
                    .andExpect(jsonPath("$.data.userId").value(response.userId()))
                    .andExpect(jsonPath("$.data.username").value(response.username()))
                    .andExpect(jsonPath("$.data.role").value(response.role()))
                    .andExpect(jsonPath("$.data.token").value(response.token()));

        }
    }
}
