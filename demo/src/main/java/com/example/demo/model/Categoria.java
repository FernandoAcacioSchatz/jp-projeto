package com.example.demo.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa uma categoria de produtos
 */
@Entity
@Table(name = "tb_categoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class Categoria extends Auditable implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer id;

    @NotBlank(message = "O nome da categoria não pode estar vazio.")
    @Size(min = 3, max = 50, message = "O nome deve ter entre 3 e 50 caracteres.")
    @Column(unique = true, nullable = false, length = 50)
    private String nome;
    
    @NotBlank(message = "A descrição da categoria não pode estar vazia.")
    @Size(min = 10, max = 255, message = "A descrição deve ter entre 10 e 255 caracteres.")
    @Column(nullable = false, length = 255)
    private String descricao;

    @JsonIgnore // Evita loop infinito na serialização JSON
    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY)
    private List<Produto> produtos = new ArrayList<>();

    /**
     * Retorna a quantidade de produtos nesta categoria
     */
    public int getQuantidadeProdutos() {
        return produtos != null ? produtos.size() : 0;
    }
}
