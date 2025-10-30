package com.example.demo.model;

/**
 * Enum para status de pagamento PIX
 */
public enum StatusPagamentoPix {
    PENDENTE("Aguardando Pagamento"),
    CONFIRMADO("Pagamento Confirmado"),
    EXPIRADO("PIX Expirado"),
    CANCELADO("Pagamento Cancelado");

    private final String descricao;

    StatusPagamentoPix(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
