package com.example.demo.dto;

import com.example.demo.model.Fornecedor;


public record FornecedorResponseDTO(
        Integer idFornecedor,
        String nomeFornecedor,
        String email,
        String cnpj) {

    public FornecedorResponseDTO(Fornecedor fornecedor) {
        this(
                fornecedor.getIdFornecedor(),
                fornecedor.getNome(),
                fornecedor.getUser().getEmail(),
                fornecedor.getCnpj());
    }
}