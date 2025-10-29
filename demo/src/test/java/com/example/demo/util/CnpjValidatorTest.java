package com.example.demo.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Testes unitários para o CnpjValidator
 */
class CnpjValidatorTest {

    @Test
    void testCnpjValido() {
        // CNPJs válidos
        assertTrue(CnpjValidator.isValid("11222333000181"));
        assertTrue(CnpjValidator.isValid("11.222.333/0001-81"));
        assertTrue(CnpjValidator.isValid("06990590000123"));
        assertTrue(CnpjValidator.isValid("06.990.590/0001-23"));
    }

    @Test
    void testCnpjInvalido() {
        // CNPJ com dígito verificador errado
        assertFalse(CnpjValidator.isValid("11222333000180"));
        assertFalse(CnpjValidator.isValid("11.222.333/0001-80"));
        
        // CNPJ com todos os dígitos iguais
        assertFalse(CnpjValidator.isValid("11111111111111"));
        assertFalse(CnpjValidator.isValid("00000000000000"));
        assertFalse(CnpjValidator.isValid("11.111.111/1111-11"));
    }

    @Test
    void testCnpjComTamanhoInvalido() {
        assertFalse(CnpjValidator.isValid("123"));
        assertFalse(CnpjValidator.isValid("123456789012345678"));
    }

    @Test
    void testCnpjNuloOuVazio() {
        assertFalse(CnpjValidator.isValid(null));
        assertFalse(CnpjValidator.isValid(""));
        assertFalse(CnpjValidator.isValid("   "));
    }

    @Test
    void testFormatacao() {
        assertEquals("11.222.333/0001-81", CnpjValidator.format("11222333000181"));
        assertEquals("06.990.590/0001-23", CnpjValidator.format("06990590000123"));
    }

    @Test
    void testRemoverFormatacao() {
        assertEquals("11222333000181", CnpjValidator.removeFormat("11.222.333/0001-81"));
        assertEquals("06990590000123", CnpjValidator.removeFormat("06.990.590/0001-23"));
        assertEquals("11222333000181", CnpjValidator.removeFormat("11222333000181"));
    }
}
