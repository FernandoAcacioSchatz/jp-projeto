package com.example.demo.service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.FornecedorRequestDTO;
import com.example.demo.exception.CnpjException;
import com.example.demo.exception.EmailException;
import com.example.demo.exception.RegraNegocioException;
import com.example.demo.model.Cliente;
import com.example.demo.model.Fornecedor;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.FornecedorRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;

@Service
public class FornecedorService {

    private final ClienteService clienteService;

    private final FornecedorRepository fRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public FornecedorService(FornecedorRepository fRepository, UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder, ClienteService clienteService) {
        this.fRepository = fRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.clienteService = clienteService;
    }

    public Fornecedor findById(Integer idFornecedor) {
        Fornecedor fornecedores = fRepository.findById(idFornecedor)
                .orElseThrow(
                        () -> new NoSuchElementException(
                                "Fornecedor " + idFornecedor + " não encontrado! Tipo: " + Fornecedor.class.getName()));
        return fornecedores;
    }

    public Fornecedor inserirFornecedor(FornecedorRequestDTO dto) {

        if (fRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailException("Email já cadastrado no sistema.");
        }

        if (fRepository.findByCnpj(dto.getCnpj()).isPresent()) {
            throw new CnpjException("CNPJ já cadastrado");
        }

        Role roleFornecedor = roleRepository.findByNomePapel("ROLE_FORNECEDOR")
                .orElseThrow(() -> new RuntimeException("Role ROLE_FORNECEDOR não encontrada no sistema."));

        User novoUser = new User();
        novoUser.setEmail(dto.getEmail());
        novoUser.setSenha(passwordEncoder.encode(dto.getSenha()));
        novoUser.setRoles(Set.of(roleFornecedor));

        Fornecedor novoFornecedor = new Fornecedor();
        novoFornecedor.setNome(dto.getNome());
        novoFornecedor.setCnpj(dto.getCnpj());
        novoFornecedor.setTelefone(dto.getTelefone());

        try {
            return fRepository.save(novoFornecedor);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Erro de integridade ao salvar o fornecedor: " + e.getMessage());
        }
    }

    public Fornecedor alteraFornecedor(FornecedorRequestDTO dto, Integer idFornecedor) {

        Fornecedor fornecedorExistente = this.findById(idFornecedor);

        User userExiste = fornecedorExistente.getUser();
        if (userExiste == null) {
            throw new RuntimeException("Usuário associado ao fornecedor não encontrado.");
        }

        if (dto.getEmail() != null && !dto.getEmail().equals(userExiste.getEmail())) {
            Optional<User> outroUserPorEmail = userRepository.findByEmail(dto.getEmail());
            if (outroUserPorEmail.isPresent() && !outroUserPorEmail.get().getId().equals(userExiste.getId())) {
                throw new EmailException("Email já cadastrado no sistema.");
            }
            userExiste.setEmail(dto.getEmail());
        }
        if (dto.getNome() != null) {
            fornecedorExistente.setNome(dto.getNome());
        }

        if (dto.getTelefone() != null) {
            fornecedorExistente.setTelefone(dto.getTelefone());
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

    public void deletarFornecedor(Integer idFornecedor) {

        Fornecedor fornecedorParaDeletar = this.findById(idFornecedor);

        fRepository.delete(fornecedorParaDeletar);
    }

}
