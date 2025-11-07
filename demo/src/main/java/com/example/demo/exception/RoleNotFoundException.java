package com.example.demo.exception;


public class RoleNotFoundException extends RuntimeException {

    public RoleNotFoundException(String message) {
        super(message);
    }

    public RoleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    
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
