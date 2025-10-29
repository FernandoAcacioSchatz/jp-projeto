package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.AdicionarItemCarrinhoDTO;
import com.example.demo.dto.CarrinhoResponseDTO;
import com.example.demo.model.Cliente;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.service.CarrinhoService;

import jakarta.validation.Valid;

/**
 * Controller para gerenciamento do carrinho de compras
 * Apenas clientes autenticados podem acessar
 */
@RestController
@RequestMapping("/carrinho")
public class CarrinhoController {

    @Autowired
    private CarrinhoService carrinhoService;

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping
    public ResponseEntity<CarrinhoResponseDTO> obterCarrinho(Authentication authentication) {

        Integer idCliente = obterIdClienteLogado(authentication);

        CarrinhoResponseDTO carrinho = carrinhoService.obterCarrinho(idCliente);

        return ResponseEntity.ok(carrinho);
    }

    @PostMapping("/item")
    public ResponseEntity<CarrinhoResponseDTO> adicionarItem(
            @Valid @RequestBody AdicionarItemCarrinhoDTO dto,
            Authentication authentication) {

        Integer idCliente = obterIdClienteLogado(authentication);

        CarrinhoResponseDTO carrinho = carrinhoService.adicionarItem(idCliente, dto);

        return ResponseEntity.ok(carrinho);
    }

    @PatchMapping("/item/{idItem}")
    public ResponseEntity<CarrinhoResponseDTO> atualizarQuantidade(
            @PathVariable Integer idItem,
            @RequestParam Integer quantidade,
            Authentication authentication) {

        Integer idCliente = obterIdClienteLogado(authentication);

        CarrinhoResponseDTO carrinho = carrinhoService.atualizarQuantidadeItem(idCliente, idItem, quantidade);

        return ResponseEntity.ok(carrinho);
    }

    @DeleteMapping("/item/{idItem}")
    public ResponseEntity<CarrinhoResponseDTO> removerItem(
            @PathVariable Integer idItem,
            Authentication authentication) {

        Integer idCliente = obterIdClienteLogado(authentication);

        CarrinhoResponseDTO carrinho = carrinhoService.removerItem(idCliente, idItem);

        return ResponseEntity.ok(carrinho);
    }

    @DeleteMapping
    public ResponseEntity<Void> limparCarrinho(Authentication authentication) {

        Integer idCliente = obterIdClienteLogado(authentication);

        carrinhoService.limparCarrinho(idCliente);

        return ResponseEntity.noContent().build();
    }

    private Integer obterIdClienteLogado(Authentication authentication) {
        String email = authentication.getName();

        Cliente cliente = clienteRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado para o usuário logado."));

        return cliente.getIdCliente();
    }
}
