package com.example.demo.util;

import org.springframework.stereotype.Component;

@Component
public class InputSanitizer {

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

    public String sanitizeSql(String input) {
        if (input == null) {
            return null;
        }

        return input
                .replaceAll("--", "")
                .replaceAll(";", "")
                .replaceAll("'", "''") // Escapa aspas simples
                .replaceAll("\\\\", "")
                .replaceAll("xp_", "")
                .replaceAll("sp_", "");
    }

    public boolean isSafe(String input) {
        if (input == null) {
            return true;
        }

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
