package com.example.gymprogress.security;

import com.example.gymprogress.entities.AppUser;
import com.example.gymprogress.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Dodaj ovu liniju

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder; // Inicijalizuj passwordEncoder
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> optionalUser = userRepository.findByUsername(username);

        AppUser user = optionalUser.orElseThrow(() ->
                new UsernameNotFoundException("User not found")
        );

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new ArrayList<>());
    }

    public AppUser loadAppUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public AppUser saveUser(AppUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Enkripcija lozinke
        user.setRole("ROLE_USER"); // Postavi default ulogu
        return userRepository.save(user);
    }

}
