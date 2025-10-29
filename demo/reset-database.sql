-- ===================================================================
-- SCRIPT DE RECRIAÇÃO COMPLETA DO BANCO DE DADOS
-- ===================================================================
-- ATENÇÃO: Este script irá APAGAR TODOS OS DADOS EXISTENTES!
-- Execute apenas se tiver certeza do que está fazendo.
-- ===================================================================

-- 1. Apaga o banco se ele existir
DROP DATABASE IF EXISTS jpsenac;

-- 2. Cria o banco novamente
CREATE DATABASE jpsenac CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 3. Seleciona o banco
USE jpsenac;

-- ===================================================================
-- INSERÇÃO DE DADOS ESSENCIAIS (ROLES)
-- ===================================================================
-- Estas roles serão criadas automaticamente pelo Hibernate,
-- mas vamos inserir os dados essenciais após a aplicação criar as tabelas.

-- Execute estes comandos APÓS a aplicação subir pela primeira vez:
/*
INSERT INTO tb_roles (nome_papel) VALUES ('ROLE_CLIENTE');
INSERT INTO tb_roles (nome_papel) VALUES ('ROLE_FORNECEDOR');
INSERT INTO tb_roles (nome_papel) VALUES ('ROLE_ADMIN');
*/

-- ===================================================================
-- OBSERVAÇÕES
-- ===================================================================
-- 1. Após executar este script, inicie a aplicação Spring Boot
-- 2. O Hibernate criará todas as tabelas automaticamente (ddl-auto=update)
-- 3. Depois execute os INSERTs das roles (descomente o bloco acima)
-- 4. Teste a aplicação no Postman seguindo o guia
