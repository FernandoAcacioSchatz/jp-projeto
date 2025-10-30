package com.example.demo.dto;

import com.example.demo.model.Endereco;
import com.example.demo.model.EstadosBrasileiros;

/**
 * DTO para resposta de Endereço
 */
public record EnderecoResponseDTO(
    Integer idEndereco,
    String apelido,
    String cep,
    String rua,
    String numero,
    String complemento,
    String bairro,
    String cidade,
    EstadosBrasileiros estado,
    Boolean isPrincipal,
    String enderecoCompleto
) {
    public EnderecoResponseDTO(Endereco endereco) {
        this(
            endereco.getIdEndereco(),
            endereco.getApelido(),
            endereco.getCep(),
            endereco.getRua(),
            endereco.getNumero(),
            endereco.getComplemento(),
            endereco.getBairro(),
            endereco.getCidade(),
            endereco.getEstado(),
            endereco.getIsPrincipal(),
            endereco.getEnderecoCompleto()
        );
    }
}
