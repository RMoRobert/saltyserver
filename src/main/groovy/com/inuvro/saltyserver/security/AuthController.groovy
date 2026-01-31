package com.inuvro.saltyserver.security

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController {
    
    private final AuthenticationManager authenticationManager
    private final UserDetailsService userDetailsService
    private final JwtService jwtService
    
    AuthController(
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager
        this.userDetailsService = userDetailsService
        this.jwtService = jwtService
    }
    
    /**
     * Login and get a JWT token
     * POST /api/auth/login
     */
    @PostMapping("/login")
    ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username, request.password)
            )
            
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.username)
            String token = jwtService.generateToken(userDetails)
            
            return ResponseEntity.ok(new AuthResponse(
                    token: token,
                    username: userDetails.username,
                    expiresIn: jwtService.getExpirationTime()
            ))
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body([error: "Invalid username or password"])
        }
    }
    
    /**
     * Validate a token and get user info
     * GET /api/auth/validate
     */
    @GetMapping("/validate")
    ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body([error: "No token provided"])
        }
        
        try {
            String token = authHeader.substring(7)
            String username = jwtService.extractUsername(token)
            UserDetails userDetails = userDetailsService.loadUserByUsername(username)
            
            if (jwtService.isTokenValid(token, userDetails)) {
                return ResponseEntity.ok([
                        valid: true,
                        username: username
                ])
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body([error: "Token is invalid or expired"])
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body([error: "Token validation failed: ${e.message}"])
        }
    }
}

class LoginRequest {
    String username
    String password
}

class AuthResponse {
    String token
    String username
    long expiresIn
}
