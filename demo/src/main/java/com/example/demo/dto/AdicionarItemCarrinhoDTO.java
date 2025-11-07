package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public record AdicionarItemCarrinhoDTO(
        
        @NotNull(message = "O ID do produto é obrigatório")
        Integer idProduto,

        @NotNull(message = "A quantidade é obrigatória")
        @Positive(message = "A quantidade deve ser maior que zero")
        Integer quantidade
) {
}
