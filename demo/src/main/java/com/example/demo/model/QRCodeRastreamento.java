package com.example.demo.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa um QR Code de rastreamento para um item do pedido
 */
@Entity
@Table(name = "tb_qrcode_rastreamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class QRCodeRastreamento extends Auditable implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_qrcode")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_item_pedido", nullable = false, unique = true)
    private ItemPedido itemPedido;

    @Column(name = "codigo_rastreamento", unique = true, nullable = false, length = 50)
    private String codigoRastreamento;

    @Column(name = "qrcode_base64", columnDefinition = "LONGTEXT")
    private String qrcodeBase64; // Imagem em Base64 para retornar na API

    @Column(name = "qrcode_caminho_arquivo", length = 255)
    private String qrcodeCaminhoArquivo; // Caminho do arquivo físico

    @Column(name = "conteudo_qrcode", columnDefinition = "TEXT")
    private String conteudoQRCode; // Texto completo que está codificado

    @Column(name = "data_geracao", nullable = false)
    private LocalDateTime dataGeracao;

    @PrePersist
    protected void onCreate() {
        if (this.dataGeracao == null) {
            this.dataGeracao = LocalDateTime.now();
        }
    }
}
