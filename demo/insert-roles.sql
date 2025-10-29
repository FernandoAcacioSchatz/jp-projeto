-- ===================================================================
-- SCRIPT DE INSERÇÃO DAS ROLES OBRIGATÓRIAS
-- ===================================================================
-- Execute este script APÓS a aplicação Spring Boot subir pela primeira vez
-- e o Hibernate criar todas as tabelas automaticamente.
-- 
-- IMPORTANTE: Este script é OBRIGATÓRIO para o funcionamento do sistema!
-- Sem estas roles, você receberá o erro:
-- "⚠️ ERRO DE CONFIGURAÇÃO: Role 'ROLE_CLIENTE' não encontrada no banco de dados"
-- ===================================================================

USE backend;

-- Insere as 3 roles necessárias para o sistema funcionar
INSERT INTO tb_roles (nome_papel) VALUES ('ROLE_CLIENTE');
INSERT INTO tb_roles (nome_papel) VALUES ('ROLE_FORNECEDOR');
INSERT INTO tb_roles (nome_papel) VALUES ('ROLE_ADMIN');

-- Verifica se foram inseridas corretamente
SELECT * FROM tb_roles;
