package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.ClienteRequestDTO;
import com.example.demo.model.Cliente;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;

@SpringBootTest
@Transactional
@DisplayName("Testes de Integração do ClienteService")
public class ClienteServiceTest {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Deve inserir cliente com sucesso quando dados são válidos")
    void testInserirClienteComSucesso() {
        // Arrange
        Role roleCliente = Role.builder().nomePapel("ROLE_CLIENTE").build();
        roleRepository.save(roleCliente);

        ClienteRequestDTO clienteRequestDTO = new ClienteRequestDTO(
            "João Silva",
            "senha123",
            "joao.silva@email.com",
            "(11) 99999-9999",
            "123.456.789-09"
        );

        // Act
        Cliente clienteSalvo = clienteService.inserirCliente(clienteRequestDTO);

        // Assert
        assertNotNull(clienteSalvo.getIdCliente());
        assertEquals("João Silva", clienteSalvo.getNomeCliente());
        assertEquals("12345678909", clienteSalvo.getCpf());

        User userSalvo = userRepository.findByEmail("joao.silva@email.com").get();
        assertNotNull(userSalvo);
        assertTrue(passwordEncoder.matches("senha123", userSalvo.getSenha()));
        assertTrue(userSalvo.getRoles().stream().anyMatch(role -> role.getNomePapel().equals("ROLE_CLIENTE")));
    }
}
