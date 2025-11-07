package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record CategoriaRequestDTO(
    
    @NotBlank(message = "O nome da categoria não pode estar vazio.")
    @Size(min = 3, max = 50, message = "O nome deve ter entre 3 e 50 caracteres.")
    String nome,

    @NotBlank(message = "A descrição da categoria não pode estar vazia.")
    @Size(min = 10, max = 255, message = "A descrição deve ter entre 10 e 255 caracteres.")
    String descricao
) {}
