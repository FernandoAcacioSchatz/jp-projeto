package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tb_users") // "user" é palavra reservada em alguns bancos
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User implements Serializable {
    // NOTA: Depois vamos fazer ela implementar 'UserDetails' do Spring Security,
    // mas por enquanto, vamos mantê-la simples.

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long id; // Long é o tipo mais comum para IDs de usuário

    @NotBlank
    @Email
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @NotBlank
    @Column(name = "senha", nullable = false)
    private String senha;

    @ManyToMany(fetch = FetchType.EAGER) // EAGER é importante para o Spring Security
    @JoinTable(name = "tb_user_roles", // Nome da tabela de associação
            joinColumns = @JoinColumn(name = "user_id"), // Chave estrangeira para User
            inverseJoinColumns = @JoinColumn(name = "role_id") // Chave estrangeira para Role
    )
    private Set<Role> roles = new HashSet<>();

}