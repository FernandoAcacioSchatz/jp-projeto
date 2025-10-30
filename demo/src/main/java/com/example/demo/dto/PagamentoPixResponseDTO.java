package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.demo.model.PagamentoPix;
import com.example.demo.model.StatusPagamentoPix;

/**
 * DTO de resposta com dados do pagamento PIX
 */
public record PagamentoPixResponseDTO(
        Integer id,
        Integer idPedido,
        String codigoPix,
        String qrCodePix,
        BigDecimal valorPix,
        LocalDateTime dataExpiracao,
        StatusPagamentoPix statusPagamento,
        LocalDateTime dataConfirmacao,
        String txid,
        Boolean isExpirado,
        String urlCheckStatus
) {
    public PagamentoPixResponseDTO(PagamentoPix pix) {
        this(
                pix.getId(),
                pix.getPedido().getId(),
                pix.getCodigoPix(),
                pix.getQrCodePix(),
                pix.getValorPix(),
                pix.getDataExpiracao(),
                pix.getStatusPagamento(),
                pix.getDataConfirmacao(),
                pix.getTxid(),
                pix.isExpirado(),
                "/pagamento/pix/" + pix.getPedido().getId() + "/status"
        );
    }
}
