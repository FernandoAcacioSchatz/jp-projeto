package com.example.demo.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.dto.CategoriaRequestDTO;
import com.example.demo.dto.CategoriaResponseDTO;
import com.example.demo.exception.RegraNegocioException;
import com.example.demo.model.Categoria;
import com.example.demo.repository.CategoriaRepository;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    /**
     * Busca uma categoria por ID
     */
    public Categoria findById(Integer idCategoria) {
        return categoriaRepository
                .findById(idCategoria)
                .orElseThrow(() -> new NoSuchElementException(
                        "Categoria " + idCategoria + " não encontrada! Tipo: " + Categoria.class.getName()));
    }

    /**
     * Lista todas as categorias
     */
    public List<CategoriaResponseDTO> listarTodasCategorias() {
        List<Categoria> categorias = categoriaRepository.findAll();
        return categorias.stream()
                .map(CategoriaResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Lista todas as categorias com paginação
     */
    public Page<CategoriaResponseDTO> listarTodasCategoriasPaginado(Pageable pageable) {
        Page<Categoria> categorias = categoriaRepository.findAll(pageable);
        return categorias.map(CategoriaResponseDTO::new);
    }

    /**
     * Cria uma nova categoria
     */
    public Categoria inserirCategoria(CategoriaRequestDTO dto) {
        
        // Verifica se já existe uma categoria com este nome
        if (categoriaRepository.findByNomeIgnoreCase(dto.nome()).isPresent()) {
            throw new RegraNegocioException("Já existe uma categoria com o nome '" + dto.nome() + "'.");
        }

        Categoria novaCategoria = new Categoria();
        novaCategoria.setNome(dto.nome());
        novaCategoria.setDescricao(dto.descricao());

        try {
            return categoriaRepository.save(novaCategoria);
        } catch (DataIntegrityViolationException e) {
            throw new RegraNegocioException("Erro de integridade ao salvar categoria: " + e.getMessage(), e);
        }
    }

    /**
     * Atualiza uma categoria existente
     */
    public Categoria alterarCategoria(CategoriaRequestDTO dto, Integer idCategoria) {
        
        Categoria categoriaExistente = this.findById(idCategoria);

        // Verifica se o novo nome já está sendo usado por outra categoria
        if (dto.nome() != null && !dto.nome().equals(categoriaExistente.getNome())) {
            categoriaRepository.findByNomeIgnoreCase(dto.nome()).ifPresent(c -> {
                if (!c.getId().equals(idCategoria)) {
                    throw new RegraNegocioException("Já existe uma categoria com o nome '" + dto.nome() + "'.");
                }
            });
            categoriaExistente.setNome(dto.nome());
        }

        if (dto.descricao() != null) {
            categoriaExistente.setDescricao(dto.descricao());
        }

        return categoriaRepository.save(categoriaExistente);
    }

    /**
     * Deleta uma categoria (soft delete)
     */
    public void deletarCategoria(Integer idCategoria) {
        
        Categoria categoriaParaDeletar = this.findById(idCategoria);

        // Verifica se existem produtos associados
        if (categoriaParaDeletar.getQuantidadeProdutos() > 0) {
            throw new RegraNegocioException(
                "Não é possível deletar a categoria '" + categoriaParaDeletar.getNome() + 
                "' pois existem " + categoriaParaDeletar.getQuantidadeProdutos() + 
                " produto(s) associado(s). Remova os produtos primeiro ou altere suas categorias."
            );
        }

        // Soft delete
        categoriaParaDeletar.markAsDeleted();
        categoriaRepository.save(categoriaParaDeletar);
        
        // Para hard delete (exclusão física), use:
        // categoriaRepository.delete(categoriaParaDeletar);
    }
}
