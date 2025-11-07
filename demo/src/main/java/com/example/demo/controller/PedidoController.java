package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demo.dto.CriarPedidoDTO;
import com.example.demo.dto.PedidoResponseDTO;
import com.example.demo.dto.PedidoResumoDTO;
import com.example.demo.model.Cliente;
import com.example.demo.model.Fornecedor;
import com.example.demo.model.StatusPedido;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.FornecedorRepository;
import com.example.demo.service.PedidoService;

import jakarta.validation.Valid;

import java.net.URI;

@RestController
@RequestMapping("/pedido")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> criarPedido(
            @Valid @RequestBody CriarPedidoDTO dto,
            Authentication authentication) {

        Integer idCliente = obterIdClienteLogado(authentication);

        PedidoResponseDTO pedido = pedidoService.criarPedidoDoCarrinho(idCliente, dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(pedido.id())
                .toUri();

        return ResponseEntity.created(uri).body(pedido);
    }

    @GetMapping("/{idPedido}")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(
            @PathVariable Integer idPedido,
            Authentication authentication) {

        verificarAcessoPedido(idPedido, authentication);

        PedidoResponseDTO pedido = pedidoService.buscarPorId(idPedido);

        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/meus-pedidos")
    public ResponseEntity<List<PedidoResumoDTO>> listarMeusPedidos(Authentication authentication) {

        Integer idCliente = obterIdClienteLogado(authentication);

        List<PedidoResumoDTO> pedidos = pedidoService.listarPedidosDoCliente(idCliente);

        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/meus-pedidos/paginado")
    public ResponseEntity<Page<PedidoResumoDTO>> listarMeusPedidosPaginado(
            @PageableDefault(size = 10, sort = "dataPedido") Pageable pageable,
            Authentication authentication) {

        Integer idCliente = obterIdClienteLogado(authentication);

        Page<PedidoResumoDTO> pedidos = pedidoService.listarPedidosDoClientePaginado(idCliente, pageable);

        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/vendas")
    public ResponseEntity<List<PedidoResumoDTO>> listarVendas(Authentication authentication) {

        Integer idFornecedor = obterIdFornecedorLogado(authentication);

        List<PedidoResumoDTO> pedidos = pedidoService.listarPedidosDoFornecedor(idFornecedor);

        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/vendas/paginado")
    public ResponseEntity<Page<PedidoResumoDTO>> listarVendasPaginado(
            @PageableDefault(size = 10, sort = "dataPedido") Pageable pageable,
            Authentication authentication) {

        Integer idFornecedor = obterIdFornecedorLogado(authentication);

        Page<PedidoResumoDTO> pedidos = pedidoService.listarPedidosDoFornecedorPaginado(idFornecedor, pageable);

        return ResponseEntity.ok(pedidos);
    }

    @PatchMapping("/{idPedido}/status")
    public ResponseEntity<PedidoResponseDTO> atualizarStatus(
            @PathVariable Integer idPedido,
            @RequestParam StatusPedido status,
            Authentication authentication) {

        obterIdClienteLogado(authentication);

        PedidoResponseDTO pedido = pedidoService.atualizarStatus(idPedido, status);

        return ResponseEntity.ok(pedido);
    }

    @PostMapping("/{idPedido}/cancelar")
    public ResponseEntity<PedidoResponseDTO> cancelarPedido(
            @PathVariable Integer idPedido,
            Authentication authentication) {

        obterIdClienteLogado(authentication);

        PedidoResponseDTO pedido = pedidoService.cancelarPedido(idPedido);

        return ResponseEntity.ok(pedido);
    }

    private Integer obterIdClienteLogado(Authentication authentication) {
        String email = authentication.getName();

        Cliente cliente = clienteRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado para o usuário logado."));

        return cliente.getIdCliente();
    }

    private Integer obterIdFornecedorLogado(Authentication authentication) {
        String email = authentication.getName();

        Fornecedor fornecedor = fornecedorRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado para o usuário logado."));

        return fornecedor.getIdFornecedor();
    }

    private void verificarAcessoPedido(Integer idPedido, Authentication authentication) {
        String email = authentication.getName();

        clienteRepository.findByUser_Email(email)
                .ifPresentOrElse(
                        cliente -> {
                            PedidoResponseDTO pedido = pedidoService.buscarPorId(idPedido);
                            if (!pedido.idCliente().equals(cliente.getIdCliente())) {
                                throw new RuntimeException("Você não tem permissão para acessar este pedido.");
                            }
                        },
                        () -> {
                            fornecedorRepository.findByUser_Email(email)
                                    .orElseThrow(() -> new RuntimeException(
                                            "Usuário não encontrado ou sem permissão."));
                        });
    }
}
