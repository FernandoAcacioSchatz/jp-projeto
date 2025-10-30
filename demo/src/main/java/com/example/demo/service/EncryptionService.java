package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Serviço de criptografia AES-256 para dados sensíveis.
 * Usado principalmente para criptografar números de cartão mascarados.
 */
@Service
public class EncryptionService {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    @Value("${encryption.key:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String encryptionKey;

    /**
     * Criptografa uma string usando AES-256.
     * 
     * @param plainText Texto em claro a ser criptografado
     * @return Texto criptografado em Base64
     * @throws RuntimeException se houver erro na criptografia
     */
    public String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }

        try {
            SecretKey key = generateKey();
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar dados: " + e.getMessage(), e);
        }
    }

    /**
     * Descriptografa uma string criptografada com AES-256.
     * 
     * @param encryptedText Texto criptografado em Base64
     * @return Texto em claro descriptografado
     * @throws RuntimeException se houver erro na descriptografia
     */
    public String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText;
        }

        try {
            SecretKey key = generateKey();
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao descriptografar dados: " + e.getMessage(), e);
        }
    }

    /**
     * Gera a chave secreta a partir da string de configuração.
     * 
     * @return SecretKey para uso com AES
     */
    private SecretKey generateKey() {
        try {
            // Decodifica a chave de Base64
            byte[] decodedKey = Base64.getDecoder().decode(encryptionKey);
            
            // Garante que temos exatamente 32 bytes (256 bits) para AES-256
            byte[] key = new byte[32];
            System.arraycopy(decodedKey, 0, key, 0, Math.min(decodedKey.length, 32));
            
            return new SecretKeySpec(key, ALGORITHM);
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar chave de criptografia: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica se uma string está criptografada (formato Base64 válido).
     * 
     * @param text Texto a verificar
     * @return true se parecer estar criptografado
     */
    public boolean isEncrypted(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        try {
            Base64.getDecoder().decode(text);
            // Se não lançou exceção, pode ser Base64 válido
            // Mas não garante que seja criptografado por nós
            return text.length() > 16 && !text.contains(" ");
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
