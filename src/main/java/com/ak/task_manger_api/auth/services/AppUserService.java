package com.ak.task_manger_api.auth.services;

import com.ak.task_manger_api.auth.models.AppUser;
import com.ak.task_manger_api.auth.repositories.AppUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppUserService implements UserDetailsService {
    @Autowired
    private final AppUserRepository _repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Fetching userDetails of user with username='{}'", username);

        AppUser user = _repository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        log.info("UserDetails fetched successfully for username='{}'", username);
        return new User(user.getUsername(), user.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
    }

    public AppUser getCurrentUser(Principal principal) {
        log.debug("Fetching current user from Principal '{}'", principal.getName());

        return _repository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public AppUser findUserByUsername(String username) {
        log.info("Fetching user with username='{}'", username)
        ;
        return _repository.findByUsername(username)
                .orElseThrow(EntityNotFoundException::new);
    }

    public AppUser createUser(AppUser user) {
        log.debug("Creating new user with the data:{}", user);

        AppUser created = _repository.save(user);

        log.info("User created successfully with the id:'{}', name:'{}'", created.getId(), created.getUsername());
        return created;
    }
}
