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

    /**
     * Busca QR Code pelo código de rastreamento
     */
    Optional<QRCodeRastreamento> findByCodigoRastreamento(String codigoRastreamento);

    /**
     * Busca todos os QR Codes de um pedido
     */
    @Query("SELECT q FROM QRCodeRastreamento q WHERE q.itemPedido.pedido.id = :idPedido")
    List<QRCodeRastreamento> findByPedidoId(@Param("idPedido") Integer idPedido);

    /**
     * Busca QR Code por item do pedido
     */
    Optional<QRCodeRastreamento> findByItemPedido_Id(Integer idItemPedido);
}
