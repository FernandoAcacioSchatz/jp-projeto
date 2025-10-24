package com.example.demo.dto;

import com.example.demo.model.Cliente;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClienteResponseDTO {

    private Integer idCliente;

    private String nomeCliente;

    private String email;

    private String telefone;

    public ClienteResponseDTO(Cliente cliente) {
        this.idCliente = cliente.getIdCliente();
        this.nomeCliente = cliente.getNomeCliente();
        this.email = cliente.getUser().getEmail();
        this.telefone = cliente.getTelefone();
    }
}
