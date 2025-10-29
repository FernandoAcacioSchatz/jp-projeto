package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Carrinho;

@Repository
public interface CarrinhoRepository extends JpaRepository<Carrinho, Integer> {

    /**
     * Busca o carrinho de um cliente específico
     */
    Optional<Carrinho> findByCliente_IdCliente(Integer idCliente);

    /**
     * Verifica se um cliente já possui um carrinho
     */
    boolean existsByCliente_IdCliente(Integer idCliente);
}
