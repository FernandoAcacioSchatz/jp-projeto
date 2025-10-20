package com.example.demo.dto;

import com.example.demo.model.Usuario;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UsuarioResponseDTO {

    private Integer idUsuario;

    private String nomeUsuario;

    private String email;

    private String telefone;

    public UsuarioResponseDTO(Usuario usuario) {
        this.idUsuario = usuario.getIdUsuario();
        this.nomeUsuario = usuario.getNomeUsuario();
        this.email = usuario.getEmail();
        this.telefone = usuario.getTelefone();
    }
}
