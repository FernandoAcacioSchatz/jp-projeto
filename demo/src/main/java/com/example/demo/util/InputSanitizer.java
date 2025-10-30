package com.example.demo.util;

import org.springframework.stereotype.Component;

/**
 * 🛡️ UTILITÁRIO DE SANITIZAÇÃO
 * 
 * Previne ataques XSS (Cross-Site Scripting) e SQL Injection
 * removendo caracteres perigosos de inputs do usuário.
 */
@Component
public class InputSanitizer {

    /**
     * Remove tags HTML e caracteres perigosos para prevenir XSS
     */
    public String sanitizeHtml(String input) {
        if (input == null) {
            return null;
        }
        
        return input
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll("\"", "&quot;")
            .replaceAll("'", "&#x27;")
            .replaceAll("/", "&#x2F;");
    }

    /**
     * Remove caracteres perigosos para SQL Injection
     * 
     * ⚠️ IMPORTANTE: Esta é uma proteção adicional.
     * O Spring JPA já previne SQL Injection usando Prepared Statements.
     */
    public String sanitizeSql(String input) {
        if (input == null) {
            return null;
        }
        
        // Remove caracteres comuns em SQL Injection
        return input
            .replaceAll("--", "")
            .replaceAll(";", "")
            .replaceAll("'", "''")  // Escapa aspas simples
            .replaceAll("\\\\", "")
            .replaceAll("xp_", "")
            .replaceAll("sp_", "");
    }

    /**
     * Valida se o input não contém caracteres suspeitos
     */
    public boolean isSafe(String input) {
        if (input == null) {
            return true;
        }
        
        // Lista de padrões suspeitos
        String[] dangerousPatterns = {
            "<script", "javascript:", "onerror=", "onload=",
            "eval(", "expression(", "vbscript:", "onclick=",
            "../../", "../", "./", // Path traversal
            "cmd.exe", "powershell", "bash", "sh -c" // Command injection
        };
        
        String lowerInput = input.toLowerCase();
        for (String pattern : dangerousPatterns) {
            if (lowerInput.contains(pattern)) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Sanitiza completamente: HTML + SQL + validação
     */
    public String sanitizeComplete(String input) {
        if (input == null || input.isBlank()) {
            return input;
        }
        
        if (!isSafe(input)) {
            throw new SecurityException("Input contém caracteres potencialmente perigosos.");
        }
        
        return sanitizeSql(sanitizeHtml(input.trim()));
    }
}
