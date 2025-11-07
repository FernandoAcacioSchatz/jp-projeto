package com.example.demo.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.FornecedorRequestDTO;
import com.example.demo.dto.FornecedorResponseDTO;
import com.example.demo.exception.CnpjException;
import com.example.demo.exception.EmailException;
import com.example.demo.exception.RegraNegocioException;
import com.example.demo.exception.RoleNotFoundException;
import com.example.demo.model.Fornecedor;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.FornecedorRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.CnpjValidator;

@Service("fornecedorService")
public class FornecedorService {

    private final FornecedorRepository fRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public FornecedorService(FornecedorRepository fRepository, UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.fRepository = fRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<FornecedorResponseDTO> listarTodosFornecedores() {

        List<Fornecedor> fornecedores = fRepository.findAll();

        return fornecedores.stream()
                .map(fornecedor -> new FornecedorResponseDTO(fornecedor))
                .collect(Collectors.toList());
    }

    /**
     * Lista todos os fornecedores com paginação
     */
    public Page<FornecedorResponseDTO> listarTodosFornecedoresPaginado(Pageable pageable) {

        Page<Fornecedor> fornecedores = fRepository.findAll(pageable);

        return fornecedores.map(fornecedor -> new FornecedorResponseDTO(fornecedor));
    }

    public Fornecedor findById(Integer idFornecedor) {
        Fornecedor fornecedores = fRepository.findById(idFornecedor)
                .orElseThrow(
                        () -> new NoSuchElementException(
                                "Fornecedor " + idFornecedor + " não encontrado! Tipo: " + Fornecedor.class.getName()));
        return fornecedores;
    }

    public Fornecedor inserirFornecedor(FornecedorRequestDTO dto) {

        if (fRepository.findByUser_Email(dto.email()).isPresent()) {
            throw new EmailException("Email já cadastrado no sistema.");
        }

        // Remove formatação do CNPJ para validação e armazenamento
        String cnpjLimpo = CnpjValidator.removeFormat(dto.cnpj());

        // Valida o CNPJ (dígitos verificadores)
        if (!CnpjValidator.isValid(cnpjLimpo)) {
            throw new CnpjException("CNPJ inválido. Verifique os dígitos informados.");
        }

        if (fRepository.findByCnpj(cnpjLimpo).isPresent()) {
            throw new CnpjException("CNPJ já cadastrado no sistema.");
        }

        Role roleFornecedor = roleRepository.findByNomePapel("ROLE_FORNECEDOR")
                .orElseThrow(() -> RoleNotFoundException.forRole("ROLE_FORNECEDOR"));

        User novoUser = new User();
        novoUser.setEmail(dto.email());
        novoUser.setSenha(passwordEncoder.encode(dto.senha()));
        novoUser.setRoles(Set.of(roleFornecedor));

        Fornecedor novoFornecedor = new Fornecedor();
        novoFornecedor.setNome(dto.nome());
        novoFornecedor.setCnpj(cnpjLimpo); // Armazena CNPJ sem formatação
        novoFornecedor.setTelefone(dto.telefone());
        novoFornecedor.setEstado(dto.estado());
        novoFornecedor.setUser(novoUser);

        try {
            return fRepository.save(novoFornecedor);
        } catch (DataIntegrityViolationException e) {
            throw new RegraNegocioException("Erro de integridade ao salvar o fornecedor: " + e.getMessage(), e);
        }
    }

    public Fornecedor alteraFornecedor(FornecedorRequestDTO dto, Integer idFornecedor) {

        Fornecedor fornecedorExistente = this.findById(idFornecedor);

        User userExiste = fornecedorExistente.getUser();
        if (userExiste == null) {
            throw new RegraNegocioException("Usuário associado ao fornecedor não encontrado.");
        }

        if (dto.email() != null && !dto.email().equals(userExiste.getEmail())) {
            Optional<User> outroUserPorEmail = userRepository.findByEmail(dto.email());
            if (outroUserPorEmail.isPresent() && !outroUserPorEmail.get().getId().equals(userExiste.getId())) {
                throw new EmailException("Email já cadastrado no sistema.");
            }
            userExiste.setEmail(dto.email());
        }
        if (dto.nome() != null) {
            fornecedorExistente.setNome(dto.nome());
        }

        if (dto.telefone() != null) {
            fornecedorExistente.setTelefone(dto.telefone());
        }

        return fRepository.save(fornecedorExistente);
    }

    public void alterarSenha(String novaSenha, Integer idFornecedor) {

        Fornecedor fornecedor = this.findById(idFornecedor);

        User user = fornecedor.getUser();
        if (user == null) {
            throw new RegraNegocioException("Usuário associado ao fornecedor não encontrado.");
        }

        user.setSenha(passwordEncoder.encode(novaSenha));

        userRepository.save(user);

    }

    /**
     * Altera a senha do fornecedor validando a senha atual
     */
    public void alterarSenhaComValidacao(String senhaAtual, String novaSenha, Integer idFornecedor) {

        Fornecedor fornecedor = this.findById(idFornecedor);

        User user = fornecedor.getUser();
        if (user == null) {
            throw new RegraNegocioException("Usuário associado ao fornecedor não encontrado.");
        }

        // Valida a senha atual
        if (!passwordEncoder.matches(senhaAtual, user.getSenha())) {
            throw new RegraNegocioException("Senha atual incorreta.");
        }

        // Atualiza para a nova senha
        user.setSenha(passwordEncoder.encode(novaSenha));

        userRepository.save(user);
    }

    public void deletarFornecedor(Integer idFornecedor) {

        Fornecedor fornecedorParaDeletar = this.findById(idFornecedor);

        // Soft delete - apenas marca como deletado
        fornecedorParaDeletar.markAsDeleted();
        fRepository.save(fornecedorParaDeletar);
        
        // Para hard delete (exclusão física), use:
        // fRepository.delete(fornecedorParaDeletar);
    }

    public boolean isOwner(Authentication auth, Integer idFornecedor) {
        // 1. Pega o email (username) do usuário logado no token
        String emailDoUsuarioLogado = auth.getName();

        // 2. Busca o fornecedor pelo ID que está sendo acessado
        Fornecedor fornecedor = findById(idFornecedor);

        // 3. Verifica se o usuário associado ao fornecedor existe
        User userDoFornecedor = fornecedor.getUser();
        if (userDoFornecedor == null) {
            // Se o fornecedor não tiver usuário, ninguém é dono
            return false;
        }

        // 4. Compara o email do usuário logado com o email do dono do fornecedor
        return emailDoUsuarioLogado.equals(userDoFornecedor.getEmail());
    }

}
