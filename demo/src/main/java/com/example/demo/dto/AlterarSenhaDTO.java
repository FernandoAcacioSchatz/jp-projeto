package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para alteração de senha
 */
public record AlterarSenhaDTO(
    
    @NotBlank(message = "A senha atual é obrigatória")
    String senhaAtual,

    @NotBlank(message = "A nova senha é obrigatória")
    @Size(min = 8, message = "A nova senha deve ter no mínimo 8 caracteres")
    String novaSenha,

    @NotBlank(message = "A confirmação da senha é obrigatória")
    String confirmacaoSenha
) {
    /**
     * Valida se a nova senha e a confirmação são iguais
     */
    public boolean senhasConferem() {
        return novaSenha != null && novaSenha.equals(confirmacaoSenha);
    }
}
