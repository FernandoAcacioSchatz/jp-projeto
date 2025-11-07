package com.example.demo.dto;

import com.example.demo.model.TipoPagamento;

import jakarta.validation.constraints.NotNull;


public record CriarPedidoDTO(
    
    @NotNull(message = "O tipo de pagamento é obrigatório")
    TipoPagamento tipoPagamento,

    Integer idEnderecoEntrega, // Opcional - se não informado, usa o endereço principal

    /**
     * ID do cartão para pagamentos com CARTAO_CREDITO ou CARTAO_DEBITO
     * Obrigatório apenas se tipoPagamento for CARTAO_*
     * Ignorado se tipoPagamento for PIX
     */
    Integer idCartao
) {}
