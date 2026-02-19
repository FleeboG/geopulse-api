package com.geopulse.geopulse_api;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

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
      .authorizeHttpRequests(auth -> auth
        // IMPORTANT: don't lock down the error pipeline
        .dispatcherTypeMatchers(DispatcherType.ERROR, DispatcherType.FORWARD).permitAll()
        .requestMatchers("/error").permitAll()

        // Public routes
        .requestMatchers(HttpMethod.GET, "/api/v1/ping", "/api/v1/ping/").permitAll()

        // Everything else secured
        .anyRequest().authenticated()
      )
      .httpBasic(Customizer.withDefaults())
      .build();
  }
}

