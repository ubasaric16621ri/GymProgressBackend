package com.example.gymprogress.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        // Loguj sadržaj zaglavlja Authorization
        System.out.println("Authorization Header: " + authorizationHeader);

        String username = null;
        String jwt = null;

        // Proveri da li Authorization zaglavlje sadrži "Bearer " prefiks
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            System.out.println("JWT: " + jwt);

            try {
                // Pokušaj da izdvojiš korisničko ime iz tokena
                username = jwtUtil.extractUsername(jwt);
                System.out.println("Extracted Username: " + username);
            } catch (ExpiredJwtException e) {
                System.out.println("JWT token has expired: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized ako je token istekao
                return;
            } catch (Exception e) {
                System.out.println("JWT token parsing error: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request ako je token neispravan
                return;
            }
        } else {
            System.out.println("Authorization header missing or does not start with 'Bearer '");
        }

        // Ako smo uspeli da izdvojimo korisničko ime i ako nije već autentifikovan
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Validiraj JWT token
            if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                System.out.println("JWT token validated successfully for user: " + username);
            } else {
                System.out.println("JWT token validation failed");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 Forbidden ako validacija nije prošla
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
