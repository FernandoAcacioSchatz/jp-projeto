package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.QRCodeRastreamento;

@Repository
public interface QRCodeRastreamentoRepository extends JpaRepository<QRCodeRastreamento, Integer> {

    
    Optional<QRCodeRastreamento> findByCodigoRastreamento(String codigoRastreamento);

    
    @Query("SELECT q FROM QRCodeRastreamento q WHERE q.itemPedido.pedido.id = :idPedido")
    List<QRCodeRastreamento> findByPedidoId(@Param("idPedido") Integer idPedido);

  
    Optional<QRCodeRastreamento> findByItemPedido_Id(Integer idItemPedido);
}
