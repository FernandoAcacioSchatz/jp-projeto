package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {

    /**
     * Busca produtos que contenham o nome especificado (case-insensitive)
     * Exemplo: findByNomeContainingIgnoreCase("Notebook")
     */
    List<Produto> findByNomeContainingIgnoreCase(String nome);

    /**
     * Busca produtos por nome com paginação
     */
    Page<Produto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
