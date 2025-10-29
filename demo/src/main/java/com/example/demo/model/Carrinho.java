package com.example.demo.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa o carrinho de compras de um cliente
 * Cada cliente tem apenas um carrinho ativo
 */
@Entity
@Table(name = "tb_carrinho")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class Carrinho extends Auditable implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrinho")
    private Integer id;

    @OneToMany(mappedBy = "carrinho", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemCarrinho> itens = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false, unique = true)
    private Cliente cliente;

    /**
     * Calcula o valor total do carrinho somando todos os itens
     */
    public BigDecimal calcularValorTotal() {
        return itens.stream()
                .map(item -> item.getPrecoUnitario().multiply(new BigDecimal(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Retorna a quantidade total de itens no carrinho
     */
    public Integer getTotalItens() {
        return itens.stream()
                .mapToInt(ItemCarrinho::getQuantidade)
                .sum();
    }

    /**
     * Adiciona um item ao carrinho
     */
    public void adicionarItem(ItemCarrinho item) {
        item.setCarrinho(this);
        this.itens.add(item);
    }

    /**
     * Remove um item do carrinho
     */
    public void removerItem(ItemCarrinho item) {
        this.itens.remove(item);
        item.setCarrinho(null);
    }

    /**
     * Limpa todos os itens do carrinho
     */
    public void limpar() {
        this.itens.clear();
    }
}
