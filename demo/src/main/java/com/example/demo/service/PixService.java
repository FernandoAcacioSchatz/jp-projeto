package com.example.demo.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exception.RegraNegocioException;
import com.example.demo.model.PagamentoPix;
import com.example.demo.model.Pedido;
import com.example.demo.model.StatusPagamentoPix;
import com.example.demo.repository.PagamentoPixRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

@Service
public class PixService {

    private final PagamentoPixRepository pagamentoPixRepository;

    @Value("${pix.chave:12345678000190}")
    private String pixChave;

    @Value("${pix.nomeBeneficiario:Loja Virtual LTDA}")
    private String nomeBeneficiario;

    @Value("${pix.cidade:Sao Paulo}")
    private String cidade;

    @Value("${pix.minutos-expiracao:15}")
    private Integer minutosExpiracao;

    public PixService(PagamentoPixRepository pagamentoPixRepository) {
        this.pagamentoPixRepository = pagamentoPixRepository;
    }

    /**
     * 💰 Gera pagamento PIX completo para um pedido
     * Cria código PIX EMV, QR Code e salva no banco
     */
    @Transactional
    public PagamentoPix gerarPagamentoPix(Pedido pedido) {

        // Valida se o pedido já possui PIX
        if (pagamentoPixRepository.findByPedido_Id(pedido.getId()).isPresent()) {
            throw new RegraNegocioException("Este pedido já possui um pagamento PIX gerado.");
        }

        // Gera o código PIX EMV (formato Banco Central)
        String codigoPix = gerarCodigoPixEMV(pedido);

        // Gera o QR Code da string PIX
        String qrCodeBase64;
        try {
            qrCodeBase64 = gerarQRCodePix(codigoPix);
        } catch (WriterException | IOException e) {
            throw new RegraNegocioException("Erro ao gerar QR Code do PIX: " + e.getMessage());
        }

        // Define expiração (padrão: 15 minutos)
        LocalDateTime dataExpiracao = LocalDateTime.now().plusMinutes(minutosExpiracao);

        // Cria entidade PagamentoPix
        PagamentoPix pagamentoPix = new PagamentoPix();
        pagamentoPix.setPedido(pedido);
        pagamentoPix.setCodigoPix(codigoPix);
        pagamentoPix.setQrCodePix(qrCodeBase64);
        pagamentoPix.setValorPix(pedido.getValorTotal());
        pagamentoPix.setDataExpiracao(dataExpiracao);
        pagamentoPix.setStatusPagamento(StatusPagamentoPix.PENDENTE);

        return pagamentoPixRepository.save(pagamentoPix);
    }

    /**
     * 🔍 Busca pagamento PIX por ID do pedido
     */
    public PagamentoPix buscarPorPedido(Integer idPedido) {
        return pagamentoPixRepository.findByPedido_Id(idPedido)
                .orElseThrow(() -> new RegraNegocioException(
                        "Nenhum pagamento PIX encontrado para este pedido."));
    }

    /**
     * ✅ Confirma um pagamento PIX (webhook)
     */
    @Transactional
    public PagamentoPix confirmarPagamento(Integer idPedido, String txid) {

        PagamentoPix pagamentoPix = buscarPorPedido(idPedido);

        // Valida se está expirado
        if (pagamentoPix.isExpirado()) {
            pagamentoPix.expirarPagamento();
            pagamentoPixRepository.save(pagamentoPix);
            throw new RegraNegocioException("Este PIX está expirado e não pode ser confirmado.");
        }

        // Valida se já foi confirmado
        if (pagamentoPix.getStatusPagamento() == StatusPagamentoPix.CONFIRMADO) {
            throw new RegraNegocioException("Este PIX já foi confirmado anteriormente.");
        }

        // Confirma o pagamento
        pagamentoPix.confirmarPagamento(txid);

        // Atualiza status do pedido
        Pedido pedido = pagamentoPix.getPedido();
        pedido.marcarComoPago();

        return pagamentoPixRepository.save(pagamentoPix);
    }

    /**
     * ⏰ Verifica e expira PIX vencidos
     */
    @Transactional
    public void expirarPixVencidos() {
        // TODO: Implementar job agendado que verifica PIX expirados
        // e atualiza status para EXPIRADO
    }

    /**
     * 📝 Gera código PIX no formato EMV (Banco Central)
     * 
     * ⚠️ SIMPLIFICADO: Esta é uma versão básica para demonstração.
     * Em produção, use uma biblioteca especializada ou API do seu banco!
     * 
     * Formato EMV (simplificado):
     * - Payload Format Indicator
     * - Merchant Account Information
     * - Merchant Category Code
     * - Transaction Currency
     * - Transaction Amount
     * - Country Code
     * - Merchant Name
     * - Merchant City
     * - CRC16
     */
    private String gerarCodigoPixEMV(Pedido pedido) {
        
        StringBuilder emv = new StringBuilder();

        // 00 - Payload Format Indicator
        emv.append("00020126");

        // 26 - Merchant Account Information (Chave PIX)
        String merchantAccount = String.format("0014BR.GOV.BCB.PIX01%02d%s",
                pixChave.length(), pixChave);
        emv.append(String.format("26%02d%s", merchantAccount.length(), merchantAccount));

        // 52 - Merchant Category Code
        emv.append("52040000");

        // 53 - Transaction Currency (BRL = 986)
        emv.append("5303986");

        // 54 - Transaction Amount
        String valor = pedido.getValorTotal().setScale(2).toString();
        emv.append(String.format("54%02d%s", valor.length(), valor));

        // 58 - Country Code
        emv.append("5802BR");

        // 59 - Merchant Name
        emv.append(String.format("59%02d%s", nomeBeneficiario.length(), nomeBeneficiario));

        // 60 - Merchant City
        emv.append(String.format("60%02d%s", cidade.length(), cidade));

        // 62 - Additional Data Field (ID da transação)
        String txid = "PEDIDO" + String.format("%06d", pedido.getId());
        String additionalData = String.format("05%02d%s", txid.length(), txid);
        emv.append(String.format("62%02d%s", additionalData.length(), additionalData));

        // 63 - CRC16 (simplificado - em produção calcular CRC real)
        emv.append("6304");
        String crc = calcularCRC16(emv.toString());
        emv.append(crc);

        return emv.toString();
    }

    /**
     * 🖼️ Gera QR Code do código PIX em Base64
     */
    private String gerarQRCodePix(String codigoPix) throws WriterException, IOException {
        
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(
                codigoPix,
                BarcodeFormat.QR_CODE,
                350, // largura
                350  // altura
        );

        BufferedImage imagem = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // Converte para Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(imagem, "PNG", baos);
        byte[] bytes = baos.toByteArray();
        String base64 = Base64.getEncoder().encodeToString(bytes);

        return "data:image/png;base64," + base64;
    }

    /**
     * 🔢 Calcula CRC16 para código PIX EMV
     * 
     * ⚠️ SIMPLIFICADO: Em produção, use implementação completa do CRC16-CCITT
     */
    private String calcularCRC16(String payload) {
        // Implementação simplificada - retorna valor fixo
        // Em produção, implementar algoritmo CRC16-CCITT completo
        int crc = 0xFFFF;
        byte[] bytes = payload.getBytes();

        for (byte b : bytes) {
            crc ^= (b & 0xFF) << 8;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ 0x1021;
                } else {
                    crc = crc << 1;
                }
            }
        }

        return String.format("%04X", crc & 0xFFFF);
    }
}
