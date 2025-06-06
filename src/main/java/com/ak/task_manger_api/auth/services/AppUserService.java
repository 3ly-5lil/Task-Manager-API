package com.ak.task_manger_api.auth.services;

import com.ak.task_manger_api.auth.DTO.LoginRequest;
import com.ak.task_manger_api.auth.DTO.RegisterRequest;
import com.ak.task_manger_api.auth.models.AppUser;
import com.ak.task_manger_api.auth.repositories.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service @RequiredArgsConstructor
public class AppUserService {
    @Autowired
    private final AppUserRepository _repository;
    @Autowired
    private final PasswordEncoder _encoder;

    public void register(RegisterRequest request) throws RuntimeException {
        if (_repository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("username already exists");
        }

        AppUser user = AppUser.builder()
                .username(request.username())
                .password(_encoder.encode(request.password()))
                .role("USER")
                .build();

        _repository.save(user);
    }

    public User login(LoginRequest request) throws BadCredentialsException{
        Optional<AppUser> user = _repository.findByUsername(request.username());

        if (user.isEmpty() || !_encoder.matches(request.password(), user.get().getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return new User(user.get().getUsername(), user.get().getPassword(), List.of(new SimpleGrantedAuthority("ROLE_" + user.get().getRole())));
    }
}
