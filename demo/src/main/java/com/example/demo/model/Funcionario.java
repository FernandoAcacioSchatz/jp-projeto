package com.example.demo.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "tb_fornecedores")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class Funcionario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fornecedor")
    private Integer id;

    @NotBlank
    @Column(name = "nome", nullable = false)
    private String nome;

    @NotBlank
    @Email
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @NotBlank
    @Size(min = 6)
    @Column(name = "senha", nullable = false)
    private String senha;

    @Column(name = "telefone")
    private String telefone;

    @NotBlank
    @Column(name = "cnpj", unique = true, nullable = false)
    private String cnpj;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadosBrasileiros estado;

    @NotBlank
    @Column(name = "cidade", nullable = false)
    private String cidade;

    @NotBlank
    @Column(name = "cep", nullable = false)
    private String cep;

    @NotBlank
    @Column(name = "bairro", nullable = false)
    private String bairro;

    @NotBlank
    @Column(name = "rua", nullable = false)
    private String rua;

    @NotBlank
    @Column(name = "n_empresa", nullable = false)
    private String numeroEmpresa;

}
