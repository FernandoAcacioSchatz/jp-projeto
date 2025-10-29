package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.PedidoResponseDTO;
import com.example.demo.dto.PedidoResumoDTO;
import com.example.demo.exception.RegraNegocioException;
import com.example.demo.model.Carrinho;
import com.example.demo.model.Cliente;
import com.example.demo.model.ItemCarrinho;
import com.example.demo.model.ItemPedido;
import com.example.demo.model.Pedido;
import com.example.demo.model.Produto;
import com.example.demo.model.StatusPedido;
import com.example.demo.repository.CarrinhoRepository;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.ProdutoRepository;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CarrinhoRepository carrinhoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
            CarrinhoRepository carrinhoRepository,
            ClienteRepository clienteRepository,
            ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.carrinhoRepository = carrinhoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    /**
     * Cria um pedido a partir do carrinho do cliente
     */
    @Transactional
    public PedidoResponseDTO criarPedidoDoCarrinho(Integer idCliente) {

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RegraNegocioException("Cliente não encontrado."));

        Carrinho carrinho = carrinhoRepository.findByCliente_IdCliente(idCliente)
                .orElseThrow(() -> new RegraNegocioException("Carrinho não encontrado."));

        if (carrinho.getItens().isEmpty()) {
            throw new RegraNegocioException("Não é possível criar pedido com carrinho vazio.");
        }

        // Valida estoque e cria o pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.PENDENTE);

        for (ItemCarrinho itemCarrinho : carrinho.getItens()) {
            Produto produto = itemCarrinho.getProduto();

            // Valida estoque
            if (produto.getEstoque() < itemCarrinho.getQuantidade()) {
                throw new RegraNegocioException(
                        "Estoque insuficiente para o produto: " + produto.getNome() +
                                ". Disponível: " + produto.getEstoque());
            }

            // Cria item do pedido
            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setProduto(produto);
            itemPedido.setQuantidade(itemCarrinho.getQuantidade());
            itemPedido.setPrecoUnitario(itemCarrinho.getPrecoUnitario());
            itemPedido.setPedido(pedido);

            pedido.getItens().add(itemPedido);

            // Decrementa estoque
            produto.setEstoque(produto.getEstoque() - itemCarrinho.getQuantidade());
            produtoRepository.save(produto);
        }

        // Calcula e define o valor total
        pedido.setValorTotal(pedido.calcularValorTotal());

        // Salva o pedido
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        // Limpa o carrinho após criar o pedido
        carrinho.getItens().clear();
        carrinhoRepository.save(carrinho);

        return new PedidoResponseDTO(pedidoSalvo);
    }

    /**
     * Busca um pedido por ID
     */
    public PedidoResponseDTO buscarPorId(Integer idPedido) {

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RegraNegocioException("Pedido não encontrado com ID: " + idPedido));

        return new PedidoResponseDTO(pedido);
    }

    /**
     * Lista todos os pedidos de um cliente
     */
    public List<PedidoResumoDTO> listarPedidosDoCliente(Integer idCliente) {

        List<Pedido> pedidos = pedidoRepository.findByCliente_IdClienteOrderByDataPedidoDesc(idCliente);

        return pedidos.stream()
                .map(PedidoResumoDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Lista pedidos de um cliente com paginação
     */
    public Page<PedidoResumoDTO> listarPedidosDoClientePaginado(Integer idCliente, Pageable pageable) {

        Page<Pedido> pedidos = pedidoRepository.findByCliente_IdClienteOrderByDataPedidoDesc(idCliente, pageable);

        return pedidos.map(PedidoResumoDTO::new);
    }

    /**
     * Lista pedidos que contenham produtos de um fornecedor
     */
    public List<PedidoResumoDTO> listarPedidosDoFornecedor(Integer idFornecedor) {

        List<Pedido> pedidos = pedidoRepository.findPedidosPorFornecedor(idFornecedor);

        return pedidos.stream()
                .map(PedidoResumoDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Lista pedidos de um fornecedor com paginação
     */
    public Page<PedidoResumoDTO> listarPedidosDoFornecedorPaginado(Integer idFornecedor, Pageable pageable) {

        Page<Pedido> pedidos = pedidoRepository.findPedidosPorFornecedor(idFornecedor, pageable);

        return pedidos.map(PedidoResumoDTO::new);
    }

    /**
     * Atualiza o status de um pedido
     */
    @Transactional
    public PedidoResponseDTO atualizarStatus(Integer idPedido, StatusPedido novoStatus) {

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RegraNegocioException("Pedido não encontrado."));

        if (pedido.estaFinalizado()) {
            throw new RegraNegocioException("Não é possível alterar o status de um pedido finalizado.");
        }

        // Validação de transições de status
        validarTransicaoStatus(pedido.getStatus(), novoStatus);

        pedido.setStatus(novoStatus);

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return new PedidoResponseDTO(pedidoAtualizado);
    }

    /**
     * Cancela um pedido e devolve o estoque
     */
    @Transactional
    public PedidoResponseDTO cancelarPedido(Integer idPedido) {

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RegraNegocioException("Pedido não encontrado."));

        if (!pedido.podeCancelar()) {
            throw new RegraNegocioException("Não é possível cancelar um pedido com status: " + pedido.getStatus());
        }

        // Devolve o estoque
        for (ItemPedido item : pedido.getItens()) {
            Produto produto = item.getProduto();
            produto.setEstoque(produto.getEstoque() + item.getQuantidade());
            produtoRepository.save(produto);
        }

        pedido.setStatus(StatusPedido.CANCELADO);

        Pedido pedidoCancelado = pedidoRepository.save(pedido);

        return new PedidoResponseDTO(pedidoCancelado);
    }

    /**
     * Valida transição de status
     */
    private void validarTransicaoStatus(StatusPedido statusAtual, StatusPedido novoStatus) {
        
        // PENDENTE pode ir para PAGO ou CANCELADO
        if (statusAtual == StatusPedido.PENDENTE) {
            if (novoStatus != StatusPedido.PAGO && novoStatus != StatusPedido.CANCELADO) {
                throw new RegraNegocioException("Transição de status inválida: " + statusAtual + " -> " + novoStatus);
            }
        }

        // PAGO pode ir para ENVIADO ou CANCELADO
        if (statusAtual == StatusPedido.PAGO) {
            if (novoStatus != StatusPedido.ENVIADO && novoStatus != StatusPedido.CANCELADO) {
                throw new RegraNegocioException("Transição de status inválida: " + statusAtual + " -> " + novoStatus);
            }
        }

        // ENVIADO pode ir apenas para ENTREGUE
        if (statusAtual == StatusPedido.ENVIADO) {
            if (novoStatus != StatusPedido.ENTREGUE) {
                throw new RegraNegocioException("Transição de status inválida: " + statusAtual + " -> " + novoStatus);
            }
        }
    }
}
