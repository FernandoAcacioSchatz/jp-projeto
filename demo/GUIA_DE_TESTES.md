# 🧪 Guia de Testes - API REST

Este guia contém exemplos práticos para testar todas as melhorias implementadas na API.

---

## 📋 Pré-requisitos

- Aplicação rodando em `http://localhost:8080`
- Ferramenta para testar API (Postman, Insomnia, cURL, etc.)
- Banco de dados configurado

---

## 1️⃣ Testar Global Exception Handler

### ✅ Teste 1: CPF Inválido (Bean Validation)

**Request:**
```http
POST http://localhost:8080/cliente
Content-Type: application/json

{
  "nomeCliente": "João Silva",
  "email": "joao@email.com",
  "senha": "senha123456",
  "telefone": "(11) 98765-4321",
  "cpf": "111.111.111-11"
}
```

**Resposta Esperada: 400 Bad Request**
```json
{
  "timestamp": "2025-10-29T14:30:00",
  "status": 400,
  "error": "Erro de CPF",
  "message": "CPF inválido. Verifique os dígitos informados.",
  "path": "/cliente"
}
```

### ✅ Teste 2: Email Duplicado

**Request:**
```http
POST http://localhost:8080/cliente
Content-Type: application/json

{
  "nomeCliente": "Maria Santos",
  "email": "joao@email.com",  // Email já cadastrado
  "senha": "senha123456",
  "telefone": "(11) 98765-4321",
  "cpf": "123.456.789-09"
}
```

**Resposta Esperada: 400 Bad Request**
```json
{
  "timestamp": "2025-10-29T14:30:00",
  "status": 400,
  "error": "Erro de Email",
  "message": "Email já cadastrado no sistema.",
  "path": "/cliente"
}
```

### ✅ Teste 3: Cliente Não Encontrado

**Request:**
```http
GET http://localhost:8080/cliente/99999
```

**Resposta Esperada: 404 Not Found**
```json
{
  "timestamp": "2025-10-29T14:30:00",
  "status": 404,
  "error": "Recurso não encontrado",
  "message": "Cliente 99999 não encontrado! Tipo: com.example.demo.model.Cliente",
  "path": "/cliente/99999"
}
```

---

## 2️⃣ Testar Auditoria (createdAt/updatedAt)

### ✅ Teste 1: Criar Cliente e Verificar Auditoria

**Passo 1 - Criar:**
```http
POST http://localhost:8080/cliente
Content-Type: application/json

{
  "nomeCliente": "Pedro Oliveira",
  "email": "pedro@email.com",
  "senha": "senha123456",
  "telefone": "(11) 98765-4321",
  "cpf": "111.444.777-35"
}
```

**Passo 2 - Consultar:**
```http
GET http://localhost:8080/cliente/1
```

**Resposta Esperada: 200 OK**
```json
{
  "idCliente": 1,
  "nomeCliente": "Pedro Oliveira",
  "email": "pedro@email.com",
  "telefone": "(11) 98765-4321",
  "cpf": "11144477735",
  "createdAt": "2025-10-29T14:30:00",  // ✅ Preenchido automaticamente
  "updatedAt": "2025-10-29T14:30:00",  // ✅ Preenchido automaticamente
  "deletedAt": null
}
```

### ✅ Teste 2: Atualizar e Verificar updatedAt

**Passo 1 - Atualizar:**
```http
PUT http://localhost:8080/cliente/1
Content-Type: application/json

{
  "nomeCliente": "Pedro Oliveira Santos",
  "email": "pedro@email.com",
  "telefone": "(11) 99999-9999"
}
```

**Passo 2 - Consultar novamente:**
```http
GET http://localhost:8080/cliente/1
```

**Resposta Esperada:**
```json
{
  "idCliente": 1,
  "nomeCliente": "Pedro Oliveira Santos",
  "createdAt": "2025-10-29T14:30:00",  // ✅ Mantém data original
  "updatedAt": "2025-10-29T15:45:00",  // ✅ Atualizado automaticamente
  "deletedAt": null
}
```

---

## 3️⃣ Testar Soft Delete

### ✅ Teste: Deletar Cliente (Soft Delete)

**Passo 1 - Deletar:**
```http
DELETE http://localhost:8080/cliente/1
```

**Resposta Esperada: 204 No Content**

**Passo 2 - Verificar no Banco:**
```sql
SELECT id_cliente, nome_cliente, deleted_at 
FROM tb_clientes 
WHERE id_cliente = 1;
```

**Resultado Esperado:**
```
id_cliente | nome_cliente        | deleted_at
-----------|---------------------|-------------------
1          | Pedro Oliveira      | 2025-10-29 16:00:00
```

✅ O registro **não foi removido**, apenas marcado como deletado!

**Passo 3 - Tentar consultar (opcional):**
```http
GET http://localhost:8080/cliente/1
```

**Resposta:** Ainda retorna o cliente (para filtrar deletados, precisa implementar filtro no repositório)

---

## 4️⃣ Testar Paginação

### ✅ Teste 1: Listagem Básica Paginada

**Request:**
```http
GET http://localhost:8080/cliente/paginado
```

**Resposta Esperada: 200 OK**
```json
{
  "content": [
    { "idCliente": 1, "nomeCliente": "Cliente 1" },
    { "idCliente": 2, "nomeCliente": "Cliente 2" },
    ...
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": true,
      "unsorted": false
    }
  },
  "totalPages": 3,
  "totalElements": 25,
  "first": true,
  "last": false,
  "size": 10,
  "number": 0,
  "numberOfElements": 10
}
```

### ✅ Teste 2: Paginação Customizada

**Request:**
```http
GET http://localhost:8080/cliente/paginado?page=1&size=5&sort=nomeCliente,desc
```

**Parâmetros:**
- `page=1` → Segunda página
- `size=5` → 5 itens por página
- `sort=nomeCliente,desc` → Ordenado por nome descendente

### ✅ Teste 3: Paginação de Produtos

**Request:**
```http
GET http://localhost:8080/produto/paginado?page=0&size=20&sort=preco,asc
```

**Resposta:** Lista produtos ordenados por preço crescente

---

## 5️⃣ Testar Alteração de Senha

### ✅ Teste 1: Alteração de Senha com Sucesso

**Request:**
```http
POST http://localhost:8080/cliente/1/alterar-senha
Content-Type: application/json

{
  "senhaAtual": "senha123456",
  "novaSenha": "novaSenha789",
  "confirmacaoSenha": "novaSenha789"
}
```

**Resposta Esperada: 200 OK**

### ✅ Teste 2: Senha Atual Incorreta

**Request:**
```http
POST http://localhost:8080/cliente/1/alterar-senha
Content-Type: application/json

{
  "senhaAtual": "senhaErrada",
  "novaSenha": "novaSenha789",
  "confirmacaoSenha": "novaSenha789"
}
```

**Resposta Esperada: 400 Bad Request**
```json
{
  "timestamp": "2025-10-29T14:30:00",
  "status": 400,
  "error": "Erro de Regra de Negócio",
  "message": "Senha atual incorreta.",
  "path": "/cliente/1/alterar-senha"
}
```

### ✅ Teste 3: Confirmação de Senha Não Confere

**Request:**
```http
POST http://localhost:8080/cliente/1/alterar-senha
Content-Type: application/json

{
  "senhaAtual": "senha123456",
  "novaSenha": "novaSenha789",
  "confirmacaoSenha": "senhasDiferentes"
}
```

**Resposta Esperada: 400 Bad Request**
```json
{
  "timestamp": "2025-10-29T14:30:00",
  "status": 400,
  "error": "Erro de Regra de Negócio",
  "message": "A nova senha e a confirmação não conferem.",
  "path": "/cliente/1/alterar-senha"
}
```

### ✅ Teste 4: Senha Muito Curta

**Request:**
```http
POST http://localhost:8080/cliente/1/alterar-senha
Content-Type: application/json

{
  "senhaAtual": "senha123456",
  "novaSenha": "123",
  "confirmacaoSenha": "123"
}
```

**Resposta Esperada: 400 Bad Request**
```json
{
  "timestamp": "2025-10-29T14:30:00",
  "status": 400,
  "error": "Erro de validação",
  "message": "{novaSenha=A nova senha deve ter no mínimo 8 caracteres}",
  "path": "/cliente/1/alterar-senha"
}
```

---

## 6️⃣ Testar CRUD Completo de Produto

### ✅ Teste 1: Criar Produto (Fornecedor autenticado)

**Request:**
```http
POST http://localhost:8080/produto
Content-Type: application/json
Authorization: Basic <fornecedor_credentials>

{
  "nome": "Notebook Dell",
  "descricao": "Notebook de alta performance",
  "preco": 3500.00,
  "estoque": 10,
  "idCategoria": 1
}
```

**Resposta Esperada: 201 Created**

### ✅ Teste 2: Listar Produtos Paginado (Público)

**Request:**
```http
GET http://localhost:8080/produto/paginado?page=0&size=10
```

**Resposta Esperada: 200 OK**

### ✅ Teste 3: Atualizar Produto (Apenas fornecedor dono)

**Request:**
```http
PUT http://localhost:8080/produto/1
Content-Type: application/json
Authorization: Basic <fornecedor_dono_credentials>

{
  "nome": "Notebook Dell Inspiron 15",
  "preco": 3200.00,
  "estoque": 15
}
```

**Resposta Esperada: 200 OK**

### ✅ Teste 4: Tentar Atualizar Produto de Outro Fornecedor

**Request:**
```http
PUT http://localhost:8080/produto/1
Content-Type: application/json
Authorization: Basic <outro_fornecedor_credentials>

{
  "preco": 1000.00
}
```

**Resposta Esperada: 400 Bad Request**
```json
{
  "message": "Você não tem permissão para alterar este produto."
}
```

### ✅ Teste 5: Deletar Produto (Soft Delete)

**Request:**
```http
DELETE http://localhost:8080/produto/1
Authorization: Basic <fornecedor_dono_credentials>
```

**Resposta Esperada: 204 No Content**

---

## 📊 Checklist de Testes

Use este checklist para garantir que todas as funcionalidades foram testadas:

### Global Exception Handler
- [ ] CPF inválido retorna erro 400 padronizado
- [ ] CNPJ inválido retorna erro 400 padronizado
- [ ] Email duplicado retorna erro 400 padronizado
- [ ] Recurso não encontrado retorna erro 404
- [ ] Validações de Bean Validation retornam erro 400

### Auditoria
- [ ] createdAt é preenchido automaticamente ao criar
- [ ] updatedAt é atualizado automaticamente ao editar
- [ ] deletedAt é preenchido ao fazer soft delete

### Soft Delete
- [ ] DELETE marca registro como deletado (não remove do banco)
- [ ] deletedAt é preenchido com timestamp
- [ ] Registro ainda pode ser consultado

### Paginação
- [ ] GET /cliente/paginado retorna resposta paginada
- [ ] GET /fornecedor/paginado retorna resposta paginada
- [ ] GET /produto/paginado retorna resposta paginada
- [ ] Parâmetros page, size e sort funcionam corretamente

### Alteração de Senha
- [ ] Senha atual incorreta retorna erro 400
- [ ] Confirmação diferente retorna erro 400
- [ ] Senha muito curta retorna erro 400
- [ ] Alteração com sucesso retorna 200
- [ ] Nova senha é criptografada

### CRUD Produto
- [ ] Apenas fornecedor autenticado pode criar produto
- [ ] Qualquer um pode listar produtos
- [ ] Apenas fornecedor dono pode alterar/deletar produto

---

## 🔧 Ferramentas Recomendadas

### Postman
- Crie uma Collection com todos os testes
- Use Environments para URLs e credenciais
- Configure Pre-request Scripts para tokens

### cURL (Linha de Comando)
```bash
# Criar cliente
curl -X POST http://localhost:8080/cliente \
  -H "Content-Type: application/json" \
  -d '{"nomeCliente":"João","email":"joao@email.com",...}'

# Listar paginado
curl "http://localhost:8080/cliente/paginado?page=0&size=5"

# Alterar senha
curl -X POST http://localhost:8080/cliente/1/alterar-senha \
  -H "Content-Type: application/json" \
  -d '{"senhaAtual":"...","novaSenha":"...","confirmacaoSenha":"..."}'
```

---

## ✅ Testes Automatizados (JUnit)

Para criar testes automatizados, use esta estrutura:

```java
@SpringBootTest
@AutoConfigureMockMvc
class ClienteControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void deveRetornarErroPadronizadoQuandoCpfInvalido() throws Exception {
        mockMvc.perform(post("/cliente")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"cpf\":\"111.111.111-11\",...}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Erro de CPF"));
    }
}
```

---

Bons testes! 🚀
