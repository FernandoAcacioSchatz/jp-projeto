package com.example.demo.service;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UsuarioRequestDTO;
import com.example.demo.exception.CpfException;
import com.example.demo.exception.EmailException;
import com.example.demo.exception.RegraNegocioException;
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

    public Usuario inserirUsuario(UsuarioRequestDTO dto) {

        // 1. Validação de Regra de Negócio
        // (Use 'existsBy' para melhor performance)
        if (uRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailException("Email já cadastrado no sistema.");
        }

       
        if (uRepository.findByCpf(dto.getCpf()).isPresent()) {
            throw new CpfException("CPF já cadastrado");
        }

        // 2. Conversão de DTO para Entidade
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNomeUsuario(dto.getNomeUsuario());
        novoUsuario.setEmail(dto.getEmail());
        novoUsuario.setCpf(dto.getCpf());
        novoUsuario.setTelefone(dto.getTelefone());
        // Lembre-se de CRIPTOGRAFAR a senha antes de salvar
        // novoUsuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        novoUsuario.setSenha(dto.getSenha()); // (Versão simplificada)

        // 3. Salvar no Banco com try-catch
        try {
            return uRepository.save(novoUsuario);
        } catch (DataIntegrityViolationException e) {
            // "Cinto de segurança" para erros de concorrência ou outras constraints
            throw new RegraNegocioException("Erro de integridade ao salvar usuário: " + e.getMessage(), e);
        }
    }

    public Usuario alterarUsuario(UsuarioRequestDTO dto, Integer idUsuario) {

        // 1. Busca o usuário existente
        Usuario usuario = this.findById(idUsuario); // Reutiliza seu método de busca

        // 2. Atualiza os dados com base no DTO
        // Note que não atualizamos a senha aqui. Isso deve ser um método separado.
        usuario.setNomeUsuario(dto.getNomeUsuario());
        usuario.setEmail(dto.getEmail()); // (Cuidado: precisa validar se o novo email já existe)
        usuario.setTelefone(dto.getTelefone());
        usuario.setCpf(dto.getCpf()); // (Cuidado: precisa validar se o novo CPF já existe)

        // 3. Salva as alterações
        // Como o 'usuario' já tem um ID, o save() fará um UPDATE
        return uRepository.save(usuario);
    }

    /**
     * MÉTODO ALTERARSENHA CORRIGIDO (Forma Simplificada)
     * O ideal é ter um DTO específico (ex: AlterarSenhaDTO)
     * que peça a senha antiga e a nova.
     * Receber a entidade 'Usuario' aqui é perigoso.
     */
    public Usuario alterarSenha(String novaSenha, Integer idUsuario) {

        Usuario usuario = this.findById(idUsuario);

        // Lembre-se de CRIPTOGRAFAR a nova senha
        // usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuario.setSenha(novaSenha); // (Versão simplificada)

        return uRepository.save(usuario);
    }

}
