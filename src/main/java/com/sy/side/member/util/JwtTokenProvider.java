package com.sy.side.member.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Getter
    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    public String generateToken(Long userId, String email, String nickname) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .addClaims(Map.of(
                        "email", email,
                        "nickname", nickname
                ))
                .signWith(SignatureAlgorithm.HS256, secret.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}