package com.example.demo.dto;

/**
 * DTO para confirmar pagamento PIX (webhook)
 */
public record ConfirmarPixDTO(
        String txid
) {
}
