package com.example.demo.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa um pedido realizado por um cliente
 */
@Entity
@Table(name = "tb_pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class Pedido extends Auditable implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Integer id;

    @Column(name = "data_pedido", nullable = false)
    private LocalDateTime dataPedido;

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPedido status;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pagamento", nullable = false, length = 20)
    private TipoPagamento tipoPagamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_endereco_entrega")
    private Endereco enderecoEntrega;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cartao")
    private Cartao cartao;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemPedido> itens = new ArrayList<>();

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private PagamentoPix pagamentoPix;

    /**
     * Define a data do pedido automaticamente antes de persistir
     */
    @PrePersist
    protected void onCreate() {
        if (this.dataPedido == null) {
            this.dataPedido = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = StatusPedido.PENDENTE;
        }
    }

    /**
     * Calcula o valor total do pedido
     */
    public BigDecimal calcularValorTotal() {
        return itens.stream()
                .map(ItemPedido::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Adiciona um item ao pedido
     */
    public void adicionarItem(ItemPedido item) {
        item.setPedido(this);
        this.itens.add(item);
    }

    /**
     * Verifica se o pedido pode ser cancelado
     */
    public boolean podeCancelar() {
        return this.status == StatusPedido.PENDENTE || this.status == StatusPedido.PAGO;
    }

    /**
     * Verifica se o pedido está finalizado
     */
    public boolean estaFinalizado() {
        return this.status == StatusPedido.ENTREGUE || this.status == StatusPedido.CANCELADO;
    }

    /**
     * Marca o pedido como PAGO
     */
    public void marcarComoPago() {
        if (this.status != StatusPedido.PENDENTE) {
            throw new IllegalStateException("Apenas pedidos PENDENTES podem ser marcados como PAGO.");
        }
        this.status = StatusPedido.PAGO;
    }

    /**
     * Marca o pedido como EM_PREPARACAO
     */
    public void marcarComoEmPreparacao() {
        if (this.status != StatusPedido.PAGO) {
            throw new IllegalStateException("Apenas pedidos PAGOS podem ser marcados como EM_PREPARACAO.");
        }
        this.status = StatusPedido.EM_PREPARACAO;
    }

    /**
     * Marca o pedido como ENVIADO
     */
    public void marcarComoEnviado() {
        if (this.status != StatusPedido.EM_PREPARACAO) {
            throw new IllegalStateException("Apenas pedidos EM_PREPARACAO podem ser marcados como ENVIADO.");
        }
        this.status = StatusPedido.ENVIADO;
    }

    /**
     * Marca o pedido como ENTREGUE
     */
    public void marcarComoEntregue() {
        if (this.status != StatusPedido.ENVIADO) {
            throw new IllegalStateException("Apenas pedidos ENVIADOS podem ser marcados como ENTREGUE.");
        }
        this.status = StatusPedido.ENTREGUE;
    }

    /**
     * Cancela o pedido
     */
    public void cancelar() {
        if (!podeCancelar()) {
            throw new IllegalStateException("Este pedido não pode ser cancelado no status atual: " + this.status);
        }
        this.status = StatusPedido.CANCELADO;
    }
}
