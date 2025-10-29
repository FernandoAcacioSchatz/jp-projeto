package com.example.demo.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

        private final ProdutoRepository pRepository;
        private final CategoriaRepository catRepository;
        private final FornecedorRepository fRepository;

        public ProdutoService(ProdutoRepository pRepository,
                        CategoriaRepository catRepository,
                        FornecedorRepository fRepository) {
                this.pRepository = pRepository;
                this.catRepository = catRepository;
                this.fRepository = fRepository;
        }

        public List<ProdutoResponseDTO> listarTodosProdutos() {

                List<Produto> produtos = pRepository.findAll();

                return produtos.stream()
                                .map(produto -> new ProdutoResponseDTO(produto))
                                .collect(Collectors.toList());
        }

        /**
         * Lista todos os produtos com paginação
         */
        public Page<ProdutoResponseDTO> listarTodosProdutosPaginado(Pageable pageable) {

                Page<Produto> produtos = pRepository.findAll(pageable);

                return produtos.map(produto -> new ProdutoResponseDTO(produto));
        }

        /**
         * Busca produtos por nome (busca parcial, case-insensitive)
         */
        public List<ProdutoResponseDTO> buscarProdutosPorNome(String nome) {

                List<Produto> produtos = pRepository.findByNomeContainingIgnoreCase(nome);

                return produtos.stream()
                                .map(produto -> new ProdutoResponseDTO(produto))
                                .collect(Collectors.toList());
        }

        /**
         * Busca produtos por nome com paginação
         */
        public Page<ProdutoResponseDTO> buscarProdutosPorNomePaginado(String nome, Pageable pageable) {

                Page<Produto> produtos = pRepository.findByNomeContainingIgnoreCase(nome, pageable);

                return produtos.map(produto -> new ProdutoResponseDTO(produto));
        }

        public Produto findById(Integer idProduto) {
                Produto produtos = pRepository.findById(idProduto)
                                .orElseThrow(
                                                () -> new NoSuchElementException(
                                                                "Produto " + idProduto + " não encontrado! Tipo: "
                                                                                + Produto.class.getName()));
                return produtos;
        }

        @Transactional
        public ProdutoResponseDTO criarProduto(ProdutoRequestDTO dto, String emailUsuarioLogado) {

                Fornecedor fornecedorLogado = fRepository.findByUser_Email(emailUsuarioLogado)
                                .orElseThrow(() -> new NoSuchElementException("Usuário (Fornecedor) não encontrado."));

                Categoria categoria = catRepository.findById(dto.idCategoria())
                                .orElseThrow(() -> new NoSuchElementException(
                                                "Categoria não encontrada com ID: " + dto.idCategoria()));

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

        @Transactional
        public ProdutoResponseDTO atualizarProduto(Integer idProduto, ProdutoRequestDTO dto, String emailUsuarioLogado) {

                Produto produtoExistente = findById(idProduto);

                Fornecedor fornecedorLogado = fRepository.findByUser_Email(emailUsuarioLogado)
                                .orElseThrow(() -> new NoSuchElementException("Usuário (Fornecedor) não encontrado."));

                if (!produtoExistente.getFornecedor().getIdFornecedor().equals(fornecedorLogado.getIdFornecedor())) {
                        throw new RuntimeException("Você não tem permissão para alterar este produto.");
                }

                if (dto.nome() != null) {
                        produtoExistente.setNome(dto.nome());
                }

                if (dto.descricao() != null) {
                        produtoExistente.setDescricao(dto.descricao());
                }

                if (dto.preco() != null) {
                        produtoExistente.setPreco(dto.preco());
                }

                if (dto.estoque() != null) {
                        produtoExistente.setEstoque(dto.estoque());
                }

                if (dto.idCategoria() != null) {
                        Categoria categoria = catRepository.findById(dto.idCategoria())
                                        .orElseThrow(() -> new NoSuchElementException(
                                                        "Categoria não encontrada com ID: " + dto.idCategoria()));
                        produtoExistente.setCategoria(categoria);
                }

                Produto produtoAtualizado = pRepository.save(produtoExistente);

                return new ProdutoResponseDTO(produtoAtualizado);
        }

        @Transactional
        public void deletarProduto(Integer idProduto, String emailUsuarioLogado) {

                Produto produtoExistente = findById(idProduto);

                Fornecedor fornecedorLogado = fRepository.findByUser_Email(emailUsuarioLogado)
                                .orElseThrow(() -> new NoSuchElementException("Usuário (Fornecedor) não encontrado."));

                if (!produtoExistente.getFornecedor().getIdFornecedor().equals(fornecedorLogado.getIdFornecedor())) {
                        throw new RuntimeException("Você não tem permissão para deletar este produto.");
                }

                // Soft delete - apenas marca como deletado
                produtoExistente.markAsDeleted();
                pRepository.save(produtoExistente);
                
                // Para hard delete (exclusão física), use:
                // pRepository.delete(produtoExistente);
        }

}