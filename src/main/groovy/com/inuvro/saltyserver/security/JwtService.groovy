package com.inuvro.saltyserver.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

import javax.crypto.SecretKey
import java.nio.charset.StandardCharsets
import java.util.function.Function

@Service
class JwtService {
    
    @Value('${salty.jwt.secret:defaultSecretKeyThatShouldBeChangedInProductionSalty123456}')
    private String secretKey
    
    @Value('${salty.jwt.expiration:86400000}')  // Default 24 hours in ms
    private long jwtExpiration
    
    String extractUsername(String token) {
        return extractClaim(token, { Claims claims -> claims.getSubject() })
    }
    
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token)
        return claimsResolver.apply(claims)
    }
    
    String generateToken(UserDetails userDetails) {
        return generateToken([:], userDetails)
    }
    
    String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration)
    }
    
    long getExpirationTime() {
        return jwtExpiration
    }
    
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact()
    }
    
    boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token)
        return (username == userDetails.getUsername()) && !isTokenExpired(token)
    }
    
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date())
    }
    
    private Date extractExpiration(String token) {
        return extractClaim(token, { Claims claims -> claims.getExpiration() })
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
    }
    
    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8)
        // Ensure key is at least 256 bits (32 bytes) for HS256
        if (keyBytes.length < 32) {
            // Pad the key if too short
            byte[] paddedKey = new byte[32]
            System.arraycopy(keyBytes, 0, paddedKey, 0, keyBytes.length)
            keyBytes = paddedKey
        }
        return Keys.hmacShaKeyFor(keyBytes)
    }
}
