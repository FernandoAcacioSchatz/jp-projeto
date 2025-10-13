package com.example.demo.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_usuarios")
@Getter
@Setter
@EqualsAndHashCode(of = "idUsuario")
@AllArgsConstructor
@NoArgsConstructor
public class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @NotBlank(message = "O nome é Obrigatório")
    @Column(nullable = false)
    private String nomeUsuario;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8)
    @Column(nullable = false)
    private String senha;

    @NotBlank(message = "O email é obtigatório")
    @Email(message = "O formato do email é invalido")
    @Column(unique = true, nullable = false)
    private String email;

    private String telefone;

    @NotBlank(message = "O CPF é Obrigatório")
    @Size(min = 11, max = 14)
    @Column(unique = true, nullable = false)
    private String cpf;

    @OneToMany(mappedBy = "usuario", // Mapeado pelo atributo "usuario" na classe Pedido
            fetch = FetchType.LAZY
    )
    private List<Pedido> pedidos = new ArrayList<>();

}
