package com.fuegolento.backend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey jwtSecret = Jwts.SIG.HS256.key().build();
    private final JwtParser jwtParser = Jwts.parser().verifyWith(jwtSecret).build();

    public String tokenStringFromHeaders(HttpServletRequest req) {
        String bearerToken = req.getHeader(HttpHeaders.AUTHORIZATION);

        if (bearerToken == null || bearerToken.isBlank()) {
            return null;
        }

        if (!bearerToken.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header does not start with Bearer");
        }

        return bearerToken.substring(7);
    }

    public String tokenStringFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (TokenType.ACCESS.cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    public Claims validateToken(String token) {
        return jwtParser.parseSignedClaims(token).getPayload();
    }

    public String generateAccessToken(UserDetails userDetails) {
        return buildToken(TokenType.ACCESS, userDetails).compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(TokenType.REFRESH, userDetails).compact();
    }

    private JwtBuilder buildToken(TokenType tokenType, UserDetails userDetails) {
        Date currentDate = new Date();
        Date expiryDate = Date.from(currentDate.toInstant().plus(tokenType.duration));

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities())
                .claim("type", tokenType.name())
                .issuedAt(currentDate)
                .expiration(expiryDate)
                .signWith(jwtSecret);
    }
}