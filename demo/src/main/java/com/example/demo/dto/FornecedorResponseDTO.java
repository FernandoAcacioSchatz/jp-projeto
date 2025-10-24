package com.example.demo.dto;

import com.example.demo.model.Fornecedor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FornecedorResponseDTO {

    private Integer idFornecedor;

    private String nomeFornecedor;

    private String email;

    private String cnpj;

    public FornecedorResponseDTO(Fornecedor fornecedor) {
        this.idFornecedor = fornecedor.getId();
        this.nomeFornecedor = fornecedor.getNome();
        this.email = fornecedor.getUser().getEmail();
        this.cnpj = fornecedor.getCnpj();
    }
}
