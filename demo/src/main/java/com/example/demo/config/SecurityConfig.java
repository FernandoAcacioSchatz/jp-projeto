package com.example.demo.config;

// 1. IMPORTS NECESSÁRIOS
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // Habilita CORS com a configuração customizada
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                // Desabilita CSRF (correto para REST APIs stateless)
                .csrf(csrf -> csrf.disable())
                // Gerenciamento de sessão - STATELESS (JWT não precisa de sessão)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // Permite acesso à página inicial da API
                        .requestMatchers("/").permitAll()
                        
                        // Endpoints de autenticação públicos
                        .requestMatchers("/auth/**").permitAll()
                        
                        // Permite criar cliente e fornecedor
                        .requestMatchers(HttpMethod.POST, "/cliente").permitAll()
                        .requestMatchers(HttpMethod.POST, "/fornecedor").permitAll()
                        
                        // Permite listar produtos sem autenticação
                        .requestMatchers(HttpMethod.GET, "/produto").permitAll()
                        .requestMatchers(HttpMethod.GET, "/produto/**").permitAll()
                        
                        // Exige autenticação para qualquer outra coisa
                        .anyRequest().authenticated()
                )
                // Adiciona o filtro JWT antes do UsernamePasswordAuthenticationFilter
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                // Entry point customizado para erros de autenticação
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint))
                // Headers de segurança
                .headers(headers -> headers
                    // Previne clickjacking
                    .frameOptions(frame -> frame.deny())
                    // HSTS - Força HTTPS por 1 ano
                    // ⚠️ DESCOMENTADO PARA PRODUÇÃO, COMENTADO PARA LOCALHOST
                    // Em localhost use HTTP, em produção com SSL use HTTPS
                    // .httpStrictTransportSecurity(hsts -> hsts
                    //     .includeSubDomains(true)
                    //     .maxAgeInSeconds(31536000))
                    // Previne MIME sniffing
                    .contentTypeOptions(contentType -> contentType.disable())
                );

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(passwordEncoder());
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
