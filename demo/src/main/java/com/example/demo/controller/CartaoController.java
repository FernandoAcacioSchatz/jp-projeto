package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CartaoRequestDTO;
import com.example.demo.dto.CartaoResponseDTO;
import com.example.demo.dto.CartaoResumoDTO;
import com.example.demo.service.CartaoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cartao")
public class CartaoController {

    private final CartaoService cartaoService;

    public CartaoController(CartaoService cartaoService) {
        this.cartaoService = cartaoService;
    }

    /**
     * 💳 POST /cartao?idCliente={id}
     * Cadastra um novo cartão para o cliente
     * Requer: ROLE_CLIENTE
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_CLIENTE')")
    public ResponseEntity<CartaoResponseDTO> cadastrarCartao(
            @RequestBody @Valid CartaoRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        // TODO: Pegar idCliente do usuário autenticado
        // Por enquanto, recebe via parâmetro ou assume ID fixo
        Integer idCliente = 1; // TEMPORÁRIO

        CartaoResponseDTO response = cartaoService.cadastrarCartao(idCliente, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 📋 GET /cartao?idCliente={id}
     * Lista todos os cartões do cliente
     * Requer: ROLE_CLIENTE
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_CLIENTE')")
    public ResponseEntity<List<CartaoResumoDTO>> listarCartoes(
            @AuthenticationPrincipal UserDetails userDetails) {

        // TODO: Pegar idCliente do usuário autenticado
        Integer idCliente = 1; // TEMPORÁRIO

        List<CartaoResumoDTO> cartoes = cartaoService.listarCartoesDoCliente(idCliente);

        return ResponseEntity.ok(cartoes);
    }

    /**
     * 🔍 GET /cartao/{id}
     * Busca um cartão específico
     * Requer: ROLE_CLIENTE
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_CLIENTE')")
    public ResponseEntity<CartaoResponseDTO> buscarPorId(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {

        // TODO: Pegar idCliente do usuário autenticado
        Integer idCliente = 1; // TEMPORÁRIO

        CartaoResponseDTO cartao = cartaoService.buscarPorId(id, idCliente);

        return ResponseEntity.ok(cartao);
    }

    /**
     * ⭐ PUT /cartao/{id}/principal
     * Define um cartão como principal
     * Requer: ROLE_CLIENTE
     */
    @PutMapping("/{id}/principal")
    @PreAuthorize("hasRole('ROLE_CLIENTE')")
    public ResponseEntity<CartaoResponseDTO> definirComoPrincipal(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {

        // TODO: Pegar idCliente do usuário autenticado
        Integer idCliente = 1; // TEMPORÁRIO

        CartaoResponseDTO cartao = cartaoService.definirComoPrincipal(id, idCliente);

        return ResponseEntity.ok(cartao);
    }

    /**
     * 🗑️ DELETE /cartao/{id}
     * Remove um cartão
     * Requer: ROLE_CLIENTE
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_CLIENTE')")
    public ResponseEntity<Void> removerCartao(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {

        // TODO: Pegar idCliente do usuário autenticado
        Integer idCliente = 1; // TEMPORÁRIO

        cartaoService.removerCartao(id, idCliente);

        return ResponseEntity.noContent().build();
    }
}
