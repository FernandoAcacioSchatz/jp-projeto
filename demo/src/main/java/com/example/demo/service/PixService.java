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

    @Transactional
    public PagamentoPix gerarPagamentoPix(Pedido pedido) {

        if (pagamentoPixRepository.findByPedido_Id(pedido.getId()).isPresent()) {
            throw new RegraNegocioException("Este pedido já possui um pagamento PIX gerado.");
        }

        String codigoPix = gerarCodigoPixEMV(pedido);

        String qrCodeBase64;
        try {
            qrCodeBase64 = gerarQRCodePix(codigoPix);
        } catch (WriterException | IOException e) {
            throw new RegraNegocioException("Erro ao gerar QR Code do PIX: " + e.getMessage());
        }

        LocalDateTime dataExpiracao = LocalDateTime.now().plusMinutes(minutosExpiracao);

        PagamentoPix pagamentoPix = new PagamentoPix();
        pagamentoPix.setPedido(pedido);
        pagamentoPix.setCodigoPix(codigoPix);
        pagamentoPix.setQrCodePix(qrCodeBase64);
        pagamentoPix.setValorPix(pedido.getValorTotal());
        pagamentoPix.setDataExpiracao(dataExpiracao);
        pagamentoPix.setStatusPagamento(StatusPagamentoPix.PENDENTE);

        return pagamentoPixRepository.save(pagamentoPix);
    }

    public PagamentoPix buscarPorPedido(Integer idPedido) {
        return pagamentoPixRepository.findByPedido_Id(idPedido)
                .orElseThrow(() -> new RegraNegocioException(
                        "Nenhum pagamento PIX encontrado para este pedido."));
    }

    @Transactional
    public PagamentoPix confirmarPagamento(Integer idPedido, String txid) {

        PagamentoPix pagamentoPix = buscarPorPedido(idPedido);

        if (pagamentoPix.isExpirado()) {
            pagamentoPix.expirarPagamento();
            pagamentoPixRepository.save(pagamentoPix);
            throw new RegraNegocioException("Este PIX está expirado e não pode ser confirmado.");
        }

        if (pagamentoPix.getStatusPagamento() == StatusPagamentoPix.CONFIRMADO) {
            throw new RegraNegocioException("Este PIX já foi confirmado anteriormente.");
        }

        pagamentoPix.confirmarPagamento(txid);

        Pedido pedido = pagamentoPix.getPedido();
        pedido.marcarComoPago();

        return pagamentoPixRepository.save(pagamentoPix);
    }

    @Transactional
    public void expirarPixVencidos() {

    }

    private String gerarCodigoPixEMV(Pedido pedido) {

        StringBuilder emv = new StringBuilder();

        emv.append("00020126");

        String merchantAccount = String.format("0014BR.GOV.BCB.PIX01%02d%s",
                pixChave.length(), pixChave);
        emv.append(String.format("26%02d%s", merchantAccount.length(), merchantAccount));

        emv.append("52040000");

        emv.append("5303986");

        String valor = pedido.getValorTotal().setScale(2).toString();
        emv.append(String.format("54%02d%s", valor.length(), valor));

        emv.append("5802BR");

        emv.append(String.format("59%02d%s", nomeBeneficiario.length(), nomeBeneficiario));

        emv.append(String.format("60%02d%s", cidade.length(), cidade));

        String txid = "PEDIDO" + String.format("%06d", pedido.getId());
        String additionalData = String.format("05%02d%s", txid.length(), txid);
        emv.append(String.format("62%02d%s", additionalData.length(), additionalData));

        emv.append("6304");
        String crc = calcularCRC16(emv.toString());
        emv.append(crc);

        return emv.toString();
    }

    private String gerarQRCodePix(String codigoPix) throws WriterException, IOException {

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(
                codigoPix,
                BarcodeFormat.QR_CODE,
                350,
                350);

        BufferedImage imagem = MatrixToImageWriter.toBufferedImage(bitMatrix);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(imagem, "PNG", baos);
        byte[] bytes = baos.toByteArray();
        String base64 = Base64.getEncoder().encodeToString(bytes);

        return "data:image/png;base64," + base64;
    }

    private String calcularCRC16(String payload) {

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