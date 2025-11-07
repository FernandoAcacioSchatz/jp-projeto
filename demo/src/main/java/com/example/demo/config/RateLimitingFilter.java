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

@Component
public class RateLimitingFilter implements Filter {

    private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();

    private final Map<String, Long> requestTimestamps = new ConcurrentHashMap<>();

    private static final int MAX_REQUESTS_PER_MINUTE = 100;
    private static final long MINUTE_IN_MILLIS = 60_000;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientIP = getClientIP(httpRequest);

        if (!isAllowed(clientIP)) {
            httpResponse.setStatus(429); 
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write(
                    "{\"error\": \"Rate limit exceeded\", " +
                            "\"message\": \"Muitas requisições. Tente novamente em 1 minuto.\", " +
                            "\"status\": 429}");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isAllowed(String clientIP) {
        long currentTime = System.currentTimeMillis();

        requestTimestamps.entrySet().removeIf(entry -> currentTime - entry.getValue() > MINUTE_IN_MILLIS);

        if (!requestCounts.containsKey(clientIP)) {
            requestCounts.put(clientIP, new AtomicInteger(1));
            requestTimestamps.put(clientIP, currentTime);
            return true;
        }

        Long firstRequestTimeObj = requestTimestamps.get(clientIP);

        if (firstRequestTimeObj == null) {
            requestCounts.put(clientIP, new AtomicInteger(1));
            requestTimestamps.put(clientIP, currentTime);
            return true;
        }

        long firstRequestTime = firstRequestTimeObj.longValue();

        if (currentTime - firstRequestTime > MINUTE_IN_MILLIS) {
            requestCounts.put(clientIP, new AtomicInteger(1));
            requestTimestamps.put(clientIP, currentTime);
            return true;
        }

        int currentCount = requestCounts.get(clientIP).incrementAndGet();
        return currentCount <= MAX_REQUESTS_PER_MINUTE;
    }

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
