package com.example.demo.config;

import com.example.demo.security.JwtAuthFilter;
import com.example.demo.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter,
                          CustomUserDetailsService userDetailsService) {
        this.jwtAuthFilter   = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    private static final String[] PUBLIC_URLS = {
        "/", "/products/**", "/api/auth/**", "/api/products/**",
        "/api/categories/**", "/api/brands/**", "/api/reviews/**",
        "/api/wishlist/*/status",
        "/auth/**", "/static/**", "/css/**", "/js/**", "/images/**", "/error"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    // ── Spring Security 7: dùng AuthenticationManagerBuilder thay DaoAuthenticationProvider ──
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder =
            http.getSharedObject(AuthenticationManagerBuilder.class);
        builder
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
        return builder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthenticationManager authManager) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationManager(authManager)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_URLS).permitAll()
                .requestMatchers(
                    "/account/**", "/cart/**", "/checkout/**",
                    "/api/cart/**", "/api/orders/**", "/api/wishlist/**")
                    .hasAnyRole("CUSTOMER", "STAFF", "ADMIN")
                .requestMatchers("/staff/**", "/api/staff/**")
                    .hasAnyRole("STAFF", "ADMIN")
                .requestMatchers("/api/admin/orders/**")
                    .hasAnyRole("STAFF", "ADMIN")
                .requestMatchers("/admin/**", "/api/admin/**")
                    .hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}