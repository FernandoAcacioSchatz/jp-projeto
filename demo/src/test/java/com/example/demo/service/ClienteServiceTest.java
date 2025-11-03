package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.dto.ClienteRequestDTO;
import com.example.demo.dto.ClienteResponseDTO;
import com.example.demo.exception.CpfException;
import com.example.demo.exception.EmailException;
import com.example.demo.model.Cliente;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ClienteService")
public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClienteService clienteService;

    private ClienteRequestDTO clienteRequestDTO;
    private Cliente cliente;
    private User user;
    private Role roleCliente;
    private String senhaEncriptada;

    @BeforeEach
    void setUp() {
        // Preparar dados de teste
                clienteRequestDTO = new ClienteRequestDTO(
            "João Silva",
            "senha123",
            "joao@email.com",
            "(11) 99999-9999",
            "123.456.789-09" // CPF válido com dígitos verificadores
        );

        senhaEncriptada = "senha_encriptada_123";

        user = User.builder()
                .email("joao@email.com")
                .senha(senhaEncriptada)
                .build();

        roleCliente = Role.builder()
                .nomePapel("ROLE_CLIENTE")
                .build();

        Set<Role> roles = new HashSet<>();
        roles.add(roleCliente);
        user.setRoles(roles);

        cliente = Cliente.builder()
            .idCliente(1)
            .nomeCliente("João Silva")
            .cpf("12345678909") // CPF válido: 123.456.789-09
            .telefone("(11) 99999-9999")
            .user(user)
            .build();
    }

    @Test
    @DisplayName("Deve inserir cliente com sucesso quando dados são válidos")
    void testInserirClienteComSucesso() {
        // Arrange
        when(clienteRepository.findByUser_Email("joao@email.com")).thenReturn(Optional.empty());
        when(clienteRepository.findByCpf("12345678909")).thenReturn(Optional.empty());
        when(roleRepository.findByNomePapel("ROLE_CLIENTE")).thenReturn(Optional.of(roleCliente));
        when(passwordEncoder.encode("senha123")).thenReturn(senhaEncriptada);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        // Act
        Cliente clienteSalvo = clienteService.inserirCliente(clienteRequestDTO);

        // Assert
        assertNotNull(clienteSalvo);
        assertEquals("João Silva", clienteSalvo.getNomeCliente());
        assertEquals("12345678909", clienteSalvo.getCpf());
        assertEquals(senhaEncriptada, clienteSalvo.getUser().getSenha());
        assertTrue(clienteSalvo.getUser().getRoles().contains(roleCliente));

        verify(passwordEncoder).encode("senha123");
        verify(clienteRepository).save(any(Cliente.class));
        verify(roleRepository).findByNomePapel("ROLE_CLIENTE");
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já existe")
    void testInserirClienteEmailJaExiste() {
        // Arrange
        when(clienteRepository.findByUser_Email("joao@email.com"))
                .thenReturn(Optional.of(cliente));

        // Act & Assert
        assertThrows(EmailException.class, () -> {
            clienteService.inserirCliente(clienteRequestDTO);
        });

        verify(clienteRepository).findByUser_Email("joao@email.com");
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF já existe")
    void testInserirClienteCPFJaExiste() {
        // Arrange
        when(clienteRepository.findByUser_Email("joao@email.com"))
                .thenReturn(Optional.empty());
        when(clienteRepository.findByCpf("12345678909"))
                .thenReturn(Optional.of(cliente));

        // Act & Assert
        assertThrows(CpfException.class, () -> {
            clienteService.inserirCliente(clienteRequestDTO);
        });

        verify(clienteRepository).findByCpf("12345678909");
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF é inválido")
    void testInserirClienteCPFInvalido() {
        // Arrange
        when(clienteRepository.findByUser_Email("joao@email.com"))
                .thenReturn(Optional.empty());

        clienteRequestDTO = new ClienteRequestDTO(
                "João Silva",
                "senha123",
                "joao@email.com",
                "(11) 99999-9999",
                "123.456" // CPF inválido
        );

        // Act & Assert
        assertThrows(CpfException.class, () -> {
            clienteService.inserirCliente(clienteRequestDTO);
        });

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve listar todos os clientes com sucesso")
    void testListarTodosClientes() {
        // Arrange
        List<Cliente> clientes = Arrays.asList(cliente);
        when(clienteRepository.findAll()).thenReturn(clientes);

        // Act
        List<ClienteResponseDTO> resultado = clienteService.listarTodosClientes();

        // Assert
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());

        // Verificar os dados no DTO de resposta
        ClienteResponseDTO dto = resultado.get(0);
        assertEquals(cliente.getNomeCliente(), dto.nomeCliente());
        assertEquals(cliente.getTelefone(), dto.telefone());
        assertEquals(cliente.getUser().getEmail(), dto.email());

        // Verificar CPF diretamente no objeto Cliente retornado pelo repositório
        Cliente clienteRetornado = clientes.get(0);
        assertEquals("12345678909", clienteRetornado.getCpf());

        verify(clienteRepository).findAll();
    }

    @Test
    @DisplayName("Deve encontrar cliente por ID com sucesso")
    void testFindByIdComSucesso() {
        // Arrange
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));

        // Act
        Cliente clienteEncontrado = clienteService.findById(1);

        // Assert
        assertNotNull(clienteEncontrado);
        assertEquals(cliente.getIdCliente(), clienteEncontrado.getIdCliente());
        assertEquals(cliente.getNomeCliente(), clienteEncontrado.getNomeCliente());
        assertEquals(cliente.getCpf(), clienteEncontrado.getCpf());
        assertEquals(cliente.getUser().getEmail(), clienteEncontrado.getUser().getEmail());

        verify(clienteRepository).findById(1);
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente não encontrado por ID")
    void testFindByIdClienteNaoEncontrado() {
        // Arrange
        when(clienteRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(java.util.NoSuchElementException.class, () -> {
            clienteService.findById(999);
        });

        verify(clienteRepository).findById(999);
    }
}