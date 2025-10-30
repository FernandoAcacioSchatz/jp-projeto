package com.example.demo.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.QRCodeResponseDTO;
import com.example.demo.exception.RegraNegocioException;
import com.example.demo.model.QRCodeRastreamento;
import com.example.demo.repository.QRCodeRastreamentoRepository;

@RestController
@RequestMapping("/rastreamento")
public class RastreamentoController {

    private final QRCodeRastreamentoRepository qrCodeRepository;

    public RastreamentoController(QRCodeRastreamentoRepository qrCodeRepository) {
        this.qrCodeRepository = qrCodeRepository;
    }

    /**
     * 🔍 GET /rastreamento/{codigo}
     * Retorna informações de rastreamento pelo código
     */
    @GetMapping("/{codigo}")
    public ResponseEntity<QRCodeResponseDTO> buscarPorCodigo(@PathVariable String codigo) {
        
        QRCodeRastreamento qrCode = qrCodeRepository.findByCodigoRastreamento(codigo)
                .orElseThrow(() -> new RegraNegocioException(
                        "Código de rastreamento não encontrado: " + codigo));

        return ResponseEntity.ok(new QRCodeResponseDTO(qrCode));
    }

    /**
     * 📦 GET /rastreamento/pedido/{idPedido}
     * Lista todos os QR Codes de rastreamento de um pedido
     * Requer autenticação (CLIENTE ou ADMIN)
     */
    @GetMapping("/pedido/{idPedido}")
    @PreAuthorize("hasAnyRole('ROLE_CLIENTE', 'ROLE_ADMIN')")
    public ResponseEntity<List<QRCodeResponseDTO>> listarPorPedido(@PathVariable Integer idPedido) {
        
        List<QRCodeRastreamento> qrCodes = qrCodeRepository.findByPedidoId(idPedido);

        if (qrCodes.isEmpty()) {
            throw new RegraNegocioException(
                    "Nenhum código de rastreamento encontrado para o pedido: " + idPedido);
        }

        List<QRCodeResponseDTO> dtos = qrCodes.stream()
                .map(QRCodeResponseDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * 📥 GET /rastreamento/{codigo}/download
     * Faz download do arquivo PNG do QR Code
     */
    @GetMapping("/{codigo}/download")
    public ResponseEntity<Resource> downloadQRCode(@PathVariable String codigo) {
        
        QRCodeRastreamento qrCode = qrCodeRepository.findByCodigoRastreamento(codigo)
                .orElseThrow(() -> new RegraNegocioException(
                        "Código de rastreamento não encontrado: " + codigo));

        try {
            // Lê o arquivo físico
            Path caminhoArquivo = Paths.get(qrCode.getQrcodeCaminhoArquivo());
            
            if (!Files.exists(caminhoArquivo)) {
                throw new RegraNegocioException(
                        "Arquivo do QR Code não encontrado no servidor.");
            }

            byte[] bytes = Files.readAllBytes(caminhoArquivo);
            ByteArrayResource resource = new ByteArrayResource(bytes);

            // Define headers para download
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=" + codigo + ".png");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(bytes.length)
                    .contentType(MediaType.IMAGE_PNG)
                    .body(resource);

        } catch (IOException e) {
            throw new RegraNegocioException(
                    "Erro ao ler arquivo do QR Code: " + e.getMessage());
        }
    }

    /**
     * 📄 GET /rastreamento/{codigo}/conteudo
     * Retorna o conteúdo de texto completo do QR Code (para debug/visualização)
     */
    @GetMapping("/{codigo}/conteudo")
    public ResponseEntity<String> visualizarConteudo(@PathVariable String codigo) {
        
        QRCodeRastreamento qrCode = qrCodeRepository.findByCodigoRastreamento(codigo)
                .orElseThrow(() -> new RegraNegocioException(
                        "Código de rastreamento não encontrado: " + codigo));

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(qrCode.getConteudoQRCode());
    }
}
