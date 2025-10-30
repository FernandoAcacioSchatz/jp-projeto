package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 🔒 SERVIÇO DE BLOQUEIO DE CONTA (Account Lockout)
 * 
 * Previne ataques de força bruta bloqueando contas após N tentativas falhadas.
 * 
 * TODO: Implementar persistência em banco de dados para ambiente distribuído
 */
@Service
public class AccountLockoutService {

    private static final int MAX_ATTEMPTS = 5; // Máximo de tentativas
    private static final long LOCKOUT_DURATION_MINUTES = 15; // Tempo de bloqueio em minutos

    // Armazena tentativas de login: email -> contador
    private final ConcurrentHashMap<String, Integer> attemptCache = new ConcurrentHashMap<>();
    
    // Armazena quando o bloqueio expira: email -> timestamp
    private final ConcurrentHashMap<String, LocalDateTime> lockoutCache = new ConcurrentHashMap<>();

    /**
     * Registra uma tentativa de login falhada.
     * 
     * @param email Email do usuário
     */
    public void registerFailedAttempt(String email) {
        int attempts = attemptCache.getOrDefault(email, 0) + 1;
        attemptCache.put(email, attempts);

        if (attempts >= MAX_ATTEMPTS) {
            lockAccount(email);
        }
    }

    /**
     * Reseta o contador de tentativas após login bem-sucedido.
     * 
     * @param email Email do usuário
     */
    public void resetAttempts(String email) {
        attemptCache.remove(email);
        lockoutCache.remove(email);
    }

    /**
     * Verifica se a conta está bloqueada.
     * 
     * @param email Email do usuário
     * @return true se a conta está bloqueada
     */
    public boolean isAccountLocked(String email) {
        LocalDateTime lockoutTime = lockoutCache.get(email);
        
        if (lockoutTime == null) {
            return false;
        }

        // Verifica se o tempo de bloqueio expirou
        if (LocalDateTime.now().isAfter(lockoutTime)) {
            // Bloqueio expirou, libera a conta
            unlockAccount(email);
            return false;
        }

        return true;
    }

    /**
     * Retorna o número de tentativas restantes antes do bloqueio.
     * 
     * @param email Email do usuário
     * @return Número de tentativas restantes
     */
    public int getRemainingAttempts(String email) {
        int attempts = attemptCache.getOrDefault(email, 0);
        return Math.max(0, MAX_ATTEMPTS - attempts);
    }

    /**
     * Retorna quando o bloqueio expira.
     * 
     * @param email Email do usuário
     * @return LocalDateTime quando expira, ou null se não está bloqueado
     */
    public LocalDateTime getLockoutExpiration(String email) {
        return lockoutCache.get(email);
    }

    /**
     * Bloqueia a conta por LOCKOUT_DURATION_MINUTES minutos.
     * 
     * @param email Email do usuário
     */
    private void lockAccount(String email) {
        LocalDateTime lockoutExpiration = LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES);
        lockoutCache.put(email, lockoutExpiration);
    }

    /**
     * Desbloqueia a conta manualmente.
     * 
     * @param email Email do usuário
     */
    private void unlockAccount(String email) {
        attemptCache.remove(email);
        lockoutCache.remove(email);
    }
}
