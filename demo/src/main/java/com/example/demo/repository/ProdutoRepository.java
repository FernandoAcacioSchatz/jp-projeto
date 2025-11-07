package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {

    
    List<Produto> findByNomeContainingIgnoreCase(String nome);

   
    Page<Produto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

   
    List<Produto> findByCategoria_Id(Integer idCategoria);

    Page<Produto> findByCategoria_Id(Integer idCategoria, Pageable pageable);
}
