package com.example.demo.validation;

import com.example.demo.util.CnpjValidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Implementação do validador de CNPJ
 */
public class CnpjValidatorConstraint implements ConstraintValidator<ValidCnpj, String> {

    @Override
    public void initialize(ValidCnpj constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String cnpj, ConstraintValidatorContext context) {
        if (cnpj == null || cnpj.isBlank()) {
            return true; // @NotBlank deve tratar valores nulos/vazios
        }
        
        return CnpjValidator.isValid(cnpj);
    }
}
