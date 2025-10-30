package com.example.demo.config;

import com.example.demo.service.EncryptionService;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Converter JPA para criptografar/descriptografar dados automaticamente
 * ao salvar/carregar do banco de dados.
 * 
 * Usado para campos sensíveis como números de cartão (mesmo mascarados).
 */
@Converter
@Component
public class EncryptionConverter implements AttributeConverter<String, String> {

    private static EncryptionService encryptionService;

    /**
     * Injeta o EncryptionService via setter para uso estático.
     * Necessário porque JPA cria instâncias do Converter diretamente.
     */
    @Autowired
    public void setEncryptionService(EncryptionService service) {
        EncryptionConverter.encryptionService = service;
    }

    /**
     * Converte atributo da entidade para valor do banco (CRIPTOGRAFA).
     * 
     * @param attribute Valor em claro da entidade
     * @return Valor criptografado para salvar no banco
     */
    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }
        
        // Se já estiver criptografado, não criptografa novamente
        if (encryptionService != null && encryptionService.isEncrypted(attribute)) {
            return attribute;
        }
        
        // Criptografa antes de salvar
        return encryptionService != null ? encryptionService.encrypt(attribute) : attribute;
    }

    /**
     * Converte valor do banco para atributo da entidade (DESCRIPTOGRAFA).
     * 
     * @param dbData Valor criptografado do banco
     * @return Valor em claro para usar na entidade
     */
    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        // Se não parece criptografado, retorna como está
        if (encryptionService != null && !encryptionService.isEncrypted(dbData)) {
            return dbData;
        }
        
        // Descriptografa ao carregar do banco
        try {
            return encryptionService != null ? encryptionService.decrypt(dbData) : dbData;
        } catch (Exception e) {
            // Se falhar a descriptografia, retorna o valor original
            // Pode acontecer se a chave mudou ou dados não estavam criptografados
            return dbData;
        }
    }
}
