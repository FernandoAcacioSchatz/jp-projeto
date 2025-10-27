package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteRequestDTO(
    
    @NotBlank(message = "O nome é Obrigatório")
    String nomeCliente,

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8)
    String senha,

    @NotBlank(message = "O email é obrigatório") // Adicionado, pois o email não deve ser vazio
    @Email(message = "O formato do email é invalido")
    String email,

    @NotBlank(message = "O telefone é Obrigatório")
    String telefone,

    @NotBlank(message = "O CPF é Obrigatório")
    @Size(min = 11, max = 14)
    String cpf
) {}