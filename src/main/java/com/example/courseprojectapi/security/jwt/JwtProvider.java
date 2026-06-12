package com.example.courseprojectapi.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import com.example.courseprojectapi.model.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {
    
    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expirationMs}")
    private long accessTokenExpiration;

    @Value("${app.jwt.refreshExpirationMs}")
    private long refreshTokenExpiration;

    public String generateAccessToken(User user) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        Date date = new Date(new Date().getTime() + accessTokenExpiration);
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("type", "access_token")
                .claim("role", user.getRole().getRoleName())
                .setExpiration(date)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(User user) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        Date date = new Date(new Date().getTime() + refreshTokenExpiration);
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("type", "refresh_token")
                .claim("role", user.getRole().getRoleName())
                .setExpiration(date)
                .signWith(key)
                .compact();
    }

    public boolean validateAccessToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
            return Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token).getBody().get("type").equals("access_token");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
            return Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token).getBody().get("type").equals("refresh_token");
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public String getRoleFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().get("role").toString();
    }
}
