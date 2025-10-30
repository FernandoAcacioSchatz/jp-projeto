package com.example.demo.model;

import java.io.Serializable;

import com.example.demo.config.EncryptionConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 🏦 ENTIDADE CARTÃO
 * 
 * Armazena cartões de crédito/débito dos clientes de forma SEGURA.
 * 
 * ⚠️ SEGURANÇA PCI-DSS:
 * - NUNCA armazenar número completo do cartão
 * - NUNCA armazenar CVV/CVC
 * - Armazena apenas últimos 4 dígitos (mascarado)
 * - Número mascarado é CRIPTOGRAFADO no banco (AES-256)
 * - Em produção: usar gateway de pagamento (Stripe, PagSeguro, etc.)
 */
@Entity
@Table(name = "tb_cartoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class Cartao extends Auditable implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cartao")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    @JsonIgnore
    private Cliente cliente;

    /**
     * Número do cartão MASCARADO (ex: "**** **** **** 1234")
     * Armazena apenas os últimos 4 dígitos para identificação
     * 
     * 🔐 CRIPTOGRAFADO automaticamente com AES-256 ao salvar no banco
     */
    @NotBlank
    @Column(name = "numero_mascarado", nullable = false, length = 255)
    @Convert(converter = EncryptionConverter.class)
    private String numeroMascarado;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "bandeira", nullable = false, length = 20)
    private BandeiraCartao bandeira;

    @NotBlank
    @Column(name = "nome_titular", nullable = false, length = 100)
    private String nomeTitular;

    /**
     * Mês de validade (1-12)
     */
    @NotNull
    @Min(1)
    @Max(12)
    @Column(name = "mes_validade", nullable = false)
    private Integer mesValidade;

    /**
     * Ano de validade (formato completo, ex: 2025)
     */
    @NotNull
    @Min(2024)
    @Column(name = "ano_validade", nullable = false)
    private Integer anoValidade;

    /**
     * Indica se é o cartão principal do cliente (padrão no checkout)
     */
    @Column(name = "is_principal", nullable = false)
    private Boolean isPrincipal = false;

    /**
     * Apelido/nome amigável para o cartão (ex: "Cartão Corporativo", "Cartão Pessoal")
     */
    @Column(name = "apelido", length = 50)
    private String apelido;

    /**
     * Verifica se o cartão está vencido
     */
    public boolean isVencido() {
        java.time.LocalDate hoje = java.time.LocalDate.now();
        int anoAtual = hoje.getYear();
        int mesAtual = hoje.getMonthValue();

        if (this.anoValidade < anoAtual) {
            return true;
        }
        if (this.anoValidade == anoAtual && this.mesValidade < mesAtual) {
            return true;
        }
        return false;
    }

    /**
     * Retorna a descrição completa do cartão (para exibição)
     */
    public String getDescricaoCompleta() {
        StringBuilder sb = new StringBuilder();
        sb.append(bandeira.getDescricao());
        sb.append(" ");
        sb.append(numeroMascarado);
        if (apelido != null && !apelido.isBlank()) {
            sb.append(" (").append(apelido).append(")");
        }
        return sb.toString();
    }
}
