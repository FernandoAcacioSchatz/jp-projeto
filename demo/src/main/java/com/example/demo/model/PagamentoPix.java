package com.example.demo.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 💰 ENTIDADE PAGAMENTO PIX
 * 
 * Armazena informações de pagamentos via PIX
 * Relacionado a um pedido específico
 */
@Entity
@Table(name = "tb_pagamentos_pix")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class PagamentoPix extends Auditable implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pagamento_pix")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "id_pedido", nullable = false, unique = true)
    @JsonIgnore
    private Pedido pedido;

    /**
     * Código PIX no formato EMV (copia e cola)
     * Formato padrão do Banco Central do Brasil
     */
    @NotNull
    @Column(name = "codigo_pix", nullable = false, length = 500, columnDefinition = "TEXT")
    private String codigoPix;

    /**
     * QR Code do PIX em Base64 (imagem PNG)
     */
    @NotNull
    @Column(name = "qrcode_pix", nullable = false, columnDefinition = "LONGTEXT")
    private String qrCodePix;

    /**
     * Valor do pagamento PIX
     */
    @NotNull
    @Positive
    @Column(name = "valor_pix", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorPix;

    /**
     * Data e hora de expiração do PIX (padrão: 15 minutos)
     */
    @NotNull
    @Column(name = "data_expiracao", nullable = false)
    private LocalDateTime dataExpiracao;

    /**
     * Status do pagamento PIX
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status_pagamento", nullable = false, length = 20)
    private StatusPagamentoPix statusPagamento = StatusPagamentoPix.PENDENTE;

    /**
     * Data e hora da confirmação do pagamento (quando statusPagamento = CONFIRMADO)
     */
    @Column(name = "data_confirmacao")
    private LocalDateTime dataConfirmacao;

    /**
     * ID da transação PIX (retornado pela instituição financeira)
     */
    @Column(name = "txid", length = 100)
    private String txid;

    /**
     * Verifica se o PIX está expirado
     */
    public boolean isExpirado() {
        return LocalDateTime.now().isAfter(dataExpiracao);
    }

    /**
     * Confirma o pagamento PIX
     */
    public void confirmarPagamento(String txid) {
        this.statusPagamento = StatusPagamentoPix.CONFIRMADO;
        this.dataConfirmacao = LocalDateTime.now();
        this.txid = txid;
    }

    /**
     * Expira o pagamento PIX
     */
    public void expirarPagamento() {
        this.statusPagamento = StatusPagamentoPix.EXPIRADO;
    }

    /**
     * Cancela o pagamento PIX
     */
    public void cancelarPagamento() {
        this.statusPagamento = StatusPagamentoPix.CANCELADO;
    }
}
