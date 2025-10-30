package com.example.demo.dto;

import com.example.demo.model.EstadosBrasileiros;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para requisições de criação e atualização de Endereço
 */
public record EnderecoRequestDTO(
    
    @NotBlank(message = "O apelido do endereço é obrigatório (ex: Casa, Trabalho)")
    @Size(min = 3, max = 50, message = "O apelido deve ter entre 3 e 50 caracteres")
    String apelido,

    @NotBlank(message = "O CEP é obrigatório")
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP inválido. Use o formato: 12345-678")
    String cep,

    @NotBlank(message = "A rua é obrigatória")
    @Size(min = 3, max = 100, message = "A rua deve ter entre 3 e 100 caracteres")
    String rua,

    @NotBlank(message = "O número é obrigatório")
    @Size(max = 10, message = "O número deve ter no máximo 10 caracteres")
    String numero,

    @Size(max = 100, message = "O complemento deve ter no máximo 100 caracteres")
    String complemento,

    @NotBlank(message = "O bairro é obrigatório")
    @Size(min = 3, max = 50, message = "O bairro deve ter entre 3 e 50 caracteres")
    String bairro,

    @NotBlank(message = "A cidade é obrigatória")
    @Size(min = 3, max = 50, message = "A cidade deve ter entre 3 e 50 caracteres")
    String cidade,

    @NotNull(message = "O estado é obrigatório")
    EstadosBrasileiros estado,

    Boolean isPrincipal // Se não informado, será false
) {}
