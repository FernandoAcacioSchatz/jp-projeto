package com.example.demo.dto;

import com.example.demo.model.EstadosBrasileiros;
import com.example.demo.validation.ValidCnpj;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record FornecedorRequestDTO(
    
    @NotBlank(message = "O nome é Obrigatório")
    String nome,

    @NotBlank(message = "O email é Obrigatório")
    @Email(message = "Formato de email inválido")
    String email,

    @NotBlank(message = "A senha é Obrigatória")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
    String senha,

    @NotBlank(message = "O CNPJ é Obrigatório")
    @Size(min = 14, max = 18, message = "O CNPJ deve ter 14 dígitos")
    @ValidCnpj(message = "CNPJ inválido. Verifique os dígitos informados.")
    String cnpj,

    @NotBlank(message = "O telefone é Obrigatório")
    String telefone,

    @NotNull(message = "O estado é Obrigatório")
    EstadosBrasileiros estado
) {
}