package com.example.demo.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 🛡️ RATE LIMITING FILTER
 * 
 * Previne ataques de brute force limitando o número de requisições
 * por IP em um período de tempo.
 * 
 * Configuração atual: 100 requisições por minuto por IP
 * 
 * ⚠️ PRODUÇÃO: Use biblioteca profissional como Bucket4j ou Redis
 */
@Component
public class RateLimitingFilter implements Filter {

    // Armazena: IP -> contagem de requisições
    private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    
    // Armazena: IP -> timestamp da primeira requisição
    private final Map<String, Long> requestTimestamps = new ConcurrentHashMap<>();
    
    // Configuração
    private static final int MAX_REQUESTS_PER_MINUTE = 100;
    private static final long MINUTE_IN_MILLIS = 60_000;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String clientIP = getClientIP(httpRequest);
        
        // Verifica rate limit
        if (!isAllowed(clientIP)) {
            httpResponse.setStatus(429); // Too Many Requests
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write(
                "{\"error\": \"Rate limit exceeded\", " +
                "\"message\": \"Muitas requisições. Tente novamente em 1 minuto.\", " +
                "\"status\": 429}"
            );
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * Verifica se o IP ainda está dentro do limite de requisições
     */
    private boolean isAllowed(String clientIP) {
        long currentTime = System.currentTimeMillis();
        
        // Limpa dados antigos (mais de 1 minuto)
        requestTimestamps.entrySet().removeIf(entry -> 
            currentTime - entry.getValue() > MINUTE_IN_MILLIS
        );
        
        // Primeira requisição deste IP
        if (!requestCounts.containsKey(clientIP)) {
            requestCounts.put(clientIP, new AtomicInteger(1));
            requestTimestamps.put(clientIP, currentTime);
            return true;
        }
        
        long firstRequestTime = requestTimestamps.get(clientIP);
        
        // Se passou mais de 1 minuto, reseta o contador
        if (currentTime - firstRequestTime > MINUTE_IN_MILLIS) {
            requestCounts.put(clientIP, new AtomicInteger(1));
            requestTimestamps.put(clientIP, currentTime);
            return true;
        }
        
        // Incrementa e verifica limite
        int currentCount = requestCounts.get(clientIP).incrementAndGet();
        return currentCount <= MAX_REQUESTS_PER_MINUTE;
    }

    /**
     * Obtém o IP real do cliente (considera proxies)
     */
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }
        
        return request.getRemoteAddr();
    }
}
