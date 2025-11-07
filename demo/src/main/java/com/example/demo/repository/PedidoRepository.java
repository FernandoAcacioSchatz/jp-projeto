package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Pedido;
import com.example.demo.model.StatusPedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

   
    List<Pedido> findByCliente_IdClienteOrderByDataPedidoDesc(Integer idCliente);

    
    Page<Pedido> findByCliente_IdClienteOrderByDataPedidoDesc(Integer idCliente, Pageable pageable);

    
    List<Pedido> findByStatusOrderByDataPedidoDesc(StatusPedido status);

    List<Pedido> findByCliente_IdClienteAndStatusOrderByDataPedidoDesc(Integer idCliente, StatusPedido status);

    @Query("SELECT DISTINCT p FROM Pedido p JOIN p.itens i WHERE i.produto.fornecedor.idFornecedor = :idFornecedor ORDER BY p.dataPedido DESC")
    List<Pedido> findPedidosPorFornecedor(@Param("idFornecedor") Integer idFornecedor);

    
    @Query("SELECT DISTINCT p FROM Pedido p JOIN p.itens i WHERE i.produto.fornecedor.idFornecedor = :idFornecedor ORDER BY p.dataPedido DESC")
    Page<Pedido> findPedidosPorFornecedor(@Param("idFornecedor") Integer idFornecedor, Pageable pageable);
}
