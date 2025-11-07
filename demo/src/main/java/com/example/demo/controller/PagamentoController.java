package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ConfirmarPixDTO;
import com.example.demo.dto.PagamentoPixResponseDTO;
import com.example.demo.model.PagamentoPix;
import com.example.demo.service.PixService;

@RestController
@RequestMapping("/pagamento")
public class PagamentoController {

    private final PixService pixService;

    public PagamentoController(PixService pixService) {
        this.pixService = pixService;
    }

    @GetMapping("/pix/{idPedido}")
    public ResponseEntity<PagamentoPixResponseDTO> buscarPixDoPedido(@PathVariable Integer idPedido) {

        PagamentoPix pagamentoPix = pixService.buscarPorPedido(idPedido);

        return ResponseEntity.ok(new PagamentoPixResponseDTO(pagamentoPix));
    }

    @PostMapping("/pix/{idPedido}/confirmar")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PagamentoPixResponseDTO> confirmarPagamento(
            @PathVariable Integer idPedido,
            @RequestBody ConfirmarPixDTO dto) {

        PagamentoPix pagamentoPix = pixService.confirmarPagamento(idPedido, dto.txid());

        return ResponseEntity.ok(new PagamentoPixResponseDTO(pagamentoPix));
    }

    @GetMapping("/pix/{idPedido}/status")
    public ResponseEntity<PagamentoPixResponseDTO> verificarStatus(@PathVariable Integer idPedido) {

        PagamentoPix pagamentoPix = pixService.buscarPorPedido(idPedido);

        if (pagamentoPix.isExpirado() && 
            pagamentoPix.getStatusPagamento() == com.example.demo.model.StatusPagamentoPix.PENDENTE) {
            pagamentoPix.expirarPagamento();
        }

        return ResponseEntity.ok(new PagamentoPixResponseDTO(pagamentoPix));
    }
}
