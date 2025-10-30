package com.example.demo.dto;

import com.example.demo.model.BandeiraCartao;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * DTO para cadastrar novo cartão
 * 
 * ⚠️ IMPORTANTE: Este DTO recebe os últimos 4 dígitos para mascarar.
 * Em produção, use gateway de pagamento (Stripe, PagSeguro) e não trafegue dados de cartão!
 */
public record CartaoRequestDTO(

        @NotBlank(message = "Os últimos 4 dígitos do cartão são obrigatórios")
        @Pattern(regexp = "\\d{4}", message = "Informe apenas os últimos 4 dígitos numéricos")
        String ultimos4Digitos,

        @NotNull(message = "A bandeira do cartão é obrigatória")
        BandeiraCartao bandeira,

        @NotBlank(message = "O nome do titular é obrigatório")
        String nomeTitular,

        @NotNull(message = "O mês de validade é obrigatório")
        @Min(value = 1, message = "Mês deve ser entre 1 e 12")
        @Max(value = 12, message = "Mês deve ser entre 1 e 12")
        Integer mesValidade,

        @NotNull(message = "O ano de validade é obrigatório")
        @Min(value = 2024, message = "Ano inválido")
        Integer anoValidade,

        String apelido
) {
}
