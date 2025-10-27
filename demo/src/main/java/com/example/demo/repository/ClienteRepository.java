package com.example.demo.repository;

import com.example.demo.model.Cliente;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByUser_Email(String email);

    Optional<Cliente> findByCpf(String cpf);

    List<Cliente> findByNomeClienteContainingIgnoreCase(String nome);

}