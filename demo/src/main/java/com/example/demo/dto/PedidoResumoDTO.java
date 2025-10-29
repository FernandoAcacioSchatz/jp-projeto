package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.demo.model.Pedido;
import com.example.demo.model.StatusPedido;

/**
 * DTO de resposta resumida para listagem de pedidos
 */
public record PedidoResumoDTO(
        Integer id,
        LocalDateTime dataPedido,
        StatusPedido status,
        BigDecimal valorTotal,
        Integer totalItens
) {
    public PedidoResumoDTO(Pedido pedido) {
        this(
                pedido.getId(),
                pedido.getDataPedido(),
                pedido.getStatus(),
                pedido.getValorTotal(),
                pedido.getItens().stream()
                        .mapToInt(item -> item.getQuantidade())
                        .sum()
        );
    }
}
