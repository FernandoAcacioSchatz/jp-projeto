package com.example.demo.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa um endereço associado a um cliente
 */
@Entity
@Table(name = "tb_endereco")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idEndereco", callSuper = false)
@Builder
public class Endereco extends Auditable implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_endereco")
    private Integer idEndereco;

    @NotBlank(message = "O apelido do endereço é obrigatório (ex: Casa, Trabalho)")
    @Size(min = 3, max = 50, message = "O apelido deve ter entre 3 e 50 caracteres")
    @Column(nullable = false, length = 50)
    private String apelido; // Ex: "Casa", "Trabalho", "Casa da Mãe"

    @NotBlank(message = "O CEP é obrigatório")
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP inválido. Use o formato: 12345-678")
    @Column(nullable = false, length = 9)
    private String cep;

    @NotBlank(message = "A rua é obrigatória")
    @Size(min = 3, max = 100, message = "A rua deve ter entre 3 e 100 caracteres")
    @Column(nullable = false, length = 100)
    private String rua;

    @NotBlank(message = "O número é obrigatório")
    @Size(max = 10, message = "O número deve ter no máximo 10 caracteres")
    @Column(nullable = false, length = 10)
    private String numero;

    @Size(max = 100, message = "O complemento deve ter no máximo 100 caracteres")
    @Column(length = 100)
    private String complemento; // Apto, bloco, etc.

    @NotBlank(message = "O bairro é obrigatório")
    @Size(min = 3, max = 50, message = "O bairro deve ter entre 3 e 50 caracteres")
    @Column(nullable = false, length = 50)
    private String bairro;

    @NotBlank(message = "A cidade é obrigatória")
    @Size(min = 3, max = 50, message = "A cidade deve ter entre 3 e 50 caracteres")
    @Column(nullable = false, length = 50)
    private String cidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 2)
    private EstadosBrasileiros estado;

    @Column(name = "is_principal", nullable = false)
    private Boolean isPrincipal = false; // Endereço principal do cliente

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = true)
    private Cliente cliente;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_fornecedor", nullable = true)
    private Fornecedor fornecedor;

    /**
     * Retorna endereço formatado para exibição
     */
    public String getEnderecoCompleto() {
        StringBuilder sb = new StringBuilder();
        sb.append(rua).append(", ").append(numero);
        if (complemento != null && !complemento.isEmpty()) {
            sb.append(" - ").append(complemento);
        }
        sb.append(", ").append(bairro)
          .append(", ").append(cidade)
          .append(" - ").append(estado)
          .append(", CEP: ").append(cep);
        return sb.toString();
    }
}
