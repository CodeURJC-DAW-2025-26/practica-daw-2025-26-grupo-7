package com.fuegolento.backend.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class UserLoginService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    public UserLoginService(AuthenticationManager authenticationManager,
                            UserDetailsService userDetailsService,
                            JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public ResponseEntity<AuthResponse> login(HttpServletResponse response, LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails user = userDetailsService.loadUserByUsername(loginRequest.getUsername());

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        response.addCookie(buildTokenCookie(TokenType.ACCESS, accessToken));
        response.addCookie(buildTokenCookie(TokenType.REFRESH, refreshToken));

        AuthResponse authResponse = new AuthResponse(
                AuthResponse.Status.SUCCESS,
                "Login successful",
                null,
                accessToken,
                refreshToken
        );

        return ResponseEntity.ok(authResponse);
    }

    public ResponseEntity<AuthResponse> refresh(HttpServletResponse response, String refreshToken) {
        try {
            var claims = jwtTokenProvider.validateToken(refreshToken);
            UserDetails user = userDetailsService.loadUserByUsername(claims.getSubject());

            String newAccessToken = jwtTokenProvider.generateAccessToken(user);
            response.addCookie(buildTokenCookie(TokenType.ACCESS, newAccessToken));

            AuthResponse authResponse = new AuthResponse(
                    AuthResponse.Status.SUCCESS,
                    "Token refreshed successfully",
                    null,
                    newAccessToken,
                    refreshToken
            );

            return ResponseEntity.ok(authResponse);

        } catch (Exception e) {
            AuthResponse authResponse = new AuthResponse(
                    AuthResponse.Status.FAILURE,
                    "Failure while processing refresh token",
                    e.getMessage(),
                    null,
                    null
            );
            return ResponseEntity.status(401).body(authResponse);
        }
    }

    public ResponseEntity<AuthResponse> logout(HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        response.addCookie(removeTokenCookie(TokenType.ACCESS));
        response.addCookie(removeTokenCookie(TokenType.REFRESH));

        AuthResponse authResponse = new AuthResponse(
                AuthResponse.Status.SUCCESS,
                "Logout successful"
        );

        return ResponseEntity.ok(authResponse);
    }

    private Cookie buildTokenCookie(TokenType type, String token) {
        Cookie cookie = new Cookie(type.cookieName, token);
        cookie.setMaxAge((int) type.duration.getSeconds());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }

    private Cookie removeTokenCookie(TokenType type) {
        Cookie cookie = new Cookie(type.cookieName, "");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }
}