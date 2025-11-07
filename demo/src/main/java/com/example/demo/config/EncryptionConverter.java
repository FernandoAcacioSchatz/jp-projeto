package com.example.demo.config;

import com.example.demo.service.EncryptionService;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Converter
@Component
public class EncryptionConverter implements AttributeConverter<String, String> {

    private static EncryptionService encryptionService;

    @Autowired
    public void setEncryptionService(EncryptionService service) {
        EncryptionConverter.encryptionService = service;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }
        
        if (encryptionService != null && encryptionService.isEncrypted(attribute)) {
            return attribute;
        }
        
        return encryptionService != null ? encryptionService.encrypt(attribute) : attribute;
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        if (encryptionService != null && !encryptionService.isEncrypted(dbData)) {
            return dbData;
        }
        
        try {
            return encryptionService != null ? encryptionService.decrypt(dbData) : dbData;
        } catch (Exception e) {
            return dbData;
        }
    }
}
