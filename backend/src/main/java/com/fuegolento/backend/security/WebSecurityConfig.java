package com.fuegolento.backend.security;

import com.fuegolento.backend.security.jwt.JwtRequestFilter;
import com.fuegolento.backend.security.jwt.UnauthorizedHandlerJwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet. RequestDispatcher ;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private RepositoryUserDetailsService userDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        http
                .securityMatcher("/api/**")
                .exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt));

        http
                .authorizeHttpRequests(authorize -> authorize

                        // ===============================
                        // PUBLIC API
                        // ===============================
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/refresh").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/logout").permitAll()

                        .requestMatchers("/api/menu/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/v1/dishes").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/dishes/page").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/dishes/*").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/v1/images/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/images/*/media").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()

                        // ===============================
                        // USER API
                        // ===============================
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/me").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/me").hasAnyRole("USER", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/v1/orders/my").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders/my-cart").hasAnyRole("USER", "ADMIN")

                        .requestMatchers("/api/v1/orders/cart/**").hasAnyRole("USER", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/orders/*/copies").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders/*/invoice").hasAnyRole("USER", "ADMIN")

                        // ===============================
                        // ADMIN API - DISHES / IMAGES
                        // ===============================
                        .requestMatchers(HttpMethod.POST, "/api/v1/dishes").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/dishes/*/image").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/dishes/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/dishes/*").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/v1/images/*/media").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/images/*").hasRole("ADMIN")

                        // ===============================
                        // ADMIN API - USERS
                        // ===============================
                        .requestMatchers(HttpMethod.GET, "/api/v1/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/*/ban").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/*/unban").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/*").hasRole("ADMIN")

                        // ===============================
                        // ADMIN API - ORDERS
                        // ===============================
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/orders/*/status").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/orders/*").hasRole("ADMIN")

                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                );

        http.formLogin(formLogin -> formLogin.disable());
        http.csrf(csrf -> csrf.disable());
        http.httpBasic(httpBasic -> httpBasic.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        http
                .authorizeHttpRequests(authorize -> authorize

                        // ===============================
                        // PUBLIC WEB
                        // ===============================
                        .requestMatchers("/", "/index").permitAll()
                        .requestMatchers("/our-grill", "/gallery", "/contact", "/booking").permitAll()
                        .requestMatchers("/menu", "/menu/**").permitAll()
                        .requestMatchers("/dish", "/dish/**").permitAll()
                        .requestMatchers("/login", "/register", "/loginerror", "/banned").permitAll()
                        .requestMatchers("/error").permitAll()

                        .requestMatchers(
                                "/assets/**",
                                "/images/**",
                                "/img/**",
                                "/vendor/**",
                                "/css/**",
                                "/js/**",
                                "/favicon.ico"
                        ).permitAll()

                        // ===============================
                        // USER WEB
                        // ===============================
                        .requestMatchers(
                                "/profile",
                                "/profile/**",
                                "/order",
                                "/order/**",
                                "/orders",
                                "/orders/**"
                        ).hasRole("USER")

                        // ===============================
                        // ADMIN WEB
                        // ===============================
                        .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")

                        .anyRequest().permitAll()
                )

                .exceptionHandling(ex -> ex.accessDeniedHandler(
                        (request, response, accessDeniedException) -> {
                            request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 403);
                            request.setAttribute(RequestDispatcher.ERROR_MESSAGE, "Forbidden");
                            request.getRequestDispatcher("/error").forward(request, response);
                        }
                ))

                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .defaultSuccessUrl("/profile", true)
                        .failureHandler((request, response, exception) -> {
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