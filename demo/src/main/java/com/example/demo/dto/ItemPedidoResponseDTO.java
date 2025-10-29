package com.example.demo.dto;

import java.math.BigDecimal;

import com.example.demo.model.ItemPedido;

/**
 * DTO de resposta para item do pedido
 */
public record ItemPedidoResponseDTO(
        Integer id,
        Integer idProduto,
        String nomeProduto,
        BigDecimal precoUnitario,
        Integer quantidade,
        BigDecimal subtotal
) {
    public ItemPedidoResponseDTO(ItemPedido item) {
        this(
                item.getId(),
                item.getProduto().getId(),
                item.getProduto().getNome(),
                item.getPrecoUnitario(),
                item.getQuantidade(),
                item.calcularSubtotal()
        );
    }
}
