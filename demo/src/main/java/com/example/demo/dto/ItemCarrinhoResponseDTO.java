package com.example.demo.dto;

import java.math.BigDecimal;

import com.example.demo.model.ItemCarrinho;


public record ItemCarrinhoResponseDTO(
        Integer id,
        Integer idProduto,
        String nomeProduto,
        BigDecimal precoUnitario,
        Integer quantidade,
        BigDecimal subtotal
) {
    public ItemCarrinhoResponseDTO(ItemCarrinho item) {
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
