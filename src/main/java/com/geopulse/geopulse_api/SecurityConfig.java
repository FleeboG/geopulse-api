package com.geopulse.geopulse_api;

import com.geopulse.geopulse_api.security.JwtAuthFilter;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

  private final JwtAuthFilter jwtAuthFilter;

  public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
    this.jwtAuthFilter = jwtAuthFilter;
  }

  @PostConstruct
  void loaded() {
    System.out.println(">>> Custom SecurityConfig LOADED");
  }

  @Bean
  @Order(1)
  SecurityFilterChain actuatorChain(HttpSecurity http) throws Exception {
    return http
      .securityMatcher("/actuator/**")
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
        .anyRequest().authenticated()
      )
      .httpBasic(Customizer.withDefaults())
      .build();
  }

  @Bean
  @Order(2)
  SecurityFilterChain appChain(HttpSecurity http) throws Exception {
    return http
      .csrf(csrf -> csrf.disable())
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(auth -> auth
        .dispatcherTypeMatchers(DispatcherType.ERROR, DispatcherType.FORWARD).permitAll()
        .requestMatchers("/error").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/v1/ping", "/api/v1/ping/").permitAll()
        .requestMatchers("/api/v1/auth/**").permitAll()
        .anyRequest().authenticated()
      )
      .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
      .build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}