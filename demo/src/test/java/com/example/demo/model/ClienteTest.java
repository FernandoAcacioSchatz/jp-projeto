package com.example.demo.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

@DisplayName("Testes da classe Cliente")
public class ClienteTest {

    private Validator validator;
    private Cliente cliente;
    private User user;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        // Criar User válido
        user = User.builder()
                .email("joao@email.com")
                .senha("senha123")
                .build();

        // Criar Cliente válido usando builder
        cliente = Cliente.builder()
                .nomeCliente("João Silva")
                .cpf("123.456.789-00")
                .telefone("(11) 99999-9999")
                .user(user)
                .build();
    }

    @Test
    @DisplayName("Deve criar um cliente com sucesso quando dados são válidos")
    void testCriarClienteValido() {
        // Act
        Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);

        // Assert
        assertTrue(violations.isEmpty());
        assertEquals("João Silva", cliente.getNomeCliente());
        assertEquals("123.456.789-00", cliente.getCpf());
        assertEquals("(11) 99999-9999", cliente.getTelefone());
        assertNotNull(cliente.getUser());
        assertEquals("joao@email.com", cliente.getUser().getEmail());
    }

    @Test
    @DisplayName("Deve falhar ao criar cliente sem nome")
    void testCriarClienteSemNome() {
        // Arrange
        cliente.setNomeCliente(null);

        // Act
        Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("O nome é Obrigatório",
                violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao criar cliente sem CPF")
    void testCriarClienteSemCPF() {
        // Arrange
        cliente.setCpf(null);

        // Act
        Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("O CPF é Obrigatório",
                violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao criar cliente sem usuário")
    void testCriarClienteSemUser() {
        // Arrange
        cliente.setUser(null);

        // Act
        Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("O usuário é obrigatório")));
    }

    @Test
    @DisplayName("Deve adicionar endereço com sucesso")
    void testAdicionarEndereco() {
        // Arrange
        Endereco endereco = Endereco.builder()
                .rua("Rua Teste")
                .numero("123")
                .bairro("Centro")
                .cidade("São Paulo")
                .estado(EstadosBrasileiros.SP)
                .cep("01234-567")
                .build();

        // Act
        cliente.getEnderecos().add(endereco);
        endereco.setCliente(cliente);

        // Assert
        assertEquals(1, cliente.getEnderecos().size());
        assertTrue(cliente.getEnderecos().contains(endereco));
        assertEquals(cliente, endereco.getCliente());
    }

    @Test
    @DisplayName("Deve remover endereço com sucesso")
    void testRemoverEndereco() {
        // Arrange
        Endereco endereco = Endereco.builder()
                .rua("Rua Teste")
                .numero("123")
                .bairro("Centro")
                .cidade("São Paulo")
                .estado(EstadosBrasileiros.SP)
                .cep("01234-567")
                .build();
        cliente.getEnderecos().add(endereco);
        endereco.setCliente(cliente);

        // Act
        cliente.getEnderecos().remove(endereco);

        // Assert
        assertEquals(0, cliente.getEnderecos().size());
        assertFalse(cliente.getEnderecos().contains(endereco));
    }

    @Test
    @DisplayName("Deve associar carrinho com sucesso")
    void testAssociarCarrinho() {
        // Arrange
        Carrinho carrinho = new Carrinho();

        // Act
        cliente.setCarrinho(carrinho);
        carrinho.setCliente(cliente);

        // Assert
        assertEquals(carrinho, cliente.getCarrinho());
        assertEquals(cliente, carrinho.getCliente());
    }

    @Test
    @DisplayName("Deve adicionar pedido com sucesso")
    void testAdicionarPedido() {
        // Arrange
        Pedido pedido = new Pedido();

        // Act
        cliente.getPedidos().add(pedido);
        pedido.setCliente(cliente);

        // Assert
        assertEquals(1, cliente.getPedidos().size());
        assertTrue(cliente.getPedidos().contains(pedido));
        assertEquals(cliente, pedido.getCliente());
    }
}