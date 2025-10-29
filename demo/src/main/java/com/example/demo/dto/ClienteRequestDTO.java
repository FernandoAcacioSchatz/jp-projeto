package com.example.demo.dto;

import com.example.demo.validation.ValidCpf;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteRequestDTO(
    
    @NotBlank(message = "O nome é Obrigatório")
    String nomeCliente,

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
    String senha,

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "O formato do email é inválido")
    String email,

    @NotBlank(message = "O telefone é Obrigatório")
    String telefone,

    @NotBlank(message = "O CPF é Obrigatório")
    @Size(min = 11, max = 14, message = "O CPF deve ter 11 dígitos")
    @ValidCpf(message = "CPF inválido. Verifique os dígitos informados.")
    String cpf
) {}