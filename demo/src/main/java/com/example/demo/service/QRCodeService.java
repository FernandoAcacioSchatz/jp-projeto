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

    /**
     * Gera QR Code completo para um item do pedido
     * Salva tanto em Base64 quanto em arquivo físico
     */
    public QRCodeRastreamento gerarQRCodeParaItem(ItemPedido itemPedido) {
        
        // 1. Coleta todas as informações
        Pedido pedido = itemPedido.getPedido();
        Cliente cliente = pedido.getCliente();
        Produto produto = itemPedido.getProduto();
        Fornecedor fornecedor = produto.getFornecedor();
        Endereco enderecoCliente = pedido.getEnderecoEntrega();

        // 2. Gera código de rastreamento único
        String codigoRastreamento = gerarCodigoRastreamento(pedido, itemPedido);

        // 3. Monta o conteúdo do QR Code
        String conteudo = montarConteudoQRCode(
            fornecedor, produto, cliente, enderecoCliente, 
            pedido, itemPedido, codigoRastreamento
        );

        // 4. Gera a imagem do QR Code
        try {
            BufferedImage imagemQRCode = gerarImagemQRCode(conteudo);

            // 5. Converte para Base64
            String qrcodeBase64 = converterImagemParaBase64(imagemQRCode);

            // 6. Salva arquivo físico
            String caminhoArquivo = salvarArquivoFisico(imagemQRCode, codigoRastreamento);

            // 7. Cria e salva no banco
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

    /**
     * Gera código de rastreamento único no formato: PED0001-ITEM001
     */
    private String gerarCodigoRastreamento(Pedido pedido, ItemPedido item) {
        return String.format("PED%04d-ITEM%03d", pedido.getId(), item.getId());
    }

    /**
     * Monta o texto completo que será codificado no QR Code
     */
    private String montarConteudoQRCode(
        Fornecedor fornecedor,
        Produto produto,
        Cliente cliente,
        Endereco enderecoCliente,
        Pedido pedido,
        ItemPedido itemPedido,
        String codigoRastreamento
    ) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("═══════════════════════════════════════\n");
        sb.append("       RASTREAMENTO DE ENCOMENDA       \n");
        sb.append("═══════════════════════════════════════\n\n");
        
        sb.append("📦 CÓDIGO DE RASTREAMENTO:\n");
        sb.append("   ").append(codigoRastreamento).append("\n\n");
        
        sb.append("🏢 FORNECEDOR (REMETENTE):\n");
        sb.append("   Nome: ").append(fornecedor.getNome()).append("\n");
        sb.append("   CNPJ: ").append(formatarCNPJ(fornecedor.getCnpj())).append("\n");
        sb.append("   Telefone: ").append(fornecedor.getTelefone()).append("\n");
        sb.append("   Estado: ").append(fornecedor.getEstado().name()).append("\n");
        sb.append("\n");
        
        sb.append("📦 PRODUTO:\n");
        sb.append("   Nome: ").append(produto.getNome()).append("\n");
        sb.append("   Descrição: ").append(produto.getDescricao()).append("\n");
        sb.append("   Quantidade: ").append(itemPedido.getQuantidade()).append(" un\n");
        sb.append("\n");
        
        sb.append("👤 CLIENTE (DESTINATÁRIO):\n");
        sb.append("   Nome: ").append(cliente.getNomeCliente()).append("\n");
        sb.append("   CPF: ").append(formatarCPF(cliente.getCpf())).append("\n");
        sb.append("   Telefone: ").append(cliente.getTelefone()).append("\n");
        if (enderecoCliente != null) {
            sb.append("   Endereço: ").append(enderecoCliente.getEnderecoCompleto()).append("\n");
        }
        sb.append("\n");
        
        sb.append("🔢 INFORMAÇÕES DO PEDIDO:\n");
        sb.append("   Nº Pedido: ").append(pedido.getId()).append("\n");
        sb.append("   Nº Item: ").append(itemPedido.getId()).append("\n");
        sb.append("   Data: ").append(pedido.getDataPedido().format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        )).append("\n");
        sb.append("   Status: ").append(pedido.getStatus().name()).append("\n");
        sb.append("\n");
        
        sb.append("═══════════════════════════════════════\n");
        sb.append("   Acesse: /rastreamento/").append(codigoRastreamento).append("\n");
        sb.append("═══════════════════════════════════════\n");
        
        return sb.toString();
    }

    /**
     * Gera a imagem do QR Code
     */
    private BufferedImage gerarImagemQRCode(String conteudo) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(
            conteudo, 
            BarcodeFormat.QR_CODE, 
            400,  // largura
            400   // altura
        );
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    /**
     * Converte BufferedImage para Base64
     */
    private String converterImagemParaBase64(BufferedImage imagem) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(imagem, "PNG", baos);
        byte[] bytes = baos.toByteArray();
        String base64 = Base64.getEncoder().encodeToString(bytes);
        return "data:image/png;base64," + base64;
    }

    /**
     * Salva o QR Code como arquivo físico
     */
    private String salvarArquivoFisico(BufferedImage imagem, String codigoRastreamento) throws IOException {
        
        // Cria o diretório se não existir
        Path diretorio = Paths.get(diretorioQRCodes);
        if (!Files.exists(diretorio)) {
            Files.createDirectories(diretorio);
        }

        // Define o caminho completo do arquivo
        String nomeArquivo = codigoRastreamento + ".png";
        Path caminhoCompleto = diretorio.resolve(nomeArquivo);

        // Salva a imagem
        File arquivo = caminhoCompleto.toFile();
        ImageIO.write(imagem, "PNG", arquivo);

        return caminhoCompleto.toString();
    }

    /**
     * Formata CPF para exibição
     */
    private String formatarCPF(String cpf) {
        if (cpf == null || cpf.length() != 11) return cpf;
        return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    /**
     * Formata CNPJ para exibição
     */
    private String formatarCNPJ(String cnpj) {
        if (cnpj == null || cnpj.length() != 14) return cnpj;
        return cnpj.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
    }
}
