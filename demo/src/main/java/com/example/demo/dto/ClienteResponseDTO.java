package com.example.demo.dto;

import com.example.demo.model.Cliente;

public record ClienteResponseDTO(
        Integer idCliente,
        String nomeCliente,
        String email,
        String telefone) {

    public ClienteResponseDTO(Cliente cliente) {
        this(
                cliente.getIdCliente(),
                cliente.getNomeCliente(),
                cliente.getUser().getEmail(), // Sua lógica de mapeamento é preservada
                cliente.getTelefone());
    }
}