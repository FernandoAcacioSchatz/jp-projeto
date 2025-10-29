# 🔧 Guia de Configuração Inicial do Sistema

## ⚠️ Erro Comum: "Role não encontrada no banco de dados"

Se você receber este erro ao tentar criar um Cliente ou Fornecedor:

```json
{
  "timestamp": "2025-10-29T11:05:29.3275831",
  "status": 500,
  "error": "Erro de Configuração do Sistema",
  "message": "⚠️ ERRO DE CONFIGURAÇÃO: Role 'ROLE_CLIENTE' não encontrada no banco de dados. Execute o script de inicialização: INSERT INTO tb_roles (nome_papel) VALUES ('ROLE_CLIENTE'); Para mais detalhes, consulte o arquivo 'insert-roles.sql'.",
  "path": "/cliente"
}
```

**Isso significa que você esqueceu de executar o script de inicialização das roles!**

---

## ✅ Solução Passo a Passo

### 1️⃣ Certifique-se de que a aplicação está rodando
A aplicação precisa subir pelo menos uma vez para criar as tabelas no banco de dados.

### 2️⃣ Abra o phpMyAdmin
Acesse: http://localhost/phpmyadmin

### 3️⃣ Execute o script de roles
Clique em **SQL** e cole o seguinte comando:

```sql
USE backend;

INSERT INTO tb_roles (nome_papel) VALUES ('ROLE_CLIENTE');
INSERT INTO tb_roles (nome_papel) VALUES ('ROLE_FORNECEDOR');
INSERT INTO tb_roles (nome_papel) VALUES ('ROLE_ADMIN');

-- Verifica se foram inseridas
SELECT * FROM tb_roles;
```

### 4️⃣ Teste novamente no Postman
Agora você pode criar clientes e fornecedores normalmente!

---

## 📝 Por que isso é necessário?

O sistema usa **roles (papéis)** para controlar permissões:

- **ROLE_CLIENTE**: Usuários que compram produtos
- **ROLE_FORNECEDOR**: Usuários que vendem produtos
- **ROLE_ADMIN**: Administradores do sistema

Essas roles são **obrigatórias** e devem ser inseridas manualmente no banco de dados antes de criar usuários.

---

## 🚀 Ordem correta de inicialização

1. ✅ Criar o banco de dados (`backend` ou `jpsenac`)
2. ✅ Iniciar a aplicação Spring Boot (cria as tabelas)
3. ✅ Executar `insert-roles.sql` (insere as roles)
4. ✅ Testar no Postman (criar clientes/fornecedores)

---

## 💡 Dica

Se você recriar o banco de dados do zero, **sempre lembre-se de executar o script de roles novamente!**
