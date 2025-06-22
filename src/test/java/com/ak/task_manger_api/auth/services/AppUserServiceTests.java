package com.ak.task_manger_api.auth.services;

import com.ak.task_manger_api.auth.models.AppUser;
import com.ak.task_manger_api.auth.repositories.AppUserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AppUserServiceTests {
    @InjectMocks
    AppUserService service;
    @Mock
    AppUserRepository repository;

    @Test
    void shouldCreateUser() {
        AppUser user = new AppUser(null, "name", "password", "USER");
        AppUser mockedUser = new AppUser(1L, "name", "password", "USER");

        when(repository.save(user)).thenReturn(mockedUser);

        AppUser createdUser = service.createUser(user);

        assertEquals(mockedUser, createdUser);
    }

    @Nested
    class LoadUserByUsernameTests {
        @Test
        void shouldReturnUserDetailsWhenUserExists() {
            // Arrange
            AppUser user = new AppUser(1L, "name", "password", "USER");

            when(repository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

            // Act
            UserDetails userDetails = service.loadUserByUsername(user.getUsername());

            // Assert
            assertEquals(user.getUsername(), userDetails.getUsername());
            assertEquals(user.getPassword(), userDetails.getPassword());
        }

        @Test
        void shouldThrowUsernameNotFoundExceptionWhenUserNotExists() {
            when(repository.findByUsername(any())).thenReturn(Optional.empty());

            assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(any()));
        }
    }

    @Nested
    class GetCurrentUserTests {
        @Test
        void shouldReturnCurrentUserWhenUserExists() {
            Principal mockPrinciple = () -> "TestUser";
            var expectedUser = new AppUser(1L, "user", "Password", "USER");

            when(repository.findByUsername(mockPrinciple.getName())).thenReturn(Optional.of(expectedUser));

            AppUser user = service.getCurrentUser(mockPrinciple);

            assertEquals(expectedUser, user);
        }

        @Test
        void shouldThrowUsernameNotFoundExceptionWhenUserNotExists() {
            Principal mockPrincipal = () -> "unknown";

            when(repository.findByUsername(mockPrincipal.getName())).thenReturn(Optional.empty());

            assertThrows(UsernameNotFoundException.class, () -> service.getCurrentUser(mockPrincipal));

        }
    }

    @Nested
    class FindUserByUsernameTests {
        @Test
        void shouldReturnUserDetailsWhenUserExists() {
            // Arrange
            AppUser user = new AppUser(1L, "name", "password", "USER");

            when(repository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

            // Act
            AppUser foundUser = service.findUserByUsername(user.getUsername());

            // Assert
            assertEquals(user.getId(), foundUser.getId());
            assertEquals(user.getUsername(), foundUser.getUsername());
            assertEquals(user.getPassword(), foundUser.getPassword());
            assertEquals(user.getRole(), foundUser.getRole());
        }

        @Test
        void shouldThrowEntityNotFoundExceptionWhenUserNotExists() {
            when(repository.findByUsername(any())).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> service.findUserByUsername(any()));
        }
    }
}
