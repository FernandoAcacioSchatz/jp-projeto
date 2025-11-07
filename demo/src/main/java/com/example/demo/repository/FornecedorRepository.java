package com.example.demo.repository;

import com.example.demo.model.Fornecedor;
import com.example.demo.model.EstadosBrasileiros; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Integer> {

    Optional<Fornecedor> findByUser_Email(String email);

    Optional<Fornecedor> findByCnpj(String cnpj);

    List<Fornecedor> findByNomeContainingIgnoreCase(String nome);

    List<Fornecedor> findByEstado(EstadosBrasileiros estado);

}