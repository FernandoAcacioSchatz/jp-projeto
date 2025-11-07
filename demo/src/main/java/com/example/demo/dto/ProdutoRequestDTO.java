package com.example.demo.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProdutoRequestDTO(

        @NotBlank(message = "O nome é obrigatório") @Size(min = 3, max = 100) 
        String nome,

        @NotBlank(message = "A descrição é obrigatória") 
        String descricao,

        @NotNull(message = "O preço é obrigatório") @Positive(message = "O preço deve ser positivo") 
        BigDecimal preco,

        @NotNull(message = "O estoque é obrigatório") @PositiveOrZero(message = "O estoque não pode ser negativo") 
        Integer estoque,

        @NotNull(message = "O ID da categoria é obrigatório") 
        Integer idCategoria) {
}