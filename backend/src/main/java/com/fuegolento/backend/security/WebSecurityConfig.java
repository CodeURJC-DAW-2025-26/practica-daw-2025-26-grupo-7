package com.fuegolento.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private RepositoryUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setHideUserNotFoundExceptions(false);
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        http
            .authorizeHttpRequests(authorize -> authorize

                // ===============================
                // PUBLIC (not authenticated)
                // ===============================
                .requestMatchers("/", "/index").permitAll()

                // Info sections (public)
                .requestMatchers(
                    "/our-grill",      // Nuestra brasa
                    "/gallery",        // Galería
                    "/contact",        // Contacto
                    "/booking"         // Reservar (informational or booking page)
                ).permitAll()

                // Menu (public)
                .requestMatchers("/menu", "/menu/**").permitAll()
                .requestMatchers("/dish", "/dish/**").permitAll()

                // Auth pages (public)
                .requestMatchers("/login", "/register", "/loginerror", "/banned").permitAll()

                // Static resources (public)
                .requestMatchers(
                    "/assets/**",
                    "/images/**",
                    "/css/**",
                    "/js/**",
                    "/favicon.ico"
                ).permitAll()

                // ===============================
                // USER (authenticated)
                // ===============================
                .requestMatchers(
                    "/profile",
                    "/profile/**",
                    "/cart",
                    "/cart/**",
                    "/order",
                    "/order/**",
                    "/orders",
                    "/orders/**"
                ).hasRole("USER")

                // ===============================
                // ADMIN
                // ===============================
                .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
                .requestMatchers("/kitchen", "/kitchen/**").hasRole("ADMIN")

                // Anything else -> authenticated (safe default)
                .anyRequest().authenticated()
            )

            // ===============================
            // Login / Logout
            // ===============================
            .formLogin(formLogin -> formLogin
                .loginPage("/login")
                .defaultSuccessUrl("/profile", true)
                .failureHandler((request, response, exception) -> {
                    // If the account is disabled (banned), redirect to a dedicated page
                    if (exception instanceof org.springframework.security.authentication.DisabledException) {
                        response.sendRedirect("/banned");
                    } else {
                        response.sendRedirect("/loginerror");
                    }
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }
}
