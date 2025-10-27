package com.example.demo.dto;

import jakarta.validation.constraints.Email; // É uma boa prática adicionar validação de email
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record FornecedorRequestDTO(
    
    @NotBlank(message = "O nome é Obrigatório")
    String nome,

    @NotBlank(message = "O email é Obrigatório")
    @Email(message = "Formato de email inválido") // Recomendado
    String email,

    @NotBlank(message = "A senha é Obrigatória")
    @Size(min = 8) // Recomendo definir um tamanho mínimo para a senha
    String senha,

    @NotBlank(message = "O CNPJ é Obrigatório")
    @Size(min = 14, max = 18) // O seu size original de min 12 estava um pouco curto para CNPJ (14.123.123/0001-12)
    String cnpj,

    @NotBlank(message = "O telefone é Obrigatório")
    String telefone
) {
}