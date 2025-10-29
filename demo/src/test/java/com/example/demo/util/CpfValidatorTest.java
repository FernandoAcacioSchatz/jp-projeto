package com.example.demo.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Testes unitários para o CpfValidator
 */
class CpfValidatorTest {

    @Test
    void testCpfValido() {
        // CPFs válidos
        assertTrue(CpfValidator.isValid("11144477735"));
        assertTrue(CpfValidator.isValid("111.444.777-35"));
        assertTrue(CpfValidator.isValid("12345678909"));
        assertTrue(CpfValidator.isValid("123.456.789-09"));
    }

    @Test
    void testCpfInvalido() {
        // CPF com dígito verificador errado
        assertFalse(CpfValidator.isValid("11144477736"));
        assertFalse(CpfValidator.isValid("111.444.777-36"));
        
        // CPF com todos os dígitos iguais
        assertFalse(CpfValidator.isValid("11111111111"));
        assertFalse(CpfValidator.isValid("00000000000"));
        assertFalse(CpfValidator.isValid("111.111.111-11"));
    }

    @Test
    void testCpfComTamanhoInvalido() {
        assertFalse(CpfValidator.isValid("123"));
        assertFalse(CpfValidator.isValid("12345678901234"));
    }

    @Test
    void testCpfNuloOuVazio() {
        assertFalse(CpfValidator.isValid(null));
        assertFalse(CpfValidator.isValid(""));
        assertFalse(CpfValidator.isValid("   "));
    }

    @Test
    void testFormatacao() {
        assertEquals("111.444.777-35", CpfValidator.format("11144477735"));
        assertEquals("123.456.789-09", CpfValidator.format("12345678909"));
    }

    @Test
    void testRemoverFormatacao() {
        assertEquals("11144477735", CpfValidator.removeFormat("111.444.777-35"));
        assertEquals("12345678909", CpfValidator.removeFormat("123.456.789-09"));
        assertEquals("12345678909", CpfValidator.removeFormat("12345678909"));
    }
}
