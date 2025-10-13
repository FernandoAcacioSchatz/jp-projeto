package com.example.demo.repository;

import com.example.demo.model.Usuario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> { 
    
     // Spring Data vai criar a query: "SELECT u FROM Usuario u WHERE u.email = ?1"
    Optional<Usuario> findByEmail(String email);

    // Spring Data vai criar a query: "SELECT u FROM Usuario u WHERE u.cpf = ?1"
    Optional<Usuario> findByCpf(String cpf);
}