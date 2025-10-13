package com.example.demo.service;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.exception.EmailException;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository uRepository;

    public Usuario findById(Integer idUsuario) {
        Usuario usuarios = uRepository
                .findById(idUsuario)
                .orElseThrow(
                        () -> new NoSuchElementException(
                                "Usuario " + idUsuario + " não encontrado! Tipo: " + Usuario.class.getName()));
        return usuarios;
    }

    public Usuario inserirUsuario(Usuario usuarios) {

        usuarios.setIdUsuario(null);

        if (uRepository.findByEmail(usuarios.getEmail()).isPresent()) {
            throw new EmailException("Email ja cadastrado no sistema.");
        }
        return uRepository.save(usuarios);
    }

    public Usuario alterarUsuario(Usuario usuarios, Integer idUsuario) {
        Usuario usuario = uRepository
                .findById(idUsuario)
                .orElseThrow(
                        () -> new NoSuchElementException(
                                "Usuario " + idUsuario + " não encontrado! Tipo: " + Usuario.class.getName()));

        usuario.setNomeUsuario(usuarios.getNomeUsuario());
        usuario.setEmail(usuarios.getEmail());
        usuario.setTelefone(usuarios.getTelefone());
        usuario.setCpf(usuarios.getCpf());
        return uRepository.save(usuario);
    }

    public Usuario alterarSenha(Usuario usuarios, Integer idUsuario) {
        Usuario usuario = uRepository
                .findById(idUsuario)
                .orElseThrow(
                        () -> new NoSuchElementException(
                                "Usuario " + idUsuario + " não encontrado! Tipo: " + Usuario.class.getName()));

        usuario.setSenha(usuarios.getSenha());
        return uRepository.save(usuario);
    }

    

}
