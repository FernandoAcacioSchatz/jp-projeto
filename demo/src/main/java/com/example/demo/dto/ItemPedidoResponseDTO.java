package com.example.demo.dto;

import java.math.BigDecimal;

import com.example.demo.model.ItemPedido;


public record ItemPedidoResponseDTO(
        Integer id,
        Integer idProduto,
        String nomeProduto,
        BigDecimal precoUnitario,
        Integer quantidade,
        BigDecimal subtotal,
        QRCodeResponseDTO qrCode
) {
    public ItemPedidoResponseDTO(ItemPedido item) {
        this(
                item.getId(),
                item.getProduto().getId(),
                item.getProduto().getNome(),
                item.getPrecoUnitario(),
                item.getQuantidade(),
                item.calcularSubtotal(),
                item.getQrCodeRastreamento() != null ? 
                    new QRCodeResponseDTO(item.getQrCodeRastreamento()) : null
        );
    }
}
