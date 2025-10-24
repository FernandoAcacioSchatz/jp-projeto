package com.example.demo.config;

// 1. IMPORTAR O @Bean
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        // 3. ADICIONAR A ANOTAÇÃO @Bean
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

                http.csrf(csrf -> csrf.disable()) // O correto é "csrf -> csrf.disable()"

                                .authorizeHttpRequests(authz -> authz
                                                .requestMatchers(HttpMethod.POST, "/usuario").permitAll()
                                                .anyRequest().authenticated())
                                .httpBasic(withDefaults());

                return http.build();
        }

        // (Aqui é onde vamos adicionar os outros Beans que faltam)
}