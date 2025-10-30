package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.PagamentoPix;

@Repository
public interface PagamentoPixRepository extends JpaRepository<PagamentoPix, Integer> {

    /**
     * Busca pagamento PIX por ID do pedido
     */
    Optional<PagamentoPix> findByPedido_Id(Integer idPedido);

    /**
     * Busca pagamento PIX por TXID
     */
    Optional<PagamentoPix> findByTxid(String txid);
}
