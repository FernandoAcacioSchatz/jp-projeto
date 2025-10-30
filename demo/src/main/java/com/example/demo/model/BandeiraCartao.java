package com.example.demo.model;

/**
 * Enum para as bandeiras de cartão aceitas
 */
public enum BandeiraCartao {
    VISA("Visa"),
    MASTERCARD("Mastercard"),
    ELO("Elo"),
    AMERICAN_EXPRESS("American Express"),
    HIPERCARD("Hipercard"),
    DINERS("Diners Club");

    private final String descricao;

    BandeiraCartao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
