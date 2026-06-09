package com.myproject.auth_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.access.secret}")
    private String accessSecret;

    @Value("${jwt.refresh.secret}")
    private String refreshSecret;

    @Value("${jwt.access.expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh.expiration}")
    private long refreshExpiration;

    // ==================================================
    // KEYS
    // ==================================================

    private Key getAccessKey() {
        return Keys.hmacShaKeyFor(
                accessSecret.getBytes()
        );
    }

    private Key getRefreshKey() {
        return Keys.hmacShaKeyFor(
                refreshSecret.getBytes()
        );
    }

    // ==================================================
    // ACCESS TOKEN
    // ==================================================

    public String generateAccessToken(
            Long userId,
            String email,
            String role
    ) {

        return Jwts.builder()

                .setSubject(
                        String.valueOf(userId)
                )

                .claim("email", email)

                .claim("role", role)

                .setIssuedAt(new Date())

                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + accessExpiration
                        )
                )

                .signWith(
                        getAccessKey(),
                        SignatureAlgorithm.HS256
                )

                .compact();
    }

    // ==================================================
    // REFRESH TOKEN
    // ==================================================

    public String generateRefreshToken(
            Long userId
    ) {

        return Jwts.builder()

                .setSubject(
                        String.valueOf(userId)
                )

                .setIssuedAt(new Date())

                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + refreshExpiration
                        )
                )

                .signWith(
                        getRefreshKey(),
                        SignatureAlgorithm.HS256
                )

                .compact();
    }

    // ==================================================
    // EXTRACT
    // ==================================================

    public Long extractUserIdFromAccessToken(
            String token
    ) {

        return Long.parseLong(
                getAccessClaims(token)
                        .getSubject()
        );
    }

    public Long extractUserIdFromRefreshToken(
            String token
    ) {

        return Long.parseLong(
                getRefreshClaims(token)
                        .getSubject()
        );
    }

    public String extractRole(
            String token
    ) {

        return getAccessClaims(token)
                .get("role", String.class);
    }

    // ==================================================
    // VALIDATE
    // ==================================================

    public boolean validateAccessToken(
            String token
    ) {

        try {

            getAccessClaims(token);

            return true;

        } catch (Exception e) {

            return false;
        }
    }

    public boolean validateRefreshToken(
            String token
    ) {

        try {

            getRefreshClaims(token);

            return true;

        } catch (Exception e) {

            return false;
        }
    }

    // ==================================================
    // CLAIMS
    // ==================================================

    private Claims getAccessClaims(
            String token
    ) {

        return Jwts.parserBuilder()

                .setSigningKey(
                        getAccessKey()
                )

                .build()

                .parseClaimsJws(token)

                .getBody();
    }

    private Claims getRefreshClaims(
            String token
    ) {

        return Jwts.parserBuilder()

                .setSigningKey(
                        getRefreshKey()
                )

                .build()

                .parseClaimsJws(token)

                .getBody();
    }
}