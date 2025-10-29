package com.example.demo.validation;

import com.example.demo.util.CpfValidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Implementação do validador de CPF
 */
public class CpfValidatorConstraint implements ConstraintValidator<ValidCpf, String> {

    @Override
    public void initialize(ValidCpf constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        if (cpf == null || cpf.isBlank()) {
            return true; // @NotBlank deve tratar valores nulos/vazios
        }
        
        return CpfValidator.isValid(cpf);
    }
}
