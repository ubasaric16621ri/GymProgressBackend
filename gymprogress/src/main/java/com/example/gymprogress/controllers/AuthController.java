package com.example.gymprogress.controllers;

import com.example.gymprogress.entities.AppUser;
import com.example.gymprogress.entities.Progress;
import com.example.gymprogress.entities.RefreshToken;
import com.example.gymprogress.models.JwtRequest;
import com.example.gymprogress.models.JwtResponse;
import com.example.gymprogress.repositories.ProgressRepository;
import com.example.gymprogress.repositories.RefreshTokenRepository;
import com.example.gymprogress.security.CustomUserDetailsService;
import com.example.gymprogress.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
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
    private ProgressRepository progressRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @GetMapping("/api/resource")
    public String getResource() {
        return "Ovo je zaštićeni resurs";
    }

    @GetMapping("/api/progress/stats")
    public List<Progress> getProgressStats(@RequestParam Long userId) {
        return progressRepository.findByUserId(userId);
    }

    @GetMapping("/api/progress/compare")
    public List<Progress> compareProgress() {
        return progressRepository.findAll();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        System.out.println("Received refresh token: " + refreshToken);

        return refreshTokenRepository.findByToken(refreshToken)
                .map(token -> {
                    String newAccessToken = jwtUtil.generateToken(token.getUser().getUsername());
                    System.out.println("Generated new access token: " + newAccessToken);
                    return ResponseEntity.ok(Map.of("accessToken", newAccessToken, "refreshToken", refreshToken));
                })
                .orElseGet(() -> {
                    System.out.println("Invalid refresh token");
                    return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
                            .body(Map.of("error", "Invalid refresh token"));  // Vraća Map<String, String>
                });
    }




    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest jwtRequest) throws Exception {

        System.out.println("Request Body - Username: " + jwtRequest.getUsername());
        System.out.println("Request Body - Password: " + jwtRequest.getPassword());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final AppUser appUser = userDetailsService.loadAppUserByUsername(jwtRequest.getUsername());
        final String accessToken = jwtUtil.generateToken(appUser.getUsername());

        // Kreiraj osvežavajući token
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(appUser);
        refreshToken.setToken(jwtUtil.generateRefreshToken());
        // Postavljamo expiryDate za osvežavajući token (npr. 1 dan od sada)
        refreshToken.setExpiryDate(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
        refreshTokenRepository.save(refreshToken);

        // Vraćamo oba tokena
        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken.getToken()
        ));
    }


}
