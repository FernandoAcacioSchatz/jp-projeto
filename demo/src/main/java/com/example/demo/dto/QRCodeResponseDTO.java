package com.example.demo.dto;

import com.example.demo.model.QRCodeRastreamento;

/**
 * DTO de resposta para QR Code de rastreamento
 */
public record QRCodeResponseDTO(
    String codigoRastreamento,
    String qrcodeBase64,
    String urlRastreamento,
    String urlDownload
) {
    public QRCodeResponseDTO(QRCodeRastreamento qrCode) {
        this(
            qrCode.getCodigoRastreamento(),
            qrCode.getQrcodeBase64(),
            "/rastreamento/" + qrCode.getCodigoRastreamento(),
            "/rastreamento/" + qrCode.getCodigoRastreamento() + "/download"
        );
    }
}
