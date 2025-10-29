# Melhorias Implementadas na API REST

## 📋 Visão Geral

Este documento descreve todas as melhorias implementadas no sistema conforme as melhores práticas de desenvolvimento de APIs REST com Spring Boot.

---

## 1️⃣ Global Exception Handler (@ControllerAdvice)

### 📂 Arquivo Criado
- `GlobalExceptionHandler.java`

### ✅ Funcionalidades
Tratamento centralizado de exceções com respostas padronizadas contendo:
- **timestamp**: Data/hora do erro
- **status**: Código HTTP
- **error**: Tipo do erro
- **message**: Mensagem descritiva
- **path**: Endpoint que gerou o erro

### 🎯 Exceções Tratadas
1. **MethodArgumentNotValidException** → Bean Validation (400)
2. **CpfException** → CPF inválido (400)
3. **CnpjException** → CNPJ inválido (400)
4. **EmailException** → Email duplicado/inválido (400)
5. **NoSuchElementException** → Recurso não encontrado (404)
6. **RegraNegocioException** → Regras de negócio (400)
7. **DataIntegrityViolationException** → Integridade de dados (409)
8. **Exception** → Erros genéricos (500)

### 📝 Exemplo de Resposta
```json
{
  "timestamp": "2025-10-29T14:30:00",
  "status": 400,
  "error": "Erro de CPF",
  "message": "CPF inválido. Verifique os dígitos informados.",
  "path": "/cliente"
}
```

---

## 2️⃣ Auditoria com Campos Automáticos

### 📂 Arquivos Criados
- `Auditable.java` (classe base)
- `JpaAuditingConfig.java` (configuração)

### ✅ Funcionalidades
Todas as entidades agora herdam de `Auditable` e possuem:

| Campo | Tipo | Descrição | Automático |
|-------|------|-----------|------------|
| `createdAt` | LocalDateTime | Data de criação | ✅ Sim (@CreatedDate) |
| `updatedAt` | LocalDateTime | Data de atualização | ✅ Sim (@LastModifiedDate) |
| `deletedAt` | LocalDateTime | Data de exclusão (soft delete) | ❌ Manual |

### 📦 Entidades Atualizadas
- ✅ Cliente
- ✅ Fornecedor
- ✅ Produto

### 💡 Métodos Utilitários
```java
boolean isDeleted()      // Verifica se foi deletado
void markAsDeleted()     // Marca como deletado (soft delete)
void restore()           // Restaura registro deletado
```

---

## 3️⃣ Soft Delete (Exclusão Lógica)

### ✅ Implementação
Em vez de deletar fisicamente do banco, os registros são apenas marcados como deletados.

### 🔄 Comportamento

**Antes (Hard Delete):**
```java
cRepository.delete(cliente); // Remove do banco ❌
```

**Agora (Soft Delete):**
```java
cliente.markAsDeleted();      // Apenas marca como deletado ✅
cRepository.save(cliente);    // Mantém no banco
```

### 🎯 Benefícios
1. **Auditoria**: Histórico completo mantido
2. **Recuperação**: Possível restaurar registros
3. **Integridade**: Mantém referências de chaves estrangeiras
4. **Compliance**: Atende requisitos legais

### 📌 Nota
Para exclusão física, basta comentar o soft delete e usar:
```java
cRepository.delete(cliente); // Hard delete
```

---

## 4️⃣ Paginação nos Endpoints

### ✅ Novos Endpoints Criados

#### Clientes
```
GET /cliente          → Lista todos (sem paginação)
GET /cliente/paginado → Lista com paginação
```

#### Fornecedores
```
GET /fornecedor          → Lista todos (sem paginação)
GET /fornecedor/paginado → Lista com paginação
```

#### Produtos
```
GET /produto          → Lista todos (sem paginação)
GET /produto/paginado → Lista com paginação
```

### 📝 Exemplos de Uso

**Básico (padrão: 10 itens por página)**
```
GET /cliente/paginado
```

**Com parâmetros customizados**
```
GET /cliente/paginado?page=0&size=20&sort=nomeCliente,asc
GET /produto/paginado?page=1&size=15&sort=preco,desc
GET /fornecedor/paginado?page=2&size=5&sort=nome,asc
```

### 📊 Resposta Paginada
```json
{
  "content": [
    { "id": 1, "nome": "Cliente 1" },
    { "id": 2, "nome": "Cliente 2" }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": { "sorted": true }
  },
  "totalPages": 5,
  "totalElements": 50,
  "first": true,
  "last": false,
  "size": 10,
  "number": 0
}
```

### 🎯 Parâmetros Disponíveis

| Parâmetro | Descrição | Exemplo |
|-----------|-----------|---------|
| `page` | Número da página (inicia em 0) | `page=0` |
| `size` | Tamanho da página | `size=20` |
| `sort` | Campo e direção da ordenação | `sort=nome,asc` |

---

## 5️⃣ Endpoint de Alteração de Senha

### 📂 Arquivo Criado
- `AlterarSenhaDTO.java`

### ✅ Novos Endpoints

**Cliente:**
```
POST /cliente/{idCliente}/alterar-senha
```

**Fornecedor:**
```
POST /fornecedor/{idFornecedor}/alterar-senha
```

### 📝 Request Body
```json
{
  "senhaAtual": "senha123456",
  "novaSenha": "novaSenha12345",
  "confirmacaoSenha": "novaSenha12345"
}
```

### 🔐 Validações Aplicadas

1. ✅ **Senha atual obrigatória**
2. ✅ **Nova senha obrigatória** (mínimo 8 caracteres)
3. ✅ **Confirmação obrigatória**
4. ✅ **Senhas devem conferir** (novaSenha == confirmacaoSenha)
5. ✅ **Senha atual deve estar correta** (verifica no banco)
6. ✅ **Nova senha é criptografada** (BCrypt)

### 🚫 Erros Possíveis

| Erro | Status | Mensagem |
|------|--------|----------|
| Campos vazios | 400 | "A senha atual é obrigatória" |
| Senha curta | 400 | "A nova senha deve ter no mínimo 8 caracteres" |
| Senhas não conferem | 400 | "A nova senha e a confirmação não conferem" |
| Senha atual errada | 400 | "Senha atual incorreta" |
| Cliente não encontrado | 404 | "Cliente X não encontrado!" |

### 💡 Exemplo de Uso com cURL
```bash
curl -X POST http://localhost:8080/cliente/1/alterar-senha \
  -H "Content-Type: application/json" \
  -d '{
    "senhaAtual": "senha123456",
    "novaSenha": "novaSenha12345",
    "confirmacaoSenha": "novaSenha12345"
  }'
```

---

## 6️⃣ Melhorias no SecurityConfig

### ✅ Endpoints Públicos Configurados

```java
// Permite criar cliente e fornecedor sem autenticação
POST /cliente       → permitAll()
POST /fornecedor    → permitAll()

// Permite visualizar produtos sem autenticação
GET /produto        → permitAll()
GET /produto/**     → permitAll()

// Permite acesso à página de login
GET /login          → permitAll()

// Todos os outros endpoints requerem autenticação
anyRequest()        → authenticated()
```

---

## 📊 Resumo das Melhorias

| Melhoria | Benefício | Status |
|----------|-----------|--------|
| Global Exception Handler | Respostas de erro padronizadas | ✅ Implementado |
| Auditoria (createdAt/updatedAt) | Rastreabilidade automática | ✅ Implementado |
| Soft Delete | Exclusão lógica reversível | ✅ Implementado |
| Paginação | Performance em grandes volumes | ✅ Implementado |
| Endpoint de Alteração de Senha | Segurança com validação | ✅ Implementado |

---

## 🚀 Como Testar

### 1. Testar Exception Handler
```bash
# CPF inválido
POST /cliente
{
  "cpf": "111.111.111-11"  # Retorna erro 400 padronizado
}
```

### 2. Testar Auditoria
```bash
# Criar cliente
POST /cliente { ... }

# Consultar cliente - verá createdAt e updatedAt
GET /cliente/1
```

### 3. Testar Soft Delete
```bash
# Deletar cliente
DELETE /cliente/1

# Verificar no banco - deletedAt estará preenchido
```

### 4. Testar Paginação
```bash
# Listar com paginação
GET /cliente/paginado?page=0&size=5&sort=nomeCliente,asc
```

### 5. Testar Alteração de Senha
```bash
POST /cliente/1/alterar-senha
{
  "senhaAtual": "senha123456",
  "novaSenha": "novaSenha789",
  "confirmacaoSenha": "novaSenha789"
}
```

---

## 📌 Próximos Passos Sugeridos

1. **Implementar filtro para soft delete** nos repositórios
2. **Adicionar endpoint de restauração** de registros deletados
3. **Criar DTO de resposta** incluindo campos de auditoria
4. **Implementar cache** para endpoints de leitura frequente
5. **Adicionar documentação Swagger/OpenAPI**
6. **Implementar rate limiting** para proteção contra abuso

---

## 🎯 Conclusão

Todas as melhorias foram implementadas seguindo as melhores práticas do Spring Boot e padrões REST. A API agora está mais robusta, segura, performática e fácil de manter!
