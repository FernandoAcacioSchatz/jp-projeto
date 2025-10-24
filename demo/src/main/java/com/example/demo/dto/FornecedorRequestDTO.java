package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FornecedorRequestDTO {

    @NotBlank(message = "O nome é Obrigatório")
    private String nome;

    @NotBlank(message = "O email é Obrigatório")
    private String email;

    @NotBlank(message = "A senha é Obrigatória")
    private String senha;

    @NotBlank(message = "O CNPJ é Obrigatório")
    @Size(min = 12, max = 18)
    private String cnpj;

    @NotBlank(message = "O telefone é Obrigatório")
    private String telefone;

}
