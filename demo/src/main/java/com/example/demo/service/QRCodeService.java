package com.example.demo.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.model.Cliente;
import com.example.demo.model.Endereco;
import com.example.demo.model.Fornecedor;
import com.example.demo.model.ItemPedido;
import com.example.demo.model.Pedido;
import com.example.demo.model.Produto;
import com.example.demo.model.QRCodeRastreamento;
import com.example.demo.repository.QRCodeRastreamentoRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

@Service
public class QRCodeService {

    private final QRCodeRastreamentoRepository qrCodeRepository;

    @Value("${qrcode.diretorio:qrcodes}")
    private String diretorioQRCodes;

    public QRCodeService(QRCodeRastreamentoRepository qrCodeRepository) {
        this.qrCodeRepository = qrCodeRepository;
    }

    public QRCodeRastreamento gerarQRCodeParaItem(ItemPedido itemPedido) {

        
        Pedido pedido = itemPedido.getPedido();
        Cliente cliente = pedido.getCliente();
        Produto produto = itemPedido.getProduto();
        Fornecedor fornecedor = produto.getFornecedor();
        Endereco enderecoCliente = pedido.getEnderecoEntrega();

        String codigoRastreamento = gerarCodigoRastreamento(pedido, itemPedido);

        String conteudo = montarConteudoQRCode(
                fornecedor, produto, cliente, enderecoCliente,
                pedido, itemPedido, codigoRastreamento);

        try {
            BufferedImage imagemQRCode = gerarImagemQRCode(conteudo);

            String qrcodeBase64 = converterImagemParaBase64(imagemQRCode);

            String caminhoArquivo = salvarArquivoFisico(imagemQRCode, codigoRastreamento);

            QRCodeRastreamento qrCode = new QRCodeRastreamento();
            qrCode.setItemPedido(itemPedido);
            qrCode.setCodigoRastreamento(codigoRastreamento);
            qrCode.setConteudoQRCode(conteudo);
            qrCode.setQrcodeBase64(qrcodeBase64);
            qrCode.setQrcodeCaminhoArquivo(caminhoArquivo);

            return qrCodeRepository.save(qrCode);

        } catch (WriterException | IOException e) {
            throw new RuntimeException("Erro ao gerar QR Code: " + e.getMessage(), e);
        }
    }

    private String gerarCodigoRastreamento(Pedido pedido, ItemPedido item) {
        return String.format("PED%04d-ITEM%03d", pedido.getId(), item.getId());
    }

    private String montarConteudoQRCode(
            Fornecedor fornecedor,
            Produto produto,
            Cliente cliente,
            Endereco enderecoCliente,
            Pedido pedido,
            ItemPedido itemPedido,
            String codigoRastreamento) {
        StringBuilder sb = new StringBuilder();

        sb.append("═══════════════════════════════════════\n");
        sb.append("       RASTREAMENTO DE ENCOMENDA       \n");
        sb.append("═══════════════════════════════════════\n\n");

        sb.append("CÓDIGO DE RASTREAMENTO:\n");
        sb.append("   ").append(codigoRastreamento).append("\n\n");

        sb.append("FORNECEDOR (REMETENTE):\n");
        sb.append("   Nome: ").append(fornecedor.getNome()).append("\n");
        sb.append("   CNPJ: ").append(formatarCNPJ(fornecedor.getCnpj())).append("\n");
        sb.append("   Telefone: ").append(fornecedor.getTelefone()).append("\n");
        sb.append("   Estado: ").append(fornecedor.getEstado().name()).append("\n");
        sb.append("\n");

        sb.append("PRODUTO:\n");
        sb.append("   Nome: ").append(produto.getNome()).append("\n");
        sb.append("   Descrição: ").append(produto.getDescricao()).append("\n");
        sb.append("   Quantidade: ").append(itemPedido.getQuantidade()).append(" un\n");
        sb.append("\n");

        sb.append("CLIENTE (DESTINATÁRIO):\n");
        sb.append("   Nome: ").append(cliente.getNomeCliente()).append("\n");
        sb.append("   CPF: ").append(formatarCPF(cliente.getCpf())).append("\n");
        sb.append("   Telefone: ").append(cliente.getTelefone()).append("\n");
        if (enderecoCliente != null) {
            sb.append("   Endereço: ").append(enderecoCliente.getEnderecoCompleto()).append("\n");
        }
        sb.append("\n");

        sb.append("INFORMAÇÕES DO PEDIDO:\n");
        sb.append("   Nº Pedido: ").append(pedido.getId()).append("\n");
        sb.append("   Nº Item: ").append(itemPedido.getId()).append("\n");
        sb.append("   Data: ").append(pedido.getDataPedido().format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        sb.append("   Status: ").append(pedido.getStatus().name()).append("\n");
        sb.append("\n");

        sb.append("═══════════════════════════════════════\n");
        sb.append("   Acesse: /rastreamento/").append(codigoRastreamento).append("\n");
        sb.append("═══════════════════════════════════════\n");

        return sb.toString();
    }

    private BufferedImage gerarImagemQRCode(String conteudo) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(
                conteudo,
                BarcodeFormat.QR_CODE,
                400, 
                400 
        );
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    private String converterImagemParaBase64(BufferedImage imagem) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(imagem, "PNG", baos);
        byte[] bytes = baos.toByteArray();
        String base64 = Base64.getEncoder().encodeToString(bytes);
        return "data:image/png;base64," + base64;
    }

    private String salvarArquivoFisico(BufferedImage imagem, String codigoRastreamento) throws IOException {

        Path diretorio = Paths.get(diretorioQRCodes);
        if (!Files.exists(diretorio)) {
            Files.createDirectories(diretorio);
        }

        String nomeArquivo = codigoRastreamento + ".png";
        Path caminhoCompleto = diretorio.resolve(nomeArquivo);

        File arquivo = caminhoCompleto.toFile();
        ImageIO.write(imagem, "PNG", arquivo);

        return caminhoCompleto.toString();
    }

    private String formatarCPF(String cpf) {
        if (cpf == null || cpf.length() != 11)
            return cpf;
        return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    private String formatarCNPJ(String cnpj) {
        if (cnpj == null || cnpj.length() != 14)
            return cnpj;
        return cnpj.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
    }
}
