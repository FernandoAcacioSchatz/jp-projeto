package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.PagamentoPix;

@Repository
public interface PagamentoPixRepository extends JpaRepository<PagamentoPix, Integer> {

    
    Optional<PagamentoPix> findByPedido_Id(Integer idPedido);

   
    Optional<PagamentoPix> findByTxid(String txid);
}
