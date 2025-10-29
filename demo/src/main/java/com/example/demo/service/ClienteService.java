package com.example.demo.service;

import java.util.List;
import java.util.NoSuchElementException;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.demo.dto.ClienteRequestDTO;
import com.example.demo.dto.ClienteResponseDTO;
import com.example.demo.exception.CpfException;
import com.example.demo.exception.EmailException;
import com.example.demo.exception.RegraNegocioException;
import com.example.demo.exception.RoleNotFoundException;
import com.example.demo.model.Cliente;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.CpfValidator;

@Service
public class ClienteService {

    private final ClienteRepository cRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(ClienteRepository cRepository, UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.cRepository = cRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    
    public Cliente findById(Integer idCliente) {
        Cliente clientes = cRepository
                .findById(idCliente)
                .orElseThrow(
                        () -> new NoSuchElementException(
                                "Cliente " + idCliente + " não encontrado! Tipo: " + Cliente.class.getName()));
        return clientes;
    }

    public List<ClienteResponseDTO> listarTodosClientes() {

        List<Cliente> clientes = cRepository.findAll();

        return clientes.stream()
                .map(cliente -> new ClienteResponseDTO(cliente))
                .collect(Collectors.toList());
    }

    /**
     * Lista todos os clientes com paginação
     */
    public Page<ClienteResponseDTO> listarTodosClientesPaginado(Pageable pageable) {

        Page<Cliente> clientes = cRepository.findAll(pageable);

        return clientes.map(cliente -> new ClienteResponseDTO(cliente));
    }

    public Cliente inserirCliente(ClienteRequestDTO dto) {

        if (cRepository.findByUser_Email(dto.email()).isPresent()) {
            throw new EmailException("Email já cadastrado no sistema.");
        }

        // Remove formatação do CPF para validação e armazenamento
        String cpfLimpo = CpfValidator.removeFormat(dto.cpf());

        // Valida o CPF (dígitos verificadores)
        if (!CpfValidator.isValid(cpfLimpo)) {
            throw new CpfException("CPF inválido. Verifique os dígitos informados.");
        }

        if (cRepository.findByCpf(cpfLimpo).isPresent()) {
            throw new CpfException("CPF já cadastrado no sistema.");
        }

        Role roleCliente = roleRepository.findByNomePapel("ROLE_CLIENTE")
                .orElseThrow(() -> RoleNotFoundException.forRole("ROLE_CLIENTE"));

        User novoUser = new User();
        novoUser.setEmail(dto.email());
        novoUser.setSenha(passwordEncoder.encode(dto.senha()));
        novoUser.setRoles(Set.of(roleCliente));

        Cliente novoCliente = new Cliente();
        novoCliente.setNomeCliente(dto.nomeCliente());
        novoCliente.setCpf(cpfLimpo); // Armazena CPF sem formatação
        novoCliente.setTelefone(dto.telefone());
        novoCliente.setUser(novoUser);

        try {
            return cRepository.save(novoCliente);
        } catch (DataIntegrityViolationException e) {
            // "Cinto de segurança" para erros de concorrência ou outras constraints
            throw new RegraNegocioException("Erro de integridade ao salvar cliente: " + e.getMessage(), e);
        }
    }

    public Cliente alterarCliente(ClienteRequestDTO dto, Integer idCliente) {

        Cliente clienteExistente = this.findById(idCliente);

        User userExiste = clienteExistente.getUser();
        if (userExiste == null) {
            throw new RegraNegocioException("Usuário associado ao cliente não encontrado.");
        }
        if (dto.email() != null && !dto.email().equals(userExiste.getEmail())) {
            Optional<User> outroUserPorEmail = userRepository.findByEmail(dto.email());
            if (outroUserPorEmail.isPresent() && !outroUserPorEmail.get().getId().equals(userExiste.getId())) {
                throw new EmailException("Email já cadastrado no sistema.");
            }
            userExiste.setEmail(dto.email());
        }

        if (dto.nomeCliente() != null) {
            clienteExistente.setNomeCliente(dto.nomeCliente());
        }

        if (dto.telefone() != null) {
            clienteExistente.setTelefone(dto.telefone());
        }

        return cRepository.save(clienteExistente);

    }

    public void alterarSenha(String novaSenha, Integer idCliente) {

        Cliente cliente = this.findById(idCliente);

        User user = cliente.getUser();
        if (user == null) {
            throw new RegraNegocioException("Usuário associado ao cliente não encontrado.");
        }

        user.setSenha(passwordEncoder.encode(novaSenha));

        userRepository.save(user);

    }

    /**
     * Altera a senha do cliente validando a senha atual
     */
    public void alterarSenhaComValidacao(String senhaAtual, String novaSenha, Integer idCliente) {

        Cliente cliente = this.findById(idCliente);

        User user = cliente.getUser();
        if (user == null) {
            throw new RegraNegocioException("Usuário associado ao cliente não encontrado.");
        }

        // Valida a senha atual
        if (!passwordEncoder.matches(senhaAtual, user.getSenha())) {
            throw new RegraNegocioException("Senha atual incorreta.");
        }

        // Atualiza para a nova senha
        user.setSenha(passwordEncoder.encode(novaSenha));

        userRepository.save(user);
    }

    public void deletarCliente(Integer idCliente) {

        Cliente clienteParaDeletar = this.findById(idCliente);

        // Soft delete - apenas marca como deletado
        clienteParaDeletar.markAsDeleted();
        cRepository.save(clienteParaDeletar);
        
        // Para hard delete (exclusão física), use:
        // cRepository.delete(clienteParaDeletar);
    }

}
