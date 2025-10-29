package com.example.demo.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.AdicionarItemCarrinhoDTO;
import com.example.demo.dto.CarrinhoResponseDTO;
import com.example.demo.exception.RegraNegocioException;
import com.example.demo.model.Carrinho;
import com.example.demo.model.Cliente;
import com.example.demo.model.ItemCarrinho;
import com.example.demo.model.Produto;
import com.example.demo.repository.CarrinhoRepository;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.ProdutoRepository;

@Service
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public CarrinhoService(CarrinhoRepository carrinhoRepository,
            ClienteRepository clienteRepository,
            ProdutoRepository produtoRepository) {
        this.carrinhoRepository = carrinhoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public Carrinho buscarOuCriarCarrinho(Integer idCliente) {

        Optional<Carrinho> carrinhoExistente = carrinhoRepository.findByCliente_IdCliente(idCliente);

        if (carrinhoExistente.isPresent()) {
            return carrinhoExistente.get();
        }

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RegraNegocioException("Cliente não encontrado com ID: " + idCliente));

        Carrinho novoCarrinho = new Carrinho();
        novoCarrinho.setCliente(cliente);

        return carrinhoRepository.save(novoCarrinho);
    }

    public CarrinhoResponseDTO obterCarrinho(Integer idCliente) {

        Carrinho carrinho = buscarOuCriarCarrinho(idCliente);

        return new CarrinhoResponseDTO(carrinho);
    }

    @Transactional
    public CarrinhoResponseDTO adicionarItem(Integer idCliente, AdicionarItemCarrinhoDTO dto) {

        Carrinho carrinho = buscarOuCriarCarrinho(idCliente);

        Produto produto = produtoRepository.findById(dto.idProduto())
                .orElseThrow(() -> new RegraNegocioException("Produto não encontrado com ID: " + dto.idProduto()));

        // Verifica se há estoque suficiente
        if (produto.getEstoque() < dto.quantidade()) {
            throw new RegraNegocioException("Estoque insuficiente. Disponível: " + produto.getEstoque());
        }

        // Verifica se o produto já está no carrinho
        Optional<ItemCarrinho> itemExistente = carrinho.getItens().stream()
                .filter(item -> item.getProduto().getId().equals(dto.idProduto()))
                .findFirst();

        if (itemExistente.isPresent()) {
            // Atualiza a quantidade se o produto já estiver no carrinho
            ItemCarrinho item = itemExistente.get();
            int novaQuantidade = item.getQuantidade() + dto.quantidade();

            if (produto.getEstoque() < novaQuantidade) {
                throw new RegraNegocioException("Estoque insuficiente. Disponível: " + produto.getEstoque());
            }

            item.setQuantidade(novaQuantidade);
        } else {
            // Adiciona novo item ao carrinho
            ItemCarrinho novoItem = new ItemCarrinho();
            novoItem.setProduto(produto);
            novoItem.setQuantidade(dto.quantidade());
            novoItem.setPrecoUnitario(produto.getPreco());
            novoItem.setCarrinho(carrinho);

            carrinho.getItens().add(novoItem);
        }

        Carrinho carrinhoAtualizado = carrinhoRepository.save(carrinho);

        return new CarrinhoResponseDTO(carrinhoAtualizado);
    }

    @Transactional
    public CarrinhoResponseDTO atualizarQuantidadeItem(Integer idCliente, Integer idItem, Integer novaQuantidade) {

        if (novaQuantidade <= 0) {
            throw new RegraNegocioException("A quantidade deve ser maior que zero.");
        }

        Carrinho carrinho = carrinhoRepository.findByCliente_IdCliente(idCliente)
                .orElseThrow(() -> new RegraNegocioException("Carrinho não encontrado para o cliente."));

        ItemCarrinho item = carrinho.getItens().stream()
                .filter(i -> i.getId().equals(idItem))
                .findFirst()
                .orElseThrow(() -> new RegraNegocioException("Item não encontrado no carrinho."));

        // Verifica estoque
        if (item.getProduto().getEstoque() < novaQuantidade) {
            throw new RegraNegocioException("Estoque insuficiente. Disponível: " + item.getProduto().getEstoque());
        }

        item.setQuantidade(novaQuantidade);

        Carrinho carrinhoAtualizado = carrinhoRepository.save(carrinho);

        return new CarrinhoResponseDTO(carrinhoAtualizado);
    }

    @Transactional
    public CarrinhoResponseDTO removerItem(Integer idCliente, Integer idItem) {

        Carrinho carrinho = carrinhoRepository.findByCliente_IdCliente(idCliente)
                .orElseThrow(() -> new RegraNegocioException("Carrinho não encontrado para o cliente."));

        ItemCarrinho item = carrinho.getItens().stream()
                .filter(i -> i.getId().equals(idItem))
                .findFirst()
                .orElseThrow(() -> new RegraNegocioException("Item não encontrado no carrinho."));

        carrinho.getItens().remove(item);

        Carrinho carrinhoAtualizado = carrinhoRepository.save(carrinho);

        return new CarrinhoResponseDTO(carrinhoAtualizado);
    }

    @Transactional
    public void limparCarrinho(Integer idCliente) {

        Carrinho carrinho = carrinhoRepository.findByCliente_IdCliente(idCliente)
                .orElseThrow(() -> new RegraNegocioException("Carrinho não encontrado para o cliente."));

        carrinho.getItens().clear();

        carrinhoRepository.save(carrinho);
    }
}
