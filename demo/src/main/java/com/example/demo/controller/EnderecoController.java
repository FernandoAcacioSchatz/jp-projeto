package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.EnderecoRequestDTO;
import com.example.demo.dto.EnderecoResponseDTO;
import com.example.demo.model.Cliente;
import com.example.demo.model.Endereco;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.service.EnderecoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/endereco")
@PreAuthorize("hasRole('CLIENTE')")
public class EnderecoController {

    private final EnderecoService enderecoService;
    private final ClienteRepository clienteRepository;

    public EnderecoController(EnderecoService enderecoService, ClienteRepository clienteRepository) {
        this.enderecoService = enderecoService;
        this.clienteRepository = clienteRepository;
    }

    private Integer obterIdClienteLogado(Authentication authentication) {
        String email = authentication.getName();
        Cliente cliente = clienteRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado para o usuário logado."));
        return cliente.getIdCliente();
    }

    @GetMapping
    public ResponseEntity<List<EnderecoResponseDTO>> listarMeusEnderecos(Authentication authentication) {
        Integer idCliente = obterIdClienteLogado(authentication);
        List<EnderecoResponseDTO> enderecos = enderecoService.listarEnderecosPorCliente(idCliente);
        return ResponseEntity.ok(enderecos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnderecoResponseDTO> buscarPorId(@PathVariable Integer id, Authentication authentication) {
        Integer idCliente = obterIdClienteLogado(authentication);
        Endereco endereco = enderecoService.findById(id);
        
        if (!endereco.getCliente().getIdCliente().equals(idCliente)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(new EnderecoResponseDTO(endereco));
    }

    @PostMapping
    public ResponseEntity<EnderecoResponseDTO> criar(@Valid @RequestBody EnderecoRequestDTO dto, Authentication authentication) {
        Integer idCliente = obterIdClienteLogado(authentication);
        Endereco novoEndereco = enderecoService.inserirEndereco(dto, idCliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(new EnderecoResponseDTO(novoEndereco));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnderecoResponseDTO> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody EnderecoRequestDTO dto,
            Authentication authentication) {
        Integer idCliente = obterIdClienteLogado(authentication);
        Endereco enderecoAtualizado = enderecoService.alterarEndereco(dto, id, idCliente);
        return ResponseEntity.ok(new EnderecoResponseDTO(enderecoAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id, Authentication authentication) {
        Integer idCliente = obterIdClienteLogado(authentication);
        enderecoService.deletarEndereco(id, idCliente);
        return ResponseEntity.noContent().build();
    }
}
