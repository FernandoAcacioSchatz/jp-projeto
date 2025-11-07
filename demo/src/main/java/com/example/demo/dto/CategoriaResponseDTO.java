package com.example.demo.dto;

import com.example.demo.model.Categoria;


public record CategoriaResponseDTO(
    Integer id,
    String nome,
    String descricao,
    Integer quantidadeProdutos
) {
    public CategoriaResponseDTO(Categoria categoria) {
        this(
            categoria.getId(),
            categoria.getNome(),
            categoria.getDescricao(),
            categoria.getQuantidadeProdutos()
        );
    }
}
