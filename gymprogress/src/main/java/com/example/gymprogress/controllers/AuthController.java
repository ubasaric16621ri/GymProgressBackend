package com.example.gymprogress.controllers;

import com.example.gymprogress.entities.AppUser;
import com.example.gymprogress.models.JwtRequest;
import com.example.gymprogress.models.JwtResponse;
import com.example.gymprogress.security.CustomUserDetailsService;
import com.example.gymprogress.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody AppUser user) {
        try {
            AppUser savedUser = userDetailsService.saveUser(user); // Ova metoda bi trebala da vraća korisnika
            return ResponseEntity.ok(Map.of("message", "User registered successfully", "user", savedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Registration failed: " + e.getMessage()));
        }
    }



    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest jwtRequest) throws Exception {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final AppUser appUser = userDetailsService.loadAppUserByUsername(jwtRequest.getUsername());
        final String accessToken = jwtUtil.generateToken(appUser.getUsername());

        return ResponseEntity.ok(new JwtResponse(accessToken));
    }
}
