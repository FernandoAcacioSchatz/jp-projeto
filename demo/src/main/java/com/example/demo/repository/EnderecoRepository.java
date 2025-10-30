package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Endereco;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Integer> {

    /**
     * Busca todos os endereços de um cliente específico
     */
    List<Endereco> findByCliente_IdCliente(Integer idCliente);

    /**
     * Busca o endereço principal de um cliente
     */
    Optional<Endereco> findByCliente_IdClienteAndIsPrincipalTrue(Integer idCliente);

    /**
     * Verifica se um cliente tem um endereço principal
     */
    @Query("SELECT COUNT(e) > 0 FROM Endereco e WHERE e.cliente.idCliente = :idCliente AND e.isPrincipal = true")
    boolean existsEnderecoPrincipalByCliente(@Param("idCliente") Integer idCliente);
}
