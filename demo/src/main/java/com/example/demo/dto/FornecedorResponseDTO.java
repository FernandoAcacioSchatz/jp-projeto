package com.example.demo.dto;

import com.example.demo.model.Fornecedor;

/**
 * DTO (Data Transfer Object) para retornar dados do Fornecedor de forma segura.
 * Convertido para 'record' para um código mais limpo e imutável.
 */
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