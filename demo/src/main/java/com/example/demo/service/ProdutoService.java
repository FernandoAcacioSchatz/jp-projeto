package com.example.demo.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.ProdutoRequestDTO;
import com.example.demo.dto.ProdutoResponseDTO;
import com.example.demo.model.Categoria;
import com.example.demo.model.Fornecedor;
import com.example.demo.model.Produto;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.FornecedorRepository;
import com.example.demo.repository.ProdutoRepository;

import jakarta.transaction.Transactional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository pRepository;

    @Autowired
    private CategoriaRepository catRepository;

    @Autowired
    private FornecedorRepository fRepository;

    public List<ProdutoResponseDTO> listarTodosProdutos() {
        // 1. Busca as entidades
        List<Produto> produtos = pRepository.findAll();

        // 2. Converte a lista de Entidades para uma lista de DTOs
        return produtos.stream()
                .map(produto -> new ProdutoResponseDTO(produto)) // Usa o construtor do record
                .collect(Collectors.toList());
    }

    public Produto findById(Integer idProduto) {
        Produto produtos = pRepository.findById(idProduto)
                .orElseThrow(
                        () -> new NoSuchElementException(
                                "Produto " + idProduto + " não encontrado! Tipo: " + Produto.class.getName()));
        return produtos;
    }

    @Transactional
    public ProdutoResponseDTO criarProduto(ProdutoRequestDTO dto, String emailUsuarioLogado) {

        Fornecedor fornecedorLogado = fRepository.findByUser_Email(emailUsuarioLogado)
                .orElseThrow(() -> new NoSuchElementException("Usuário (Fornecedor) não encontrado."));

        Categoria categoria = catRepository.findById(dto.idCategoria())
                .orElseThrow(() -> new NoSuchElementException("Categoria não encontrada com ID: " + dto.idCategoria()));

        Produto novoProduto = new Produto();
        novoProduto.setNome(dto.nome());
        novoProduto.setDescricao(dto.descricao());
        novoProduto.setPreco(dto.preco());
        novoProduto.setEstoque(dto.estoque());
        novoProduto.setCategoria(categoria);

        novoProduto.setFornecedor(fornecedorLogado);

        Produto produtoSalvo = pRepository.save(novoProduto);

        return new ProdutoResponseDTO(produtoSalvo);
    }

}
