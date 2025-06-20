package com.ak.task_manger_api.auth.services;

import com.ak.task_manger_api.auth.models.AppUser;
import com.ak.task_manger_api.auth.repositories.AppUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service @RequiredArgsConstructor
public class AppUserService implements UserDetailsService {
    @Autowired
    private final AppUserRepository _repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = _repository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("User not found"));

        return new User(user.getUsername(), user.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
    }

    public AppUser getCurrentUser(Principal principal) {
        return _repository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public AppUser findUserByUsername(String username) {
        return _repository.findByUsername(username)
                .orElseThrow(EntityNotFoundException::new);
    }

    public void createUser(AppUser user){
        _repository.save(user);
    }
}
