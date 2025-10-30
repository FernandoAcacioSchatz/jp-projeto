package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * 🌐 CONFIGURAÇÃO DE CORS (Cross-Origin Resource Sharing)
 * 
 * Permite que o frontend (React, Angular, Vue, etc.) acesse a API
 * de um domínio diferente.
 * 
 * ⚠️ PRODUÇÃO: Ajuste os valores para o domínio real do frontend!
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 🔒 PRODUÇÃO: Domínios específicos do frontend
        // DESENVOLVIMENTO: Pode usar "*" temporariamente
        // Usar variável de ambiente: ALLOWED_ORIGINS
        String allowedOrigins = System.getenv("ALLOWED_ORIGINS");
        if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
            // Produção: ex: "https://meusite.com,https://www.meusite.com"
            configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
            configuration.setAllowCredentials(true); // Permitir cookies/credentials
        } else {
            // Desenvolvimento: permite qualquer origem
            configuration.setAllowedOrigins(Arrays.asList("*"));
            configuration.setAllowCredentials(false);
        }
        
        // Permite todos os métodos HTTP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        
        // Permite todos os headers
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Expõe headers personalizados para o frontend
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Total-Count"
        ));
        
        // Tempo que o navegador pode cachear a resposta de preflight
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
