package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.model.Pedido;
import com.example.demo.model.StatusPedido;

/**
 * DTO de resposta para pedido completo
 */
public record PedidoResponseDTO(
        Integer id,
        LocalDateTime dataPedido,
        Integer idCliente,
        String nomeCliente,
        StatusPedido status,
        List<ItemPedidoResponseDTO> itens,
        BigDecimal valorTotal
) {
    public PedidoResponseDTO(Pedido pedido) {
        this(
                pedido.getId(),
                pedido.getDataPedido(),
                pedido.getCliente().getIdCliente(),
                pedido.getCliente().getNomeCliente(),
                pedido.getStatus(),
                pedido.getItens().stream()
                        .map(ItemPedidoResponseDTO::new)
                        .collect(Collectors.toList()),
                pedido.getValorTotal()
        );
    }
}
