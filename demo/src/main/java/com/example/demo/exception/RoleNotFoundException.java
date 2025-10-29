package com.example.demo.exception;

/**
 * Exception lançada quando uma Role não é encontrada no banco de dados.
 * Indica um problema de configuração do sistema.
 */
public class RoleNotFoundException extends RuntimeException {

    public RoleNotFoundException(String message) {
        super(message);
    }

    public RoleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Cria uma mensagem detalhada para Role não encontrada
     * 
     * @param roleName Nome da role que não foi encontrada
     * @return Exception com mensagem formatada
     */
    public static RoleNotFoundException forRole(String roleName) {
        String message = String.format(
            "⚠️ ERRO DE CONFIGURAÇÃO: Role '%s' não encontrada no banco de dados. " +
            "Execute o script de inicialização: " +
            "INSERT INTO tb_roles (nome_papel) VALUES ('%s'); " +
            "Para mais detalhes, consulte o arquivo 'insert-roles.sql'.",
            roleName, roleName
        );
        return new RoleNotFoundException(message);
    }
}
