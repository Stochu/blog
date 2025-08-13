package com.universalis.blog.domain.authentication.services.impl;

import com.universalis.blog.domain.authentication.dtos.AuthenticationResponse;
import com.universalis.blog.domain.authentication.entities.RefreshToken;
import com.universalis.blog.exceptions.InvalidTokenException;
import com.universalis.blog.exceptions.TokenExpiredException;
import com.universalis.blog.exceptions.TokenRefreshException;
import com.universalis.blog.security.BlogUserDetails;
import com.universalis.blog.domain.authentication.services.AuthenticationService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.secret}")
    private String secretKey;

    private final Long jwtExpiryMs = 86400000L;

    @Value("${jwt.access-token-expiration:900000}") // 15 minutes
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration:86400000}") // 24 hours
    private Long refreshTokenExpiration;

    @Override
    public AuthenticationResponse authenticate(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        String accessToken = generateAccessToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(
                ((BlogUserDetails) userDetails).getId()
        );

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(accessTokenExpiration / 1000)
                .build();
    }

    @Override
    public AuthenticationResponse refreshToken(String refreshTokenStr) {
        return refreshTokenService.findByToken(refreshTokenStr)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    BlogUserDetails userDetails = new BlogUserDetails(user);
                    String accessToken = generateAccessToken(userDetails);
                    return AuthenticationResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshTokenStr)
                            .tokenType("Bearer")
                            .expiresIn(accessTokenExpiration / 1000)
                            .build();
                })
                .orElseThrow(() -> new TokenRefreshException("Invalid refresh token"));
    }

    private String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public UserDetails validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String username = claims.getSubject();

            if (username != null && !isTokenExpired(token)) {
                return userDetailsService.loadUserByUsername(username);
            }
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("JWT token has expired");
        } catch (JwtException e) {
            throw new InvalidTokenException("Invalid JWT token");
        }
        return null;
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
