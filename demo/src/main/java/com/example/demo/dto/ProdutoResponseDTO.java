package com.example.demo.dto;

import com.example.demo.model.Produto;
import java.math.BigDecimal;


public record ProdutoResponseDTO(

        Integer id,
        String nome,
        String descricao,
        BigDecimal preco,
        Integer estoque,
        String nomeCategoria,
        String nomeFornecedor) {

    public ProdutoResponseDTO(Produto produto) {
        this(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getEstoque(),
                produto.getCategoria().getNome(), 
                produto.getFornecedor().getNome() 
        );
    }
}