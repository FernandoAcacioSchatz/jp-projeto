package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.model.Carrinho;

/**
 * DTO de resposta para o carrinho completo
 */
public record CarrinhoResponseDTO(
        Integer id,
        Integer idCliente,
        String nomeCliente,
        List<ItemCarrinhoResponseDTO> itens,
        Integer totalItens,
        BigDecimal valorTotal
) {
    public CarrinhoResponseDTO(Carrinho carrinho) {
        this(
                carrinho.getId(),
                carrinho.getCliente().getIdCliente(),
                carrinho.getCliente().getNomeCliente(),
                carrinho.getItens().stream()
                        .map(ItemCarrinhoResponseDTO::new)
                        .collect(Collectors.toList()),
                carrinho.getTotalItens(),
                carrinho.calcularValorTotal()
        );
    }
}
