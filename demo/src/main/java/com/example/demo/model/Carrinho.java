package com.example.demo.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_carrinho")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Carrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrinho")
    private Integer id;

    @NotNull
    @Column(nullable = false)
    private Integer quantidade;

    @NotBlank
    @Column(nullable = false)
    private String descricao;

    @OneToMany(mappedBy = "carrinho", // A relação é gerenciada pelo atributo "carrinho" na classe ItemCarrinho
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ItemCarrinho> itens = new HashSet<>();

}
