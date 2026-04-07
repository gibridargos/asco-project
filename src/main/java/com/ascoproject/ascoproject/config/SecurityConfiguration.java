package com.ascoproject.ascoproject.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 🔴 CSRF o‘chiramiz (API uchun kerak emas)
                .csrf(AbstractHttpConfigurer::disable)

                // 🔴 CORS yoqiladi
                .cors(Customizer.withDefaults())

                // 🔥 AUTH RULES
                .authorizeHttpRequests(auth -> auth
                        // ✅ Preflight requestlarga ruxsat
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ✅ Ochiq endpointlar
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/send").permitAll()

                        // ✅ CSV upload endpointlar
                        .requestMatchers("/api/v1/bot/import-tax-info-csv").permitAll()
                        .requestMatchers("/api/v1/bot/import-info-entity-csv").permitAll()

                        // ✅ Swagger
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // 🔐 qolgan hamma endpoint → login kerak
                        .anyRequest().authenticated()
                )

                // 🔴 Stateless (JWT uchun)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 🔴 Auth provider
                .authenticationProvider(authenticationProvider)

                // 🔴 JWT filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 🔥 CORS CONFIG (MUHIM)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // ❗ EXACT origin ( / yo‘q )
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:5173",
                "https://asco.up.railway.app",
                "https://serena-psi.vercel.app",
                "https://senera-psi.vercel.app"
        ));

        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        config.setAllowedHeaders(List.of("*"));

        // 🔥 TOKEN / COOKIE uchun
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
