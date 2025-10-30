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

/**
 * Controller para gerenciamento de pedidos
 * Clientes podem criar e visualizar seus pedidos
 * Fornecedores podem visualizar pedidos que contenham seus produtos
 */
@RestController
@RequestMapping("/pedido")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    /**
     * Cria um pedido a partir do carrinho do cliente logado
     * POST /pedido
     * Body: { "tipoPagamento": "PIX", "idEnderecoEntrega": 1 (opcional) }
     */
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

    /**
     * Busca um pedido específico por ID
     * GET /pedido/{idPedido}
     */
    @GetMapping("/{idPedido}")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(
            @PathVariable Integer idPedido,
            Authentication authentication) {

        // Verifica se é cliente ou fornecedor
        verificarAcessoPedido(idPedido, authentication);

        PedidoResponseDTO pedido = pedidoService.buscarPorId(idPedido);

        return ResponseEntity.ok(pedido);
    }

    /**
     * Lista todos os pedidos do cliente logado
     * GET /pedido/meus-pedidos
     */
    @GetMapping("/meus-pedidos")
    public ResponseEntity<List<PedidoResumoDTO>> listarMeusPedidos(Authentication authentication) {

        Integer idCliente = obterIdClienteLogado(authentication);

        List<PedidoResumoDTO> pedidos = pedidoService.listarPedidosDoCliente(idCliente);

        return ResponseEntity.ok(pedidos);
    }

    /**
     * Lista pedidos do cliente com paginação
     * GET /pedido/meus-pedidos/paginado?page=0&size=10
     */
    @GetMapping("/meus-pedidos/paginado")
    public ResponseEntity<Page<PedidoResumoDTO>> listarMeusPedidosPaginado(
            @PageableDefault(size = 10, sort = "dataPedido") Pageable pageable,
            Authentication authentication) {

        Integer idCliente = obterIdClienteLogado(authentication);

        Page<PedidoResumoDTO> pedidos = pedidoService.listarPedidosDoClientePaginado(idCliente, pageable);

        return ResponseEntity.ok(pedidos);
    }

    /**
     * Lista pedidos que contenham produtos do fornecedor logado
     * GET /pedido/vendas
     */
    @GetMapping("/vendas")
    public ResponseEntity<List<PedidoResumoDTO>> listarVendas(Authentication authentication) {

        Integer idFornecedor = obterIdFornecedorLogado(authentication);

        List<PedidoResumoDTO> pedidos = pedidoService.listarPedidosDoFornecedor(idFornecedor);

        return ResponseEntity.ok(pedidos);
    }

    /**
     * Lista vendas do fornecedor com paginação
     * GET /pedido/vendas/paginado?page=0&size=10
     */
    @GetMapping("/vendas/paginado")
    public ResponseEntity<Page<PedidoResumoDTO>> listarVendasPaginado(
            @PageableDefault(size = 10, sort = "dataPedido") Pageable pageable,
            Authentication authentication) {

        Integer idFornecedor = obterIdFornecedorLogado(authentication);

        Page<PedidoResumoDTO> pedidos = pedidoService.listarPedidosDoFornecedorPaginado(idFornecedor, pageable);

        return ResponseEntity.ok(pedidos);
    }

    /**
     * Atualiza o status de um pedido
     * PATCH /pedido/{idPedido}/status?status=PAGO
     */
    @PatchMapping("/{idPedido}/status")
    public ResponseEntity<PedidoResponseDTO> atualizarStatus(
            @PathVariable Integer idPedido,
            @RequestParam StatusPedido status,
            Authentication authentication) {

        // Apenas clientes podem atualizar status (simula pagamento)
        obterIdClienteLogado(authentication);

        PedidoResponseDTO pedido = pedidoService.atualizarStatus(idPedido, status);

        return ResponseEntity.ok(pedido);
    }

    /**
     * Cancela um pedido
     * POST /pedido/{idPedido}/cancelar
     */
    @PostMapping("/{idPedido}/cancelar")
    public ResponseEntity<PedidoResponseDTO> cancelarPedido(
            @PathVariable Integer idPedido,
            Authentication authentication) {

        // Apenas o cliente dono pode cancelar
        obterIdClienteLogado(authentication);

        PedidoResponseDTO pedido = pedidoService.cancelarPedido(idPedido);

        return ResponseEntity.ok(pedido);
    }

    /**
     * Método auxiliar para obter o ID do cliente logado
     */
    private Integer obterIdClienteLogado(Authentication authentication) {
        String email = authentication.getName();

        Cliente cliente = clienteRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado para o usuário logado."));

        return cliente.getIdCliente();
    }

    /**
     * Método auxiliar para obter o ID do fornecedor logado
     */
    private Integer obterIdFornecedorLogado(Authentication authentication) {
        String email = authentication.getName();

        Fornecedor fornecedor = fornecedorRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado para o usuário logado."));

        return fornecedor.getIdFornecedor();
    }

    /**
     * Verifica se o usuário logado tem acesso ao pedido
     */
    private void verificarAcessoPedido(Integer idPedido, Authentication authentication) {
        String email = authentication.getName();

        // Tenta buscar como cliente
        clienteRepository.findByUser_Email(email)
                .ifPresentOrElse(
                        cliente -> {
                            // Verifica se o pedido pertence ao cliente
                            PedidoResponseDTO pedido = pedidoService.buscarPorId(idPedido);
                            if (!pedido.idCliente().equals(cliente.getIdCliente())) {
                                throw new RuntimeException("Você não tem permissão para acessar este pedido.");
                            }
                        },
                        () -> {
                            // Tenta buscar como fornecedor (pode ver pedidos com seus produtos)
                            fornecedorRepository.findByUser_Email(email)
                                    .orElseThrow(() -> new RuntimeException(
                                            "Usuário não encontrado ou sem permissão."));
                        });
    }
}
