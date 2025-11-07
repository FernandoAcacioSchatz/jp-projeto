package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogService {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogService.class);

    public void logSuccessfulLogin(String email, String ipAddress) {
        logger.info("[AUDIT] LOGIN_SUCCESS | User: {} | IP: {} | Time: {}",
                email, ipAddress, LocalDateTime.now());
    }

    public void logFailedLogin(String email, String ipAddress, String reason) {
        logger.warn("[AUDIT] LOGIN_FAILED | User: {} | IP: {} | Reason: {} | Time: {}",
                email, ipAddress, reason, LocalDateTime.now());
    }

    public void logAccountLockout(String email, String ipAddress) {
        logger.error("[AUDIT] ACCOUNT_LOCKED | User: {} | IP: {} | Time: {}",
                email, ipAddress, LocalDateTime.now());
    }

    public void logCardCreated(Long clienteId, String cardLastDigits, String ipAddress) {
        logger.info("[AUDIT] CARD_CREATED | ClienteId: {} | LastDigits: {} | IP: {} | Time: {}",
                clienteId, cardLastDigits, ipAddress, LocalDateTime.now());
    }

    public void logCardDeleted(Long clienteId, Integer cardId, String ipAddress) {
        logger.warn("[AUDIT] CARD_DELETED | ClienteId: {} | CardId: {} | IP: {} | Time: {}",
                clienteId, cardId, ipAddress, LocalDateTime.now());
    }

    public void logOrderCreated(Long clienteId, Long pedidoId, Double valor, String ipAddress) {
        logger.info("[AUDIT] ORDER_CREATED | ClienteId: {} | PedidoId: {} | Value: {} | IP: {} | Time: {}",
                clienteId, pedidoId, valor, ipAddress, LocalDateTime.now());
    }

    public void logPixPaymentConfirmed(Long pedidoId, String txid, String ipAddress) {
        logger.info("[AUDIT] PIX_CONFIRMED | PedidoId: {} | TxId: {} | IP: {} | Time: {}",
                pedidoId, txid, ipAddress, LocalDateTime.now());
    }

    public void logAccessDenied(String email, String resource, String ipAddress) {
        logger.error("[AUDIT] ACCESS_DENIED | User: {} | Resource: {} | IP: {} | Time: {}",
                email, resource, ipAddress, LocalDateTime.now());
    }

    public void logSensitiveDataModified(String entity, Long entityId, String field, String ipAddress) {
        logger.warn("[AUDIT] DATA_MODIFIED | Entity: {} | EntityId: {} | Field: {} | IP: {} | Time: {}",
                entity, entityId, field, ipAddress, LocalDateTime.now());
    }

    public void logSystemError(String errorType, String message, String stackTrace) {
        logger.error("[AUDIT] SYSTEM_ERROR | Type: {} | Message: {} | StackTrace: {} | Time: {}",
                errorType, message, stackTrace, LocalDateTime.now());
    }
}
