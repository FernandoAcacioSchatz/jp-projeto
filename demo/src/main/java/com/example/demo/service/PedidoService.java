package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.CriarPedidoDTO;
import com.example.demo.dto.PedidoResponseDTO;
import com.example.demo.dto.PedidoResumoDTO;
import com.example.demo.exception.RegraNegocioException;
import com.example.demo.model.Carrinho;
import com.example.demo.model.Cartao;
import com.example.demo.model.Cliente;
import com.example.demo.model.Endereco;
import com.example.demo.model.ItemCarrinho;
import com.example.demo.model.ItemPedido;
import com.example.demo.model.Pedido;
import com.example.demo.model.Produto;
import com.example.demo.model.StatusPedido;
import com.example.demo.model.TipoPagamento;
import com.example.demo.repository.CarrinhoRepository;
import com.example.demo.repository.CartaoRepository;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.EnderecoRepository;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.ProdutoRepository;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CarrinhoRepository carrinhoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final EnderecoRepository enderecoRepository;
    private final QRCodeService qrCodeService;
    private final PixService pixService;
    private final CartaoRepository cartaoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
            CarrinhoRepository carrinhoRepository,
            ClienteRepository clienteRepository,
            ProdutoRepository produtoRepository,
            EnderecoRepository enderecoRepository,
            QRCodeService qrCodeService,
            PixService pixService,
            CartaoRepository cartaoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.carrinhoRepository = carrinhoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.enderecoRepository = enderecoRepository;
        this.qrCodeService = qrCodeService;
        this.pixService = pixService;
        this.cartaoRepository = cartaoRepository;
    }

    @Transactional
    public PedidoResponseDTO criarPedidoDoCarrinho(Integer idCliente, CriarPedidoDTO dto) {

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RegraNegocioException("Cliente não encontrado."));

        Carrinho carrinho = carrinhoRepository.findByCliente_IdCliente(idCliente)
                .orElseThrow(() -> new RegraNegocioException("Carrinho não encontrado."));

        if (carrinho.getItens().isEmpty()) {
            throw new RegraNegocioException("Não é possível criar pedido com carrinho vazio.");
        }

        Endereco enderecoEntrega;
        if (dto.idEnderecoEntrega() != null) {
            enderecoEntrega = enderecoRepository.findById(dto.idEnderecoEntrega())
                    .orElseThrow(() -> new RegraNegocioException("Endereço não encontrado."));

            if (!enderecoEntrega.getCliente().getIdCliente().equals(idCliente)) {
                throw new RegraNegocioException("O endereço selecionado não pertence a você.");
            }
        } else {
            enderecoEntrega = enderecoRepository.findByCliente_IdClienteAndIsPrincipalTrue(idCliente)
                    .orElseThrow(() -> new RegraNegocioException(
                            "Você não possui um endereço principal cadastrado. Cadastre um endereço ou selecione um existente."));
        }

        Cartao cartaoSelecionado = null;
        if (dto.tipoPagamento() == TipoPagamento.CARTAO_CREDITO ||
                dto.tipoPagamento() == TipoPagamento.CARTAO_DEBITO) {

            if (dto.idCartao() != null) {

                cartaoSelecionado = cartaoRepository.findById(dto.idCartao())
                        .orElseThrow(() -> new RegraNegocioException("Cartão não encontrado."));

                if (!cartaoSelecionado.getCliente().getIdCliente().equals(idCliente)) {
                    throw new RegraNegocioException("Este cartão não pertence a você.");
                }
            } else {

                cartaoSelecionado = cartaoRepository.findByCliente_IdClienteAndIsPrincipalTrue(idCliente)
                        .orElseThrow(() -> new RegraNegocioException(
                                "Você não possui um cartão principal cadastrado. " +
                                        "Cadastre um cartão ou informe o ID de um cartão existente."));
            }

            if (cartaoSelecionado.isVencido()) {
                throw new RegraNegocioException(
                        "O cartão selecionado está vencido. Por favor, selecione outro cartão.");
            }
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setTipoPagamento(dto.tipoPagamento());
        pedido.setEnderecoEntrega(enderecoEntrega);
        pedido.setCartao(cartaoSelecionado);

        for (ItemCarrinho itemCarrinho : carrinho.getItens()) {
            Produto produto = itemCarrinho.getProduto();

            if (produto.getEstoque() < itemCarrinho.getQuantidade()) {
                throw new RegraNegocioException(
                        "Estoque insuficiente para o produto: " + produto.getNome() +
                                ". Disponível: " + produto.getEstoque());
            }

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setProduto(produto);
            itemPedido.setQuantidade(itemCarrinho.getQuantidade());
            itemPedido.setPrecoUnitario(itemCarrinho.getPrecoUnitario());
            itemPedido.setPedido(pedido);

            pedido.getItens().add(itemPedido);

            produto.setEstoque(produto.getEstoque() - itemCarrinho.getQuantidade());
            produtoRepository.save(produto);
        }

        pedido.setValorTotal(pedido.calcularValorTotal());

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        for (ItemPedido itemPedido : pedidoSalvo.getItens()) {
            qrCodeService.gerarQRCodeParaItem(itemPedido);
        }

        if (dto.tipoPagamento() == TipoPagamento.PIX) {
            pixService.gerarPagamentoPix(pedidoSalvo);
        }

        carrinho.getItens().clear();
        carrinhoRepository.save(carrinho);

        return new PedidoResponseDTO(pedidoSalvo);
    }

    public PedidoResponseDTO buscarPorId(Integer idPedido) {

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RegraNegocioException("Pedido não encontrado com ID: " + idPedido));

        return new PedidoResponseDTO(pedido);
    }

    public List<PedidoResumoDTO> listarPedidosDoCliente(Integer idCliente) {

        List<Pedido> pedidos = pedidoRepository.findByCliente_IdClienteOrderByDataPedidoDesc(idCliente);

        return pedidos.stream()
                .map(PedidoResumoDTO::new)
                .collect(Collectors.toList());
    }

    public Page<PedidoResumoDTO> listarPedidosDoClientePaginado(Integer idCliente, Pageable pageable) {

        Page<Pedido> pedidos = pedidoRepository.findByCliente_IdClienteOrderByDataPedidoDesc(idCliente, pageable);

        return pedidos.map(PedidoResumoDTO::new);
    }

    public List<PedidoResumoDTO> listarPedidosDoFornecedor(Integer idFornecedor) {

        List<Pedido> pedidos = pedidoRepository.findPedidosPorFornecedor(idFornecedor);

        return pedidos.stream()
                .map(PedidoResumoDTO::new)
                .collect(Collectors.toList());
    }

    public Page<PedidoResumoDTO> listarPedidosDoFornecedorPaginado(Integer idFornecedor, Pageable pageable) {

        Page<Pedido> pedidos = pedidoRepository.findPedidosPorFornecedor(idFornecedor, pageable);

        return pedidos.map(PedidoResumoDTO::new);
    }

    @Transactional
    public PedidoResponseDTO atualizarStatus(Integer idPedido, StatusPedido novoStatus) {

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RegraNegocioException("Pedido não encontrado."));

        if (pedido.estaFinalizado()) {
            throw new RegraNegocioException("Não é possível alterar o status de um pedido finalizado.");
        }

        validarTransicaoStatus(pedido.getStatus(), novoStatus);

        pedido.setStatus(novoStatus);

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return new PedidoResponseDTO(pedidoAtualizado);
    }

    @Transactional
    public PedidoResponseDTO cancelarPedido(Integer idPedido) {

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RegraNegocioException("Pedido não encontrado."));

        if (!pedido.podeCancelar()) {
            throw new RegraNegocioException("Não é possível cancelar um pedido com status: " + pedido.getStatus());
        }

        for (ItemPedido item : pedido.getItens()) {
            Produto produto = item.getProduto();
            produto.setEstoque(produto.getEstoque() + item.getQuantidade());
            produtoRepository.save(produto);
        }

        pedido.setStatus(StatusPedido.CANCELADO);

        Pedido pedidoCancelado = pedidoRepository.save(pedido);

        return new PedidoResponseDTO(pedidoCancelado);
    }

    private void validarTransicaoStatus(StatusPedido statusAtual, StatusPedido novoStatus) {

        if (statusAtual == StatusPedido.PENDENTE) {
            if (novoStatus != StatusPedido.PAGO && novoStatus != StatusPedido.CANCELADO) {
                throw new RegraNegocioException("Transição de status inválida: " + statusAtual + " -> " + novoStatus);
            }
        }

        if (statusAtual == StatusPedido.PAGO) {
            if (novoStatus != StatusPedido.ENVIADO && novoStatus != StatusPedido.CANCELADO) {
                throw new RegraNegocioException("Transição de status inválida: " + statusAtual + " -> " + novoStatus);
            }
        }

        if (statusAtual == StatusPedido.ENVIADO) {
            if (novoStatus != StatusPedido.ENTREGUE) {
                throw new RegraNegocioException("Transição de status inválida: " + statusAtual + " -> " + novoStatus);
            }
        }
    }
}
