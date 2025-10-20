package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRequestDTO {

    @NotBlank(message = "O nome é Obrigatório")
    private String nomeUsuario;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8)
    private String senha;

    @Email(message = "O formato do email é invalido")
    private String email;

    @NotBlank(message = "O telefone é Obrigatório")
    private String telefone;

    @NotBlank(message = "O CPF é Obrigatório")
    @Size(min = 11, max = 14)
    private String cpf;

}
