package com.example.gymprogress.controllers;

import com.example.gymprogress.entities.RefreshToken;
import com.example.gymprogress.repositories.RefreshTokenRepository;
import com.example.gymprogress.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/token")
public class TokenController {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        return refreshTokenRepository.findByToken(refreshToken)
                .map(token -> {
                    String newToken = jwtUtil.generateToken(token.getUser().getUsername());
                    return ResponseEntity.ok(Map.of("accessToken", newToken)); // Vraćamo mapu sa tokenom
                })
                .orElse(ResponseEntity.status(403).body(Map.of("error", "Invalid refresh token"))); // Takođe vraćamo mapu za grešku
    }

}
