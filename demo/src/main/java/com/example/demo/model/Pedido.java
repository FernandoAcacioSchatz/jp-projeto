package com.example.demo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "data_pedido", nullable = false)
    private LocalDateTime dataPedido;

    @Column(name = "valor_total", nullable = false)
    private BigDecimal valorTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

}

/*
 * 
 * 
 * 
 * @ManyToOne(fetch = FetchType.LAZY)
 * 
 * @JoinColumn(name = "id_usuario", nullable = false)
 * private Usuario usuario;
 * 
 * @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
 * private Set<ItemPedido> itens = new HashSet<>();
 * 
 * @Column(name = "data_pedido", nullable = false)
 * private LocalDateTime dataPedido;
 * 
 * @Column(name = "valor_total", nullable = false)
 * private BigDecimal valorTotal;
 * 
 * @Enumerated(EnumType.STRING) // Salva o nome do enum ("PAGO") em vez de um
 * número (1)
 * 
 * @Column(nullable = false)
 * private StatusPedido status;
 * 
 * // Construtor
 * public Pedido() {
 * this.dataPedido = LocalDateTime.now();
 * this.status = StatusPedido.PENDENTE; // Status inicial
 * }
 * 
 * // Getters e Setters...
 * 
 * // Método útil para adicionar itens
 * public void adicionarItem(ItemPedido item) {
 * this.itens.add(item);
 * item.setPedido(this);
 * }
 * }
 */