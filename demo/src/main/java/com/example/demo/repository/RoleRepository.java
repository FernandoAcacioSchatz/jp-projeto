package com.example.demo.repository;
import com.example.demo.model.Role;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer>{

    Optional<Role> findByNomePapel(String nomePapel);
}
