package com.example.demo.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_produto")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produto")
    private Integer id;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @NotBlank
    @Column(nullable = false)
    private String descricao;

    @NotNull(message = "O preço não pode ser nulo.")
    @Positive(message = "O preço deve ser um valor positivo.")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @NotNull(message = "O estoque não pode ser nulo.")
    @PositiveOrZero(message = "O estoque deve ser zero ou maior.")
    @Column(nullable = false)
    private Integer estoque;

    @ManyToOne(fetch = FetchType.LAZY) // Muitos produtos para UMA categoria
    @JoinColumn(name = "id_categoria", nullable = false) // Garante que todo produto tenha uma categoria
    private Categoria categoria;

    @ManyToMany
    @JoinTable(name = "produto_fornecedor", // Nome da tabela de junção
            joinColumns = @JoinColumn(name = "id_produto"), // Chave estrangeira para Produto
            inverseJoinColumns = @JoinColumn(name = "id_fornecedor") // Chave estrangeira para Fornecedor
    )
    private Set<Fornecedor> fornecedores = new HashSet<>();
}
