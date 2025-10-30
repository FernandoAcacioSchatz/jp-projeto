package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Cartao;

@Repository
public interface CartaoRepository extends JpaRepository<Cartao, Integer> {

    /**
     * Busca todos os cartões de um cliente
     */
    List<Cartao> findByCliente_IdCliente(Integer idCliente);

    /**
     * Busca o cartão principal do cliente
     */
    Optional<Cartao> findByCliente_IdClienteAndIsPrincipalTrue(Integer idCliente);

    /**
     * Verifica se um cartão pertence a um cliente
     */
    boolean existsByIdAndCliente_IdCliente(Integer id, Integer idCliente);
}
