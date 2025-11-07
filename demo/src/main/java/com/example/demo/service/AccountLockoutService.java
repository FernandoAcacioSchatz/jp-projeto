package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AccountLockoutService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MINUTES = 15;

    private final ConcurrentHashMap<String, Integer> attemptCache = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, LocalDateTime> lockoutCache = new ConcurrentHashMap<>();

    public void registerFailedAttempt(String email) {
        int attempts = attemptCache.getOrDefault(email, 0) + 1;
        attemptCache.put(email, attempts);

        if (attempts >= MAX_ATTEMPTS) {
            lockAccount(email);
        }
    }

    public void resetAttempts(String email) {
        attemptCache.remove(email);
        lockoutCache.remove(email);
    }

    public boolean isAccountLocked(String email) {
        LocalDateTime lockoutTime = lockoutCache.get(email);

        if (lockoutTime == null) {
            return false;
        }

        if (LocalDateTime.now().isAfter(lockoutTime)) {

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
